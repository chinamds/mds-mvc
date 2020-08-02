package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.MyMessageManager;
import com.mds.aiotplayer.webapp.common.util.MyMessageApi;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.common.exception.RecordExistsException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.sys.model.MessageOperate;
import com.mds.aiotplayer.sys.model.MessageAction;
import com.mds.aiotplayer.sys.model.MessageFolder;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.model.MyMessageContent;
import com.mds.aiotplayer.sys.model.MyMessageReFw;
import com.mds.aiotplayer.sys.model.MyMessageRecipient;
import com.mds.aiotplayer.sys.model.RecipientType;
import com.mds.aiotplayer.sys.model.User;
import com.mds.aiotplayer.webapp.common.controller.BaseCRUDController;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.webapp.sys.bind.annotation.CurrentUser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/sys/messageform*")
public class MyMessageFormController extends BaseCRUDController<MyMessage, Long> {
    private MyMessageManager myMessageManager = null;
    private MyMessageApi messageApi;
    
    @Autowired
    public void setMyMessageApi(MyMessageApi messageApi) {
        this.messageApi = messageApi;
    }

    @Autowired
    public void setMyMessageManager(MyMessageManager myMessageManager) {
        this.myMessageManager = myMessageManager;
    }

    public MyMessageFormController() {
        setCancelView("redirect:messages");
        setSuccessView("redirect:messages");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected MyMessage showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return myMessageManager.get(new Long(id));
        }

        return new MyMessage();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(MyMessage myMessage, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(myMessage, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "messageform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (myMessage.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            myMessageManager.remove(myMessage.getId());
            saveMessage(request, getText("myMessage.deleted", locale));
        } else {
        	if (myMessage.getUser() == null){
        		if (!StringUtils.isBlank(request.getParameter("user_id"))) {
        			myMessage.setUser(getUserManager().get(new Long(request.getParameter("user_id"))));
        		}
        	}
        	if (myMessage.getSender() == null){
        		if (!StringUtils.isBlank(request.getParameter("sender_id"))) {
        			myMessage.setSender(getUserManager().get(new Long(request.getParameter("sender_id"))));
        		}
        	}
            myMessageManager.save(myMessage);
            String key = (isNew) ? "myMessage.added" : "myMessage.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:messageform?id=" + myMessage.getId();
            }
        }

        return success;
    }
    
    @RequestMapping("{id}") //:\\d+
    public String view(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    	MyMessage m = myMessageManager.get(id);
        if (m == null) {
        	saveError(request, I18nUtils.getString("myMessage.missing", request.getLocale()));
        	
            return redirectToUrl("/message/list");
        }

        model.addAttribute("m", m);
        myMessageManager.markRead(UserUtils.getUserId(), m.getId());
        
        //get all reply message and orignal message
        List<MyMessage> messages = myMessageManager.findAncestorsAndDescendants(m);
        model.addAttribute("messages", messages);

        return viewName("view");
    }

    @RequestMapping("{id}/content")
    public String viewContent(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
    	myMessageManager.markRead(UserUtils.getUserId(), id);

        return viewName("viewContent");
    }

    @RequestMapping(value = "/send", method = RequestMethod.GET)
    public String showSendForm(Model model) {
        if (!model.containsAttribute("m")) {
            model.addAttribute("m", newModel());
        }      
        model.addAttribute(Constants.OP_NAME, MessageOperate.newmessage);
        
        return viewName("sendForm");
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String send(
            @ModelAttribute("m") MyMessage message,
            BindingResult result,
            @RequestParam(value = "recipients", required = false) String[] recipients,
            @RequestParam(value = "textcontent", required = false) String textcontent,
            Model model,
            RedirectAttributes redirectAttributes,
            final HttpServletRequest request) throws SQLException {
    	
    	message.getContent().setContent(textcontent);
    	if (recipients != null && recipients.length > 0) {
    		List<MyMessageRecipient> messageRecipients =Lists.newArrayList(); 
        	for(String userId : recipients) {
        		messageRecipients.add(new MyMessageRecipient(message, getUserManager().get(new Long(userId))));
        	}
        	
        	message.setMyMessageRecipients(messageRecipients);
    	}else {
    		result.rejectValue("myMessageRecipients", "recipient.not.exists");
    		
    		return showSendForm(model);
    	}
    	
    	if (validator != null) { // validator is null during testing
            validator.validate(message, result);

            if (result.hasErrors()) {
            	return showSendForm(model);
            }
        }
    	
    	/*if (message.getMyMessageRecipients() != null) {
    		List<MyMessageRecipient> messageRecipients =Lists.newArrayList(); 
        	for(MyMessageRecipient messageRecipient : message.getMyMessageRecipients()) {
        		Searchable searchable = Searchable.newSearchable();
        		searchable.addSearchFilter("code", SearchOperator.eq, messageRecipient.getCode());	
        		User user = getUserManager().findAll(searchable));
        		messageRecipients.add(new MyMessageRecipient(message, recipient));
        	}
        	
        	message.setMyMessageRecipients(messageRecipients);
    	}*/
    	
        /*User recipient = getUserManager().getUserByUsername(recipientUsername);
        if (recipient == null) {
            result.rejectValue("recipientId", "recipient.not.exists");
        }
        if (recipient.equals(user)) {
            result.rejectValue("recipientId", "recipient.not.self");
        }

        if (result.hasErrors()) {
            return showSendForm(model);
        }
        message.getMyMessageRecipients().add(new MyMessageRecipient(message, recipient));*/
    	var userAccount = UserUtils.getUser();
    	message.setCurrentUser(userAccount.getUsername());
    	var user = getUserManager().get(userAccount.getId());
        message.setUser(user);
        message.setSender(user);
        if (request.getParameter("draft") != null) {
        	message.setMessageFolder(MessageFolder.drafts);
        	for (MyMessageRecipient recipient : message.getMyMessageRecipients()) {
            	recipient.setMessageFolder(MessageFolder.drafts);
            	recipient.setRecipientType(RecipientType.to);
            }
        	message.getContent().setMyMessage(message);
            
        	try {
				myMessageManager.saveMyMessage(message);
			} catch (RecordExistsException e) {
				myMessageManager.clear();
        		message.setId(null);

                return showSendForm(model);
			}
        	saveMessage(request, getText("myMessage.addedtodraft", request.getLocale()));
        }else {
	        myMessageManager.send(message);
	        messageApi.pushUnreadMessage(message);
	
	        saveMessage(request, getText("myMessage.sent", request.getLocale()));
        }
        //redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, getText("myMessage.sent", request.getLocale()));
        //return redirectToUrl(viewName(MessageFolder.outbox + "/list"));
        return redirectToUrl("sys/myMessages/" + MessageFolder.outbox + "/list");
    }

    @RequestMapping(value = "/{id}/reply", method = RequestMethod.GET)
    public String showReplyForm(@PathVariable("id") Long id, final HttpServletRequest request, Model model) {
        if (!model.containsAttribute("m")) {
        	MyMessage beenReplied = myMessageManager.get(id); 
            MyMessage m = newModel();
            m.addOriginals(Lists.newArrayList(new MyMessageReFw(m, beenReplied)));
            m.getMyMessageRecipients().add(new MyMessageRecipient(m, beenReplied.getSender()));
            m.setTitle(I18nUtils.getString("myMessage.reply.prefix", request.getLocale()) + beenReplied.getTitle());
            model.addAttribute("m", m);
            model.addAttribute("original", beenReplied);
        }
        model.addAttribute(Constants.OP_NAME, MessageOperate.reply);
        
        return viewName("sendForm");
    }

    @RequestMapping(value = "/{id}/reply", method = RequestMethod.POST)
    public String reply(
            @PathVariable("id") Long id,
            @ModelAttribute("m") MyMessage m, BindingResult result,
            @RequestParam(value = "recipients", required = false) String[] recipients,
            @RequestParam(value = "textcontent", required = false) String textcontent,
            final HttpServletRequest request,
            Model model) throws SQLException {
    	
    	m.getContent().setContent(textcontent);
    	if (recipients != null && recipients.length > 0) {
    		List<MyMessageRecipient> messageRecipients =Lists.newArrayList(); 
        	for(String userId : recipients) {
        		messageRecipients.add(new MyMessageRecipient(m, getUserManager().get(new Long(userId))));
        	}
        	m.setMyMessageRecipients(messageRecipients);
    	}else {
    		result.rejectValue("myMessageRecipients", "recipient.not.exists");
    	}
    	
    	if (validator != null) { // validator is null during testing
            validator.validate(m, result);

            if (result.hasErrors()) {
            	return showReplyForm(id, request, model);
            }
        }

    	var user = getUserManager().get(UserUtils.getUser().getId());
        m.setCurrentUser(user.getUsername());
        m.setUser(user);
        m.setSender(user);
        myMessageManager.send(m);
        messageApi.pushUnreadMessage(m);

        saveMessage(request, getText("myMessage.replied", request.getLocale()));
        
        return redirectToUrl("sys/myMessages/" + MessageFolder.inbox + "/list");
    }


    @RequestMapping(value = "/{id}/forward", method = RequestMethod.GET)
    public String showForwardForm( @PathVariable("id") Long id, final HttpServletRequest request, Model model) {
    	MyMessage parent = myMessageManager.get(id); 
    	String recipientUsername = null;
    	for (MyMessageRecipient recipient : parent.getMyMessageRecipients()) {
    		recipientUsername = recipient.getUser().getUsername();
    		break;
        }
        String senderUsername = parent.getUser().getUsername();

        if (!model.containsAttribute("m")) {
        	MyMessage m = newModel();
            m.setTitle(I18nUtils.getString("myMessage.foward.prefix", request.getLocale()) + parent.getTitle());
            m.setContent(new MyMessageContent());
            m.getContent().setContent(
                    I18nUtils.getString("myMessage.foward.template", request.getLocale(),
                            senderUsername,
                            recipientUsername,
                            parent.getTitle(),
                            parent.getContent().getContent()
                    ));
            model.addAttribute("m", m);
        }
        model.addAttribute(Constants.OP_NAME, MessageOperate.forward);
        return viewName("sendForm");
    }

    @RequestMapping(value = "/{id}/forward", method = RequestMethod.POST)
    public String forward(
            @RequestParam(value = "username", required = false) String username,
            @PathVariable("id") Long id,
            @ModelAttribute("m") MyMessage m, BindingResult result,
            final HttpServletRequest request,
            Model model) throws SQLException {
        User recipient = getUserManager().getUserByUsername(username);
        if (recipient == null) {
            result.rejectValue("recipientId", "recipient.not.exists");
        }

        var user = getUserManager().get(UserUtils.getUser().getId());
        if (recipient.equals(user)) {
            result.rejectValue("recipientId", "recipient.not.self");
        }

        if (result.hasErrors()) {
            return showForwardForm(id, request, model);
        }
        m.getMyMessageRecipients().add(new MyMessageRecipient(m, recipient));
        
        m.setCurrentUser(user.getUsername());
        m.setUser(user);
        m.setSender(user);
        myMessageManager.send(m);
        messageApi.pushUnreadMessage(m);

        saveMessage(request, getText("myMessage.forwarded", request.getLocale()));
        
        return redirectToUrl(viewName(MessageFolder.inbox + "/list"));
    }


    @RequestMapping(value = "draft/save", method = RequestMethod.POST)
    public String saveDraft(
            @RequestParam(value = "username", required = false) String username,
            @ModelAttribute("m") MyMessage m,
            final HttpServletRequest request) {

    	var user = getUserManager().get(UserUtils.getUserId());
        User recipient = getUserManager().getUserByUsername(username);
        if (recipient != null) {
            m.getMyMessageRecipients().add(new MyMessageRecipient(m, recipient));
        }
        m.setUser(user);

        messageApi.saveDraft(m);

        saveMessage(request, getText("myMessage.addedtodraft", request.getLocale()));
        
        return redirectToUrl(viewName(MessageFolder.drafts + "/list"));
    }


    @RequestMapping(value = "draft/{m}/send", method = RequestMethod.GET)
    public String showResendDraftForm(@PathVariable("m") MyMessage m, Model model, final HttpServletRequest request) {
        if (m.getMyMessageRecipients().size() > 0) {
            User user = m.getMyMessageRecipients().get(0).getUser();
            if (user != null) {
                model.addAttribute("username", user.getUsername());
            }
        }
        model.addAttribute("m", m);
        String viewName = showSendForm(model);
        model.addAttribute(Constants.OP_NAME, MessageOperate.drafts);
        
        return viewName;
    }

    @RequestMapping(value = "draft/{m}/send", method = RequestMethod.POST)
    public String resendDraft(
            @ModelAttribute("m") MyMessage m,
            BindingResult result,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "contents", required = false) String contents,
            Model model, RedirectAttributes redirectAttributes,
            final HttpServletRequest request) throws SQLException {

        String viewName = send(m, result, new String[] {username}, contents, model, redirectAttributes, request);
        model.addAttribute(Constants.OP_NAME, MessageOperate.drafts);
        return viewName;
    }
    
    @RequestMapping(value = "draft/{m}/discard", method = RequestMethod.POST)
    public String discardDraft(
            @ModelAttribute("m") MyMessage m,
            BindingResult result,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "contents", required = false) String contents,
            Model model, RedirectAttributes redirectAttributes,
            final HttpServletRequest request) throws SQLException {

        String viewName = send(m, result, new String[] {username}, contents, model, redirectAttributes, request);
        model.addAttribute(Constants.OP_NAME, MessageOperate.drafts);
        return viewName;
    }

    @RequestMapping("batch/archive")
    public String batchStore(
            @RequestParam(value = "ids", required = false) Long[] ids,
            final HttpServletRequest request) {

    	//var user = getUserManager().get(UserUtils.getUserId());
    	myMessageManager.archive(UserUtils.getUserId(), ids);
        saveMessage(request, getText("myMessage.archived", request.getLocale()));
        
        return redirectToUrl(viewName(MessageFolder.archive + "/list"));
    }

    @RequestMapping("batch/move")
    public String batchMove(
            @RequestParam(value = "ids", required = false) Long[] ids,
            final HttpServletRequest request) {

        messageApi.recycle(UserUtils.getUserId(), ids);

        //redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "移动到垃圾箱成功！");
        saveMessage(request, getText("myMessage.deleted", request.getLocale()));
        
        return redirectToUrl(viewName(MessageFolder.junk + "/list"));
    }

    @RequestMapping("batch/delete")
    public String batchDelete(
            @RequestParam(value = "ids", required = false) Long[] ids,
            final HttpServletRequest request) {

        messageApi.delete(UserUtils.getUserId(), ids);

        //redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "删除成功！");
        saveMessage(request, getText("myMessage.deleted", request.getLocale()));
        
        return redirectToUrl(viewName(MessageFolder.junk + "/list"));
    }


    @RequestMapping("clear/{state}")
    public String clear(
            @PathVariable("state") MessageFolder state,
            final HttpServletRequest request) {

        messageApi.clearBox(UserUtils.getUserId(), state);

        //redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, String.format("清空%s成功！", state.getInfo()));
        saveMessage(request, I18nUtils.getString("myMessage.emptiedfolder", request.getLocale(), state.getInfo()));
        
        return redirectToUrl(viewName(MessageFolder.junk + "/list"));
    }

    @RequestMapping("markRead")
    public String markRead(
            @RequestParam(value = "ids", required = false) Long[] ids,
            @RequestParam("BackURL") String backURL,
            final HttpServletRequest request) {

        messageApi.markRead(UserUtils.getUserId(), ids);

        //redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "成功标记为已读！");
        saveMessage(request, getText("myMessage.markasread", request.getLocale()));
        
        return redirectToUrl(backURL);

    }


    @RequestMapping(value = "/unreadCount")
    @ResponseBody
    public String unreadCount() {
        return String.valueOf(messageApi.countUnread(UserUtils.getUserId()));
    }
}

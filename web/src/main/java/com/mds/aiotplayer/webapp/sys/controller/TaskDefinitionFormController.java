package com.mds.aiotplayer.webapp.sys.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.service.TaskDefinitionManager;
import com.mds.aiotplayer.sys.service.impl.DynamicTaskApi;
import com.mds.aiotplayer.common.Constants;
import com.mds.aiotplayer.sys.model.MyMessage;
import com.mds.aiotplayer.sys.model.TaskDefinition;
import com.mds.aiotplayer.webapp.common.controller.BaseCRUDController;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@RequestMapping("/taskDefinitionform*")
public class TaskDefinitionFormController extends BaseCRUDController<TaskDefinition, Long> {
    private TaskDefinitionManager taskDefinitionManager = null;
    private DynamicTaskApi dynamicTaskApi = null;
    
    @Autowired
    public void setDynamicTaskApi(DynamicTaskApi dynamicTaskApi) {
        this.dynamicTaskApi = dynamicTaskApi;
    }

    @Autowired
    public void setTaskDefinitionManager(TaskDefinitionManager taskDefinitionManager) {
        this.taskDefinitionManager = taskDefinitionManager;
    }

    public TaskDefinitionFormController() {
        setCancelView("redirect:taskDefinitions");
        setSuccessView("redirect:taskDefinitions");
    }

    @ModelAttribute
    @RequestMapping(method = RequestMethod.GET)
    protected TaskDefinition showForm(HttpServletRequest request)
    throws Exception {
        String id = request.getParameter("id");

        if (!StringUtils.isBlank(id)) {
            return taskDefinitionManager.get(new Long(id));
        }

        return new TaskDefinition();
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(TaskDefinition taskDefinition, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(taskDefinition, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "taskDefinitionform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (taskDefinition.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            taskDefinitionManager.remove(taskDefinition.getId());
            saveMessage(request, getText("taskDefinition.deleted", locale));
        } else {
            taskDefinitionManager.save(taskDefinition);
            String key = (isNew) ? "taskDefinition.added" : "taskDefinition.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:taskDefinitionform?id=" + taskDefinition.getId();
            }
        }

        return success;
    }
    
    //@Override
    public String create(Model model, @ModelAttribute("m") TaskDefinition m, BindingResult result, RedirectAttributes redirectAttributes) {       
        if (validator != null) { // validator is null during testing
            validator.validate(m, result);
        }

        if (hasError(m, result)) {
            return "showCreateForm(model)";
        }
        dynamicTaskApi.addTaskDefinition(m);
        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "新增成功");
        
        return redirectToUrl(null);
    }

    //@Override
    public String update(Model model, @ModelAttribute("m") TaskDefinition m, BindingResult result, @RequestParam(value = Constants.BACK_URL, required = false) String backURL, RedirectAttributes redirectAttributes) {
        if (hasError(m, result)) {
            return "showUpdateForm(m, model)";
        }
        dynamicTaskApi.updateTaskDefinition(m);
        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "修改成功");
        return redirectToUrl(backURL);
    }

    @RequestMapping(value = "{id}/delete", method = RequestMethod.POST)
    public String delete(
            @RequestParam(value = "forceTermination") boolean forceTermination,
            @PathVariable("id") TaskDefinition m,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {
        dynamicTaskApi.removeTaskDefinition(forceTermination, m.getId());
        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "删除成功");
        
        return redirectToUrl(backURL);
    }

    @RequestMapping(value = "batch/delete", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteInBatch(
            @RequestParam(value = "forceTermination") boolean forceTermination,
            @RequestParam(value = "ids", required = false) Long[] ids,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {
        dynamicTaskApi.removeTaskDefinition(forceTermination, ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "删除成功");
        
        return redirectToUrl(backURL);
    }


    @RequestMapping("/start")
    public String startTask(
            @RequestParam(value = "ids", required = false) Long[] ids,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {
        dynamicTaskApi.startTask(ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "启动任务成功");
        
        return redirectToUrl(backURL);
    }

    @RequestMapping("/stop")
    public String stopTask(
            @RequestParam(value = "forceTermination") boolean forceTermination,
            @RequestParam(value = "ids", required = false) Long[] ids,
            @RequestParam(value = Constants.BACK_URL, required = false) String backURL,
            RedirectAttributes redirectAttributes) {
        dynamicTaskApi.stopTask(forceTermination, ids);

        redirectAttributes.addFlashAttribute(Constants.MESSAGES_KEY, "停止任务成功");
        
        return redirectToUrl(backURL);
    }
}

/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service.impl;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.sys.dao.UserDao;
import com.mds.aiotplayer.sys.model.RoleType;
import com.mds.aiotplayer.sys.model.User;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.common.service.MailEngine;
import com.mds.aiotplayer.common.service.impl.GenericManagerImpl;
import com.mds.aiotplayer.common.web.jcaptcha.JCaptcha;
import com.mds.aiotplayer.core.CacheItem;
import com.mds.aiotplayer.i18n.util.I18nUtils;
import com.mds.aiotplayer.sys.exception.UserExistsException;
import com.mds.aiotplayer.sys.service.UserManager;
import com.mds.aiotplayer.sys.service.UserService;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserAccount;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.ConvertUtil;
import com.mds.aiotplayer.util.DateUtils;
import com.mds.aiotplayer.util.HelperFunctions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Implementation of UserManager interface.
 *
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 */
@Service("userManager")
@WebService(serviceName = "UserService", endpointInterface = "com.mds.aiotplayer.sys.service.UserService")
public class UserManagerImpl extends GenericManagerImpl<User, Long> implements UserManager, UserService {
    private PasswordEncoder passwordEncoder;
    private UserDao userDao;

    private MailEngine mailEngine;
    private SimpleMailMessage message;
    private PasswordTokenManager passwordTokenManager;

    private String passwordRecoveryTemplate = "passwordRecovery.vm";
    private String passwordUpdatedTemplate = "passwordUpdated.vm";

    @Autowired
    @Qualifier("passwordEncoder")
    public void setPasswordEncoder(final PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Autowired
    public void setUserDao(final UserDao userDao) {
        this.dao = userDao;
        this.userDao = userDao;
    }

    @Autowired(required = false)
    public void setMailEngine(final MailEngine mailEngine) {
        this.mailEngine = mailEngine;
    }

    @Autowired(required = false)
    public void setMailMessage(final SimpleMailMessage message) {
        this.message = message;
    }

    @Autowired(required = false)
    public void setPasswordTokenManager(final PasswordTokenManager passwordTokenManager) {
        this.passwordTokenManager = passwordTokenManager;
    }

    /**
     * Velocity template name to send users a password recovery mail (default
     * passwordRecovery.vm).
     *
     * @param passwordRecoveryTemplate the Velocity template to use (relative to classpath)
     * @see com.mds.aiotplayer.sys.service.MailEngine#sendMessage(org.springframework.mail.SimpleMailMessage, String, java.util.Map)
     */
    public void setPasswordRecoveryTemplate(final String passwordRecoveryTemplate) {
        this.passwordRecoveryTemplate = passwordRecoveryTemplate;
    }

    /**
     * Velocity template name to inform users their password was updated
     * (default passwordUpdated.vm).
     *
     * @param passwordUpdatedTemplate the Velocity template to use (relative to classpath)
     * @see com.mds.aiotplayer.sys.service.MailEngine#sendMessage(org.springframework.mail.SimpleMailMessage, String, java.util.Map)
     */
    public void setPasswordUpdatedTemplate(final String passwordUpdatedTemplate) {
        this.passwordUpdatedTemplate = passwordUpdatedTemplate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(final String userId) {
        return userDao.get(new Long(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers() {
    	log.debug("get all user from db");
        return userDao.getAllDistinct();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public List<User> searchUsers(String searchTerm, Integer limit, Integer offset) {
    	Pageable pageable = PageRequest.of(offset, limit);
       
        return userDao.search(pageable, new String[]{"username", "email"}, searchTerm).getContent();
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> usersSelect2(String searchTerm, Integer limit, Integer offset) {
    	if (StringUtils.isBlank(searchTerm))
    		return null;
    	
    	Pageable pageable = PageRequest.of(offset, limit);
       
        return toSelect2Data(userDao.search(pageable, new String[]{"username", "email"}, searchTerm).getContent());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public User saveUser(final User user) throws UserExistsException {

        /*if (user.getVersion() == null) {
            // if new user, lowercase userId
            user.setUsername(user.getUsername().toLowerCase());
        }*/

        // Get and prepare password management-related artifacts
        boolean passwordChanged = false;
        if (passwordEncoder != null) {
	        // Check whether we have to encrypt (or re-encrypt) the password
	        if (user.getVersion() == null) {
	            // New user, always encrypt
	            passwordChanged = true;
	        } else {
	            // Existing user, check password in DB
	            final String currentPassword = userDao.getUserPassword(user.getId());
	            if (currentPassword == null) {
	                passwordChanged = true;
	            } else {
	            	if (!currentPassword.equals(user.getPassword())) {
	            		passwordChanged = true;
	            	}
	            	/*if (!HelperFunctions.validatePassword(user.getPassword())) {
		            	final String newPassword = HelperFunctions.entryptPassword(user.getPassword());
		                if (!currentPassword.equals(newPassword)) {
		                    passwordChanged = true;
		                }
	            	}*/
	            }
	        }
	
	        // If password was changed (or new user), encrypt it
	        if (passwordChanged) {
	            user.setPassword(passwordEncoder.encode(user.getPassword()));
	        	//user.setPassword(HelperFunctions.entryptPassword(user.getPassword()));
	        	
	        }
        } else {
            log.warn("PasswordEncoder not set, skipping password encryption...");
        }

        try {
            return userDao.saveUser(user);
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername() + "' already exists!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void removeUser(final User user) {
        log.debug("removing user: " + user);
        userDao.remove(user);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
	@Override
    public HashMap<String, Object> usersTable(String searchTerm, Integer limit, Integer offset, HttpServletRequest request) throws InvalidMDSRoleException {
    	Pageable pageable = PageRequest.of(offset/limit, limit);
    	Searchable searchable = Searchable.newSearchable();
        searchable.addSort(Direction.ASC, "username");
        searchable.setPage(pageable);
        UserAccount user = UserUtils.getUser();
        if (!user.isSystem()) {
        	if (UserUtils.hasRoleType(RoleType.oa)) {
        		List<Long> userOrganizationIds = UserUtils.getUserOrganizationIds(user.getUsername());
        		searchable.addSearchFilter("organization.id", SearchOperator.in, userOrganizationIds);
        	}else if (UserUtils.hasRoleType(RoleType.ga)) {
        		GalleryBoCollection galleries = UserUtils.getGalleriesCurrentUserCanAdminister();
        		List<RoleType> roleTypes = RoleType.getRoleTypes("g");
        		List<Long> roles = RoleUtils.getMDSRoles().stream().filter(r->roleTypes.contains(r.getRoleType()) 
        				&& galleries.stream().anyMatch(g->r.getGalleries().contains(g))).map(r->r.getRoleId()).collect(Collectors.toList());
        		List<Long> userIds = userDao.findByRoleIds(roles).stream().map(u->u.getId()).collect(Collectors.toList());
        		searchable.addSearchFilter("id", SearchOperator.in, userIds);
        	}
        }
        
    	Page<User> list =  null;
    	if (StringUtils.isBlank(searchTerm)){
    		list = userDao.find(searchable);
    	}else {
    		list = userDao.search(searchable, searchTerm);
    	}
    	
    	HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
    	resultData.put("total", list.getTotalElements());
		resultData.put("rows", toBootstrapTableData(list.getContent(), request));
		
		return resultData;
    }
    
    /**
   	 * convert user data to select2 format(https://select2.org/data-sources/formats)
   	 * {
   	 *	  "results": [
   	 *	    {
   	 *	      "id": 1,
   	 *	      "text": "Option 1"
   	 *    	},
   	 *	    {
   	 *	      "id": 2,
   	 *	      "text": "Option 2",
   	 *	      "selected": true
   	 *	    },
   	 *	    {
   	 *	      "id": 3,
   	 *	      "text": "Option 3",
   	 *	      "disabled": true
   	 *	    }
   	 *	  ]
   	 *	}
   	 * @param users
   	 * @return
        * @throws Exception 
   	 */
   	private  List<HashMap<String,Object>> toBootstrapTableData(List<User> users, HttpServletRequest request){
   		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
   		for (User u : users) {
   			//user list		
   			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
   			mapData.put("username", u.getUsername());//user name fullName
   			mapData.put("fullName", u.getFullName());//user full name 
   			mapData.put("type", u.getUserType());//user type
   			mapData.put("enabled", u.isEnabled());//user is enabled
   			mapData.put("roles", u.getRoleNames());//user roles
   			//mapData.put("userType", I18nUtils.getUserType(u.getUserType(), request));//user type title
   			mapData.put("organizationCode", u.getOrganizationCode());//organization Code
   			mapData.put("id", u.getId());//user id
   			mapData.put("createdBy", u.getCreatedBy());
   			mapData.put("dateAdded", DateUtils.jsonSerializer(u.getDateAdded()));//create date
   			mapData.put("email", u.getEmail());//
   			list.add(mapData);
   		}
   				
   		return list;
   	}

    /**
     * {@inheritDoc}
     */
   	@Transactional
    @Override
    public void removeUser(final String userIds) throws WebApplicationException{
        log.debug("removing user: " + userIds);
        try {
        	userDao.remove(ConvertUtil.StringtoLongArray(userIds));
        } catch (final Exception e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }

        log.info("User(id=" + userIds + ") was successfully deleted.");
        //return Response.ok().build();
    }


    /**
     * {@inheritDoc}
     *
     * @param username the login name of the human
     * @return User the populated user object
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException thrown when username not found
     */
    @Override
    public User getUserByUsername(final String username) {
        return (User) userDao.getUserByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> search(final String searchTerm) {
        return super.search(searchTerm, User.class);
    }

    @Override
    public String buildRecoveryPasswordUrl(final User user, final String urlTemplate) {
        final String token = generateRecoveryToken(user);
        final String username = user.getUsername();
        return StringUtils.replaceEach(urlTemplate,
                new String[]{"{username}", "{token}"},
                new String[]{username, token});
    }

    @Override
    public String generateRecoveryToken(final User user) {
        return passwordTokenManager.generateRecoveryToken(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(final String username, final String token) {
        return isRecoveryTokenValid(getUserByUsername(username), token);
    }

    @Override
    public boolean isRecoveryTokenValid(final User user, final String token) {
        return passwordTokenManager.isRecoveryTokenValid(user, token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendPasswordRecoveryEmail(final String username, final String urlTemplate) {
        log.debug("Sending password recovery token to user: " + username);

        final User user = getUserByUsername(username);
        final String url = buildRecoveryPasswordUrl(user, urlTemplate);

        sendUserEmail(user, passwordRecoveryTemplate, url, "Password Recovery");
    }

    private void sendUserEmail(final User user, final String template, final String url, final String subject) {
        message.setTo(user.getFullName() + "<" + user.getEmail() + ">");
        message.setSubject(subject);

        final Map<String, Serializable> model = new HashMap<String, Serializable>();
        model.put("user", user);
        model.put("applicationURL", url);

        mailEngine.sendMessage(message, template, model);
    }
    
    /**
	 * convert user data to select2 format(https://select2.org/data-sources/formats)
	 * {
	 *	  "results": [
	 *	    {
	 *	      "id": 1,
	 *	      "text": "Option 1"
	 *    	},
	 *	    {
	 *	      "id": 2,
	 *	      "text": "Option 2",
	 *	      "selected": true
	 *	    },
	 *	    {
	 *	      "id": 3,
	 *	      "text": "Option 3",
	 *	      "disabled": true
	 *	    }
	 *	  ]
	 *	}
	 * @param users
	 * @return
	 */
	private  HashMap<String,Object> toSelect2Data(List<User> users){
		List<HashMap<String,Object>> list = new LinkedList<HashMap<String,Object>>();
		for (User u : users) {
			//角色列表
			HashMap<String, Object> mapData = new LinkedHashMap<String, Object>();
			mapData.put("text", u.getUsername()+"(" + u.getFirstName() + ")");//user name
			mapData.put("selected", false);//status
			mapData.put("id", u.getId());//user id
			list.add(mapData);
		}
		HashMap<String,Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("results", list);
		
		return resultData;
	}

    /**
     * {@inheritDoc}
     */
	@Transactional
    @Override
    public User updatePassword(final String username, final String currentPassword, final String recoveryToken, final String newPassword, final String applicationUrl) throws UserExistsException {
        User user = getUserByUsername(username);
        user.setCurrentUser(UserUtils.getLoginName());
        if (isRecoveryTokenValid(user, recoveryToken)) {
            log.debug("Updating password from recovery token for user: " + username);
            user.setPassword(newPassword);
            user = saveUser(user);
            passwordTokenManager.invalidateRecoveryToken(user, recoveryToken);

            sendUserEmail(user, passwordUpdatedTemplate, applicationUrl, "Password Updated");

            return user;
        } else if (StringUtils.isNotBlank(currentPassword)) {
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
        	//if (HelperFunctions.validatePassword(currentPassword, user.getPassword())) {
                log.debug("Updating password (providing current password) for user:" + username);
                user.setPassword(newPassword);
                user = saveUser(user);
                return user;
            }
        }
        // or throw exception
        return null;
    }
    
    public String genPassword(String plainPassword) {
    	return passwordEncoder.encode(plainPassword);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getCacheKey() {
    	return CacheItem.sys_users.toString();
    }
    
    @Override
    public Response getJCaptcha(HttpServletRequest request){
        String id = request.getRequestedSessionId();
        BufferedImage bi = JCaptcha.captchaService.getImageChallengeForID(id);
        // now create an input stream for the thumbnail buffer and return it
 		ByteArrayOutputStream baos = new ByteArrayOutputStream();
 		
 		try {
			ImageIO.write(bi, "jpeg", baos);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.warn(e.getMessage());
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
		}
 		
 		// now get the array
 		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
     		        
        return Response.ok(bais).build();
    }
}

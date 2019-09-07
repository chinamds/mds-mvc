package com.mds.sys.service.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mds.cm.exception.GallerySecurityException;
import com.mds.cm.rest.UserRest;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.core.CacheItem;
import com.mds.i18n.util.I18nUtils;
import com.mds.sys.exception.InvalidUserException;
import com.mds.sys.service.UsersService;
import com.mds.util.CacheUtils;
import com.mds.util.HelperFunctions;
import com.mds.util.StringUtils;
import com.mds.util.Utils;
import com.mds.sys.util.UserUtils;

/// <summary>
/// Contains methods for Web API access to users.
/// </summary>
@Service("usersManager")
@WebService(serviceName = "UsersService", endpointInterface = "com.mds.sys.service.UsersService")
public class UsersManagerImpl implements UsersService{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <summary>
	/// Gets the user with the specified <paramref name="userName" />.
	/// Example: GET /api/users/getbyusername?userName=Admin&amp;galleryId=1
	/// </summary>
	/// <param name="userName">The name of the user to retrieve.</param>
	/// <param name="galleryId">The gallery ID. Required for retrieving the correct user album ID.</param>
	/// <returns>An instance of <see cref="UserRest" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	/// <exception cref="Response"></exception>
	public UserRest get(String userName, long galleryId)	{
		try{
			return UserUtils.getUserEntity(userName, galleryId);
		}catch (GallerySecurityException ge){
			// This is thrown when the current user does not have view and edit permission to the requested user.
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (InvalidUserException ue){
			throw new WebApplicationException(MessageFormat.format("User '{0}' does not exist", userName), Response.Status.NOT_FOUND);
			//ReasonPhrase = "User Not Found"
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Gets a value indicating whether the <paramref name="userName" /> represents an existing user.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns><c>true</c> if the user exists, <c>false</c> otherwise</returns>
	public boolean get(String userName)	{
		return (UserUtils.getUserAccount(userName, false) != null);
	}

	/// <summary>
	/// Persists the <paramref name="user" /> to the data store. The user can be an existing one or a new one to be
	/// created.
	/// </summary>
	/// <param name="user">The role.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the requested action is not successful.</exception>
	public Response post(UserRest user){
		// POST /api/users
		try	{
			String newPwd = null;
			if (user.IsNew){
				UserUtils.createUser(user);
			}else{
				UserUtils.saveUser(user, newPwd);
			}

			String msg = MessageFormat.format("User '{0}' has been saved.{1}",
				Utils.htmlEncode(user.UserName),
				user.PasswordResetRequested ? I18nUtils.getMessage("Admin_Manage_Users_New_Pwd_Text", newPwd) : StringUtils.EMPTY);

			//return Response.status(200, msg).build();
			log.info(msg);
			
			return Response.ok().build();
		}catch (GallerySecurityException ge){
			AppEventLogUtils.LogError(ge);

			// Just in case we created the user and the exception occured at a later step, like adding the roles, delete the user.
			if (user.IsNew && UserUtils.getUserAccount(user.UserName, false) != null){
				UserUtils.deleteUser(user.UserName);
			}
			
			throw new WebApplicationException("Action Forbidden:" + ge.getMessage(), Response.Status.FORBIDDEN);
		}catch (InvalidUserException ue){
			AppEventLogUtils.LogError(ue);

			// Just in case we created the user and the exception occured at a later step, like adding the roles, delete the user.
			if (user.IsNew && UserUtils.getUserAccount(user.UserName, false) != null){
				UserUtils.deleteUser(user.UserName);
			}

			throw new WebApplicationException("Invalid User: " + ue.getMessage());
		/*}
		catch (MembershipCreateUserException ex)
		{
			AppEventLogUtils.LogError(ex);

			// Just in case we created the user and the exception occured at a later step, like adding the roles, delete the user,
			// but only if the user exists AND the error wasn't 'DuplicateUserName'.
			if (user.IsNew.GetValueOrDefault() && (ex.StatusCode != MembershipCreateStatus.DuplicateUserName) && (UserUtils.GetUser(user.UserName, false) != null))
			{
				UserUtils.DeleteUser(user.UserName);
			}

			throw new WebApplicationException(new Response(HttpStatusCode.InternalServerError)
			{
				Content = new StringContent(UserUtils.GetAddUserErrorMessage(ex.StatusCode)),
				ReasonPhrase = "Cannot Create User"
			});*/
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			// Just in case we created the user and the exception occured at a later step, like adding the roles, delete the user.
			if (user.IsNew && UserUtils.getUserAccount(user.UserName, false) != null){
				UserUtils.deleteUser(user.UserName);
			}

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}finally{
			CacheUtils.remove(CacheItem.MDSRoles);
		}
	}

	/// <summary>
	/// Permanently delete the <paramref name="userName" /> from the data store.
	/// </summary>
	/// <param name="userName">The name of the user to be deleted.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the requested action is not successful.</exception>
	public Response delete(String userName)	{
		// DELETE /api/users
		try	{
			// Don't need to check security here because we'll do that in RoleUtils.DeleteMDSRole.
			UserUtils.deleteMDSUser(userName, true);

			//return Response.status(200, MessageFormat.format("User '{0}' has been deleted", Utils.htmlEncode(userName))).build();
			log.info("User '{}' has been deleted", Utils.htmlEncode(userName));
			
			return Response.ok().build();
		}catch (GallerySecurityException ex){
			throw new WebApplicationException("Action Forbidden:" + ex.getMessage(), Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}
}
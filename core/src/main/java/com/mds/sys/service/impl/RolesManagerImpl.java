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
import com.mds.cm.exception.InvalidMDSRoleException;
import com.mds.cm.rest.RoleRest;
import com.mds.cm.util.AppEventLogUtils;
import com.mds.core.CacheItem;
import com.mds.sys.service.RolesService;
import com.mds.sys.util.RoleUtils;
import com.mds.util.CacheUtils;
import com.mds.util.HelperFunctions;
import com.mds.util.Utils;

/// <summary>
/// Contains methods for Web API access to roles.
/// </summary>
@Service("rolesManager")
@WebService(serviceName = "RolesService", endpointInterface = "com.mds.sys.service.RolesService")
public class RolesManagerImpl implements RolesService{
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <summary>
	/// Gets the role with the specified <paramref name="roleId" />.
	/// Example: GET /api/roles/getbyroleid?roleId=System%20Administrator
	/// </summary>
	/// <param name="roleId">The name of the role to retrieve.</param>
	/// <returns>An instance of <see cref="RoleRest" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	public RoleRest get(long roleId){
		try	{
			return RoleUtils.getRoleEntity(roleId);
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
		catch (Exception ex)
		{
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Persists the <paramref name="role" /> to the data store. The role can be an existing one or a new one to be
	/// created.
	/// </summary>
	/// <param name="role">The role.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the requested action is not successful.</exception>
	public Response post(RoleRest role){
		// POST /api/roles
		try	{
			// Don't need to check security here because we'll do that in RoleUtils.Save.
			RoleUtils.save(role);
			
			//return Response.status(200, MessageFormat.format("Role '{0}' has been saved", Utils.htmlEncode(role.Name))).build();
			log.info("Role '{}' has been saved", Utils.htmlEncode(role.Name));
			
			return Response.ok().build();
		}catch (InvalidMDSRoleException ex){
			throw new WebApplicationException("Action Forbidden:" + ex.getMessage(), Response.Status.FORBIDDEN);
		}catch (GallerySecurityException ex){
			throw new WebApplicationException("Action Forbidden:" + ex.getMessage(), Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}finally{
			CacheUtils.remove(CacheItem.MDSRoles);
		}
	}

	/// <summary>
	/// Permanently delete the <paramref name="roleId" /> from the data store.
	/// </summary>
	/// <param name="roleId">The name of the role to be deleted.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the requested action is not successful.</exception>
	public Response delete(long roleId){
		// DELETE /api/roles
		try{
			// Don't need to check security here because we'll do that in RoleUtils.DeleteMDSRole.
			RoleUtils.deleteSystemRole(roleId);

			//return Response.status(200, MessageFormat.format("Role '{0}' has been deleted", Utils.htmlEncode(roleId))).build();
			//log.info("Role '{}' has been deleted", Utils.htmlEncode(roleId));
			log.info("Role '{}' has been deleted", roleId);
			
			return Response.ok().build();
		}catch (GallerySecurityException ex){
			throw new WebApplicationException("Action Forbidden:" + ex.getMessage(), Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}finally{
			CacheUtils.remove(CacheItem.MDSRoles);
		}
	}
}
package com.mds.aiotplayer.sys.service;

import javax.jws.WebService;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.rest.RoleRest;

/// <summary>
/// Contains methods for Web API access to roles.
/// </summary>
@WebService
@Path("/rolerest")
public interface RolesService{
	/// <summary>
	/// Gets the role with the specified <paramref name="roleId" />.
	/// Example: GET /api/roles/getbyroleid?roleId=System%20Administrator
	/// </summary>
	/// <param name="roleId">The name of the role to retrieve.</param>
	/// <returns>An instance of <see cref="Entity.Role" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	@Path("GetByRoleId")
	public RoleRest get(long roleId);

	/// <summary>
	/// Persists the <paramref name="role" /> to the data store. The role can be an existing one or a new one to be
	/// created.
	/// </summary>
	/// <param name="role">The role.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the requested action is not successful.</exception>
	@POST
	public Response post(RoleRest role);

	/// <summary>
	/// Permanently delete the <paramref name="roleId" /> from the data store.
	/// </summary>
	/// <param name="roleId">The name of the role to be deleted.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the requested action is not successful.</exception>
	@Path("DeleteByRoleId")
	@DELETE
	public Response delete(long roleId);
}
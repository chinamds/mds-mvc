/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.service;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mds.aiotplayer.cm.rest.UserRest;

/// <summary>
/// Contains methods for Web API access to users.
/// </summary>
@WebService
@Path("/userrest")
public interface UsersService{
	/// <summary>
	/// Gets the user with the specified <paramref name="userName" />.
	/// Example: GET /api/users/getbyusername?userName=Admin&amp;galleryId=1
	/// </summary>
	/// <param name="userName">The name of the user to retrieve.</param>
	/// <param name="galleryId">The gallery ID. Required for retrieving the correct user album ID.</param>
	/// <returns>An instance of <see cref="Entity.User" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException"></exception>
	/// <exception cref="Response"></exception>
	@Path("GetByUserName")
	@GET
	public UserRest get(@QueryParam("userName") String userName, @QueryParam("galleryId") long galleryId);

	/// <summary>
	/// Gets a value indicating whether the <paramref name="userName" /> represents an existing user.
	/// </summary>
	/// <param name="userName">Name of the user.</param>
	/// <returns><c>true</c> if the user exists, <c>false</c> otherwise</returns>
	@Path("Exists")
	@GET
	public boolean get(String userName);

	/// <summary>
	/// Persists the <paramref name="user" /> to the data store. The user can be an existing one or a new one to be
	/// created.
	/// </summary>
	/// <param name="user">The role.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the requested action is not successful.</exception>
	@POST
	public Response post(UserRest user);

	/// <summary>
	/// Permanently delete the <paramref name="userName" /> from the data store.
	/// </summary>
	/// <param name="userName">The name of the user to be deleted.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the requested action is not successful.</exception>
	@Path("DeleteByUserName")
	@DELETE
	public Response delete(String userName);
}
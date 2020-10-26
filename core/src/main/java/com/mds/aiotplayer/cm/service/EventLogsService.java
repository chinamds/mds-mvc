/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.service;

import javax.jws.WebService;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/// <summary>
/// Contains methods for Web API access to events.
/// </summary>
@WebService
@Path("/events")
public interface EventLogsService{
	/// <summary>
	/// Gets an HTML formatted String representing the specified event <paramref name="id" />.
	/// </summary>
	/// <param name="id">The event ID.</param>
	/// <returns>A String.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the event does not exist in the data store,
	/// the user does not have permission to view it, or some other error occurs.</exception>
	public String Get(long id);

	/// <summary>
	/// Deletes the event having the specified <paramref name="id" />.
	/// </summary>
	/// <param name="id">The ID of the event to delete.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.HttpResponseException">Thrown when the user does not have permission to delete
	/// the event or some other error occurs.
	/// </exception>
	public Response Delete(long id);
}
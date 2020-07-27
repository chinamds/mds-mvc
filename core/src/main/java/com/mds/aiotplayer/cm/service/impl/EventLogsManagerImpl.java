package com.mds.aiotplayer.cm.service.impl;

import java.util.List;

import javax.jws.WebService;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.service.EventLogsService;

/// <summary>
/// Contains methods for Web API access to events.
/// </summary>
@Service("eventLogsManager")
@WebService(serviceName = "EventLogsService", endpointInterface = "com.mds.aiotplayer.cm.service.EventLogsService")
public class EventLogsManagerImpl implements EventLogsService{
	/// <summary>
	/// Gets an HTML formatted String representing the specified event <paramref name="id" />.
	/// </summary>
	/// <param name="id">The event ID.</param>
	/// <returns>A String.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the event does not exist in the data store,
	/// the user does not have permission to view it, or some other error occurs.</exception>
	public String Get(long id)	{
		/*IEventLog appEvent = null;
		try
		{
			appEvent = CMUtils.getAppEvents().FindById(id);

			if (appEvent == null)
			{
				throw new WebApplicationException(Response.Status.NOT_FOUND
				{
					Content = new StringContent(MessageFormat.format("Could not find event with ID = {0}", id)),
					ReasonPhrase = "Event Not Found"
				});
			}

			// If the event has a non-template gallery ID (not all do), then check the user's permission. For those errors without a gallery ID,
			// just assume the user has permission, because there is no way to verify the user can view this event. We could do something
			// that mostly works like verifying the user is a gallery admin for at least one gallery, but the function we are trying to
			// protect is viewing an event message, which is not that important to worry about.
			if (appEvent.getGalleryId() != GalleryUtils.GetTemplateGalleryId())
			{
				SecurityGuard.throwIfUserNotAuthorized(SecurityActions.AdministerSite | SecurityActions.AdministerGallery, RoleUtils.getMDSRolesForUser(), int.MinValue, appEvent.getGalleryId(), UserUtils.isAuthenticated(), false, false);
			}

			return appEvent.ToHtml();
		}
		catch (GallerySecurityException)
		{
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}
		catch (Exception ex)
		{
			if (appEvent != null)
				AppEventLogUtils.LogError(ex, appEvent.getGalleryId());
			else
				AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(new Response(HttpStatusCode.InternalServerError)
			{
				Content = HelperFunctions.getExStringContent(ex),
				ReasonPhrase = "Server Error"
			});
		}*/
		return StringUtils.EMPTY;
	}

	/// <summary>
	/// Deletes the event having the specified <paramref name="id" />.
	/// </summary>
	/// <param name="id">The ID of the event to delete.</param>
	/// <returns>An instance of <see cref="Response" />.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the user does not have permission to delete
	/// the event or some other error occurs.
	/// </exception>
	public Response Delete(long id)
	{
		/*IEventLog appEvent = null;

		try
		{
			appEvent = CMUtils.getAppEvents().FindById(id);

			if (appEvent == null)
			{
				// HTTP specification says the DELETE method must be idempotent, so deleting a nonexistent item must have 
				// the same effect as deleting an existing one. So we simply return HttpStatusCode.OK.
				return new Response(HttpStatusCode.OK) { Content = new StringContent(MessageFormat.format("Event with ID = {0} does not exist.", id)) };
			}

			var isAuthorized = true;

			// If the event has a non-template gallery ID (not all do), then check the user's permission. For those errors without a gallery ID,
			// just assume the user has permission, because there is no way to verify the user can delete this event. We could do something
			// that mostly works like verifying the user is a gallery admin for at least one gallery, but the function we are trying to
			// protect is deleting an event message, which is not that important to worry about.
			if (appEvent.getGalleryId() != GalleryUtils.GetTemplateGalleryId())
			{
				isAuthorized = UserUtils.isUserAuthorized(SecurityActions.AdministerSite | SecurityActions.AdministerGallery, RoleUtils.getMDSRolesForUser(), int.MinValue, appEvent.getGalleryId(), false, false);
			}

			if (isAuthorized)
			{
				EventLogs.EventLogUtils.Delete(id);
				HelperFunctions.purgeCache();

				return new Response(HttpStatusCode.OK) { Content = new StringContent("Event deleted...") };
			}
			else
			{
				throw new WebApplicationException(Response.Status.FORBIDDEN);
			}
		}
		catch (Exception ex)
		{
			if (appEvent != null)
				AppEventLogUtils.LogError(ex, appEvent.getGalleryId());
			else
				AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(new Response(HttpStatusCode.InternalServerError)
			{
				Content = HelperFunctions.getExStringContent(ex),
				ReasonPhrase = "Server Error"
			});
		}*/
		
		return Response.ok().build();
	}
}
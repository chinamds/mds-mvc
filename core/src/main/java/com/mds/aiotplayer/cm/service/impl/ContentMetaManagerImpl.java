package com.mds.aiotplayer.cm.service.impl;

import java.util.List;

import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mds.aiotplayer.cm.content.AddContentObjectSettings;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.rest.ContentItemMeta;
import com.mds.aiotplayer.cm.rest.ContentItem;
import com.mds.aiotplayer.cm.rest.MetaItemRest;
import com.mds.aiotplayer.cm.service.ContentMetaService;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.MetadataUtils;
import com.mds.aiotplayer.core.ActionResult;
import com.mds.aiotplayer.core.ActionResultStatus;
import com.mds.aiotplayer.core.MetadataItemName;
import com.mds.aiotplayer.util.HelperFunctions;

/// <summary>
/// Contains methods for Web API access for modifying metadata tags for multiple gallery objects.
/// Use <see cref="MetaUtils" /> for updating a metadata item for a single gallery object.
/// </summary>
@Service("contentMetaManager")
@WebService(serviceName = "ContentMetaService", endpointInterface = "com.mds.aiotplayer.cm.service.ContentMetaService")
public class ContentMetaManagerImpl implements ContentMetaService {
	
	protected final Logger log = LoggerFactory.getLogger(getClass());
	
	/// <summary>
	/// Gets the meta items for the specified <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">An array of <see cref="ContentItem" /> instances.</param>
	/// <returns>Returns a merged set of metadata.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	
	public List<MetaItemRest> getMetaItemsForContentItems(ContentItem[] contentItems, HttpServletRequest request){
		// GET /api/meta/contentitems - Gets metadata items for the specified objects
		try	{
			return (List<MetaItemRest>) MetadataUtils.getMetaItemsForContentItems(contentItems, request);
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Gets a value indicating whether the logged-on user has edit permission for all of the <paramref name="contentItems" />.
	/// </summary>
	/// <param name="contentItems">A collection of <see cref="ContentItem" /> instances.</param>
	/// <returns><c>true</c> if the current user can edit the items; <c>false</c> otherwise.</returns>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	
	public boolean canUserEdit(Iterable<ContentItem> contentItems){
		// POST /api/meta/canuseredit
		try{
			return MetadataUtils.canUserEditAllItems(contentItems);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}

	/// <summary>
	/// Updates the content items with the specified metadata value. <see cref="ContentMeta.ActionResult" />
	/// contains details about the success or failure of the operation.
	/// </summary>
	/// <param name="contentItemMeta">An instance of <see cref="ContentMeta" /> that defines
	/// the tag value to be added and the content items it is to be added to. It is expected that only
	/// the MTypeId and Value properties of <see cref="ContentMeta.MetaItem" /> are populated.</param>
	/// <exception cref="System.Web.Http.WebApplicationException">Thrown when the current user does not have permission
	/// to carry out the operation or an internal server error occurs.</exception>
	public ContentItemMeta putContentMeta(ContentItemMeta contentItemMeta){
		// /api/contentitemmeta
		try{
			MetadataUtils.saveContentItemMeta(contentItemMeta);

			if (contentItemMeta.getActionResult() == null){
				contentItemMeta.setActionResult(new ActionResult(ActionResultStatus.Success.toString(), "Save successful"));
			}
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}

		return contentItemMeta;
	}
	
	public ContentItemMeta putTest(AddContentObjectSettings settings){
		ContentItemMeta contentItemMeta = new ContentItemMeta(); 
		contentItemMeta.setMetaItem(new MetaItemRest());
		contentItemMeta.setContentItems(new ContentItem[] {new ContentItem()});
		return contentItemMeta;
	}

	/// <summary>
	/// Deletes the meta tag value from the specified content items.
	/// </summary>
	/// <param name="contentItemMeta">An instance of <see cref="ContentMeta" /> that defines
	/// the tag value to be added and the content items it is to be added to.</param>
	/// <exception cref="System.Web.Http.WebApplicationException"></exception>
	public Response deleteContentMeta(ContentItemMeta contentItemMeta){
		// /api/contentitemmeta
		try{
			MetadataItemName mType = MetadataItemName.getMetadataItemName(contentItemMeta.getMetaItem().MTypeId);
			if (mType == MetadataItemName.Tags || mType == MetadataItemName.People){
				MetadataUtils.deleteTag(contentItemMeta);
			}else{
				MetadataUtils.delete(contentItemMeta);
			}

			//return Response.status(200, "Meta item deleted...").build();
			log.info("Meta item deleted...");
			
			return Response.ok().build();
		}catch (GallerySecurityException ge){
			throw new WebApplicationException(Response.Status.FORBIDDEN);
		}catch (Exception ex){
			AppEventLogUtils.LogError(ex);

			throw new WebApplicationException(HelperFunctions.getExStringContent(ex));
		}
	}
}
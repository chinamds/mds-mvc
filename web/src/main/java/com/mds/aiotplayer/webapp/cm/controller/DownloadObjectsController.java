/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.AppEventLogUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.TransferType;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.content.MimeTypeBo;
import com.mds.aiotplayer.cm.content.ZipUtility;
import com.mds.aiotplayer.cm.exception.CannotDeleteAlbumException;
import com.mds.aiotplayer.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.InvalidMDSRoleException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.DisplayObjectType;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.AppSettings;
import com.mds.aiotplayer.sys.util.RoleUtils;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.util.Utils;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/cm/contentobjects/downloadobjects*")
public class DownloadObjectsController extends BaseFormController {
		
    public DownloadObjectsController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "hdnCheckedContentObjectIds", required = false) String[] ids
    		, @RequestParam(value = "ddlImageSize", required = false)DisplayObjectType displayType
    		, @RequestParam(value = "enableContentObjectZipDownload", required = false)boolean enableContentObjectZipDownload
    		, @RequestParam(value = "enableAlbumZipDownload", required = false)boolean enableAlbumZipDownload, HttpServletRequest request,  
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");        	
        }
        
        if (ids.length == 0) {
        	saveError(request, getText("task.no_Objects_Selected_Dtl", request.getLocale()));
        	
        	return "redirect:/cm/galleryview?g=cm_downloadobjects&aid=" + request.getParameter("albumId"); 
        }
        
        buildAndSendZipFile(ids, displayType, request, response);
        /*try
		{
        	DownloadObjects.validateBeforeObjectDeletion(ids);
		}
		catch (CannotDeleteAlbumException ex)
		{
			AppEventLogUtils.LogError(ex);

			saveError(request, ex.getMessage());
        	
        	return "redirect:/cm/galleryview?g=cm_downloadobjects&aid=" + request.getParameter("albumId");
		}
                
		if (DownloadObjects.deleteObjects(ids, userCanDeleteContentObject, userCanDeleteChildAlbum, chkDeleteDbRecordsOnly)) {
			HelperFunctions.purgeCache();

			return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
		}*/
    	
		return null;//"redirect:/cm/galleryview?g=cm_downloadobjects&aid=" + request.getParameter("albumId");
    }
    
    private void buildAndSendZipFile(String[] selectedItems,  DisplayObjectType displayType, HttpServletRequest request, HttpServletResponse response) throws IOException, UnsupportedContentObjectTypeException, UnsupportedImageTypeException, InvalidContentObjectException, InvalidAlbumException, InvalidGalleryException, InvalidMDSRoleException{
		MimeTypeBo mimeType = CMUtils.loadMimeType("dummy.zip");
		String zipFilename = Utils.urlEncode("Content Files".replace(" ", "_"));
		List<Long> albumIds = Lists.newArrayList();
		List<Long> contentObjectIds = Lists.newArrayList();
		for(String selectedItem : selectedItems){
			long id = StringUtils.toLong(selectedItem.substring(1));// 'a' or 'm'

			if (selectedItem.startsWith("m")){
				contentObjectIds.add(id);
			}else if (selectedItem.startsWith("a")){
				albumIds.add(id);
			}
		}
		
		long aid = StringUtils.toLong(request.getParameter("albumId"));
		ZipUtility zip = new ZipUtility(UserUtils.getLoginName(), RoleUtils.getMDSRolesForUser());
		{
			int bufferSize = AppSettings.getInstance().getContentObjectDownloadBufferSize();
			byte[] buffer = new byte[bufferSize];

			ByteArrayOutputStream stream = null;
			try
			{
				// Create an in-memory ZIP file.
				stream = zip.createZipStream(aid, albumIds, contentObjectIds, displayType, request);
				ByteArrayInputStream in = new ByteArrayInputStream(stream.toByteArray());
				
				String userAgent = request.getHeader("User-Agent");
		        boolean isIE = (userAgent != null) && (userAgent.toLowerCase().indexOf("msie") != -1);
		        
		        response.reset();
		        response.setHeader("Pragma", "No-cache");
		        response.setHeader("Cache-Control", "must-revalidate, no-transform");
		        response.setDateHeader("Expires", 0L);

		        response.setContentType("application/x-download");
		        response.setContentLength((int) stream.size());

		        
		        String displayFilename = zipFilename + ".zip";
		        if (isIE) {
		            displayFilename = URLEncoder.encode(displayFilename, "UTF-8");
		            response.setHeader("Content-Disposition", "attachment;filename=\"" + displayFilename + "\"");
		        } else {
		            displayFilename = new String(displayFilename.getBytes("UTF-8"), "ISO8859-1");
		            response.setHeader("Content-Disposition", "attachment;filename=" + displayFilename);
		        }
		        BufferedInputStream is = null;
		        OutputStream os = null;
		        try {

		            os = response.getOutputStream();
		            is = new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray()));
		            IOUtils.copy(is, os);
		        } catch (Exception e) {
		            e.printStackTrace();
		        } finally {
		            IOUtils.closeQuietly(is);
		        }

/*				// Send to user.
				response.addHeader("Content-Disposition", "attachment; filename=" + zipFilename + ".zip");

				response.reset();
				response.setContentType((mimeType != null ? mimeType.getFullType() : "application/octet-stream"));
				//response.setBuffer(false);

				//stream.Position = 0;
				int byteCount;
				
				while ((byteCount = in.read(buffer, 0, buffer.length)) > 0)	{
					response.getOutputStream().write(buffer, 0, byteCount);
					response.flushBuffer();;
				}*/
			}
			finally
			{
				/*if (stream != null)
					stream.Close();

				response.end();
*/			}
		}
	}
}

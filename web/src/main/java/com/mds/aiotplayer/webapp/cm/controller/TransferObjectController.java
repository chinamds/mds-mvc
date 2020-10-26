/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.cm.controller;

import org.apache.commons.lang.StringUtils;
import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.util.AlbumUtils;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.ContentObjectUtils;
import com.mds.aiotplayer.cm.util.TransferObjectState;
import com.mds.aiotplayer.cm.util.TransferObject;
import com.mds.aiotplayer.cm.util.TransferType;
import com.mds.aiotplayer.cm.content.AlbumBo;
import com.mds.aiotplayer.cm.content.ContentObjectBo;
import com.mds.aiotplayer.cm.content.GallerySettings;
import com.mds.aiotplayer.cm.exception.CannotTransferAlbumToNestedDirectoryException;
import com.mds.aiotplayer.cm.exception.GallerySecurityException;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.common.utils.Reflections;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.core.exception.WebException;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.Utils;

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

import java.io.IOException;
import java.util.Locale;

@Controller
@RequestMapping("/cm/contentobjects/transferobject*")
public class TransferObjectController extends BaseFormController {
		
    public TransferObjectController() {
        setCancelView("redirect:galleryview");
        setSuccessView("redirect:galleryview");
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(@RequestParam(value = "hdnCheckedContentObjectIds", required = false) String[] ids
    		, @RequestParam(value = "transferObjectState", required = true)TransferObjectState transferObjectState, HttpServletRequest request,  
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            //return getCancelView();
        	return "redirect:/cm/galleryview?aid=" + request.getParameter("albumId");
        }
        
        String qsValue = request.getParameter("tt");
        String pid = request.getParameter("hdnCheckedAlbumIds");
        String gid = request.getParameter("gid");
        String aid = request.getParameter("albumId");
        String showNextPage = request.getParameter("showNextPage");
        if (showNextPage != null && showNextPage.equals("1")) {
        	if (ids != null && ids.length > 0) {       		 
        		 return "redirect:/cm/galleryview?g=cm_transferobject&step=step1&tt=" + qsValue +"&ids=" + StringUtils.join(ids, ",");
        	}else {
        		saveError(request, getText("task.no_Objects_Selected_Dtl", request.getLocale()));
        		if (transferObjectState == TransferObjectState.ObjectsCopyStep2){
        			transferObjectState = TransferObjectState.ObjectsCopyStep1;
    			} else if (transferObjectState == TransferObjectState.ObjectsMoveStep2) {
    				transferObjectState = TransferObjectState.ObjectsMoveStep1;
    			} else
    				throw new WebException("The function HandleUserNotSelectingAnyObjects should never be invoked in cases where the user made a selection on a previous page and is then transferred to this page.");
        	}
        }else if (showNextPage != null && showNextPage.equals("0")) {
        	try{
        		AlbumBo destAlbum = AlbumUtils.loadAlbumInstance(new Long(pid), false);
				TransferObject.transferObjects(ids, new Long(gid), destAlbum, TransferObject.getTransType(request));

				HelperFunctions.purgeCache();

				return "redirect:/cm/galleryview?aid=" + pid;
			}catch (GallerySecurityException gs) {
				// User does not have permission to carry out the operation.
				saveError(request, getText("task.transferObjects.Cannot_Transfer_No_Permission_Msg_Dtl", request.getLocale()));
			}catch (UnsupportedContentObjectTypeException ex) {
				// User is trying to copy a file that is disabled on the Content Object Types page.
				saveError(request, getText("task.transferObjects.Cannot_Transfer_UnsupportedFileType_Msg_Dtl", request.getLocale()));
			}catch (CannotTransferAlbumToNestedDirectoryException ce) {
				// User tried to move or copy an album to one of its own subdirectories. This cannot be done.
				saveError(request, getText("task.transferObjects.Cannot_Transfer_To_Nested_Album_Msg_Dtl", request.getLocale()));
				//String msg = String.Format(CultureInfo.CurrentCulture, "<p class='mds_msgwarning'><span class='mds_bold'>{0} </span>{1}</p>", Resources.MDS.Task_Transfer_Objects_Cannot_Transfer_To_Nested_Album_Msg_Hdr, Resources.MDS.Task_Transfer_Objects_Cannot_Transfer_To_Nested_Album_Msg_Dtl);
				//phMsg.Controls.Clear();
				//phMsg.Controls.Add(new System.Web.UI.LiteralControl(msg));
			}
        	
        	return "redirect:/cm/galleryview?g=cm_transferobject&step=step1&tt=" + qsValue +"&ids=" + StringUtils.join(ids, ",");
        }
        request.setAttribute("hdnCheckedContentObjectIds", ids);
        request.setAttribute("showNextPage", showNextPage);
        request.setAttribute("transferObjectState", transferObjectState);
        request.setAttribute("hdnCheckedAlbumIds", pid);
        
        return "cm/contentobjects/transferobject";
    }
}

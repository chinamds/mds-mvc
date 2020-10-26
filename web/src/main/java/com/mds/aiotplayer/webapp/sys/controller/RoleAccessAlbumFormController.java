/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.webapp.sys.controller;

import com.mds.aiotplayer.cm.service.AlbumManager;
import com.mds.aiotplayer.cm.util.CMUtils;
import com.mds.aiotplayer.cm.util.GalleryView;
import com.mds.aiotplayer.sys.service.RoleManager;
import com.mds.aiotplayer.sys.util.UserUtils;
import com.mds.aiotplayer.util.HelperFunctions;
import com.mds.aiotplayer.util.StringUtils;
import com.mds.aiotplayer.cm.content.GalleryBo;
import com.mds.aiotplayer.cm.content.GalleryBoCollection;
import com.mds.aiotplayer.cm.model.Album;
import com.mds.aiotplayer.cm.model.Gallery;
import com.mds.aiotplayer.sys.model.Role;
import com.mds.aiotplayer.sys.model.RoleType;
import com.google.common.collect.Lists;
import com.mds.aiotplayer.common.model.search.SearchOperator;
import com.mds.aiotplayer.common.model.search.Searchable;
import com.mds.aiotplayer.webapp.common.controller.BaseFormController;
import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.SecurityActions;
import com.mds.aiotplayer.i18n.util.I18nUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/roleAccessAlbumform*")
public class RoleAccessAlbumFormController extends BaseFormController {
    private RoleManager roleManager = null;
    private AlbumManager albumManager;

    @Autowired
    public void setRoleManager(RoleManager roleManager) {
        this.roleManager = roleManager;
    }
    
    @Autowired
    public void setAlbumManager(AlbumManager albumManager) {
        this.albumManager = albumManager;
    }

    public RoleAccessAlbumFormController() {
        setCancelView("redirect:roles");
        setSuccessView("redirect:roles");
    }
        
    @RequestMapping(method = RequestMethod.GET)
    protected Model accessPermissions(HttpServletRequest request)
    throws Exception {
		Model model = new ExtendedModelMap();

    	Role role = new Role();
        String id = request.getParameter("id");
        if (!StringUtils.isBlank(id)) {
            role = roleManager.get(new Long(id));
        }
        model.addAttribute("role", role);
        GalleryBoCollection galleries = new GalleryBoCollection();
        for(Gallery gallery : role.getGalleries()) {
        	galleries.add(CMUtils.loadGallery(gallery.getId()));
        }
        model.addAttribute("galleries", galleries);
        model.addAttribute("requiredSecurityPermissions", SecurityActions.ViewAlbumOrContentObject.value());
        List<Long> albumIds = role.getAlbums().stream().map(r->r.getId()).collect(Collectors.toList());
        model.addAttribute("selectedAlbumIds", new LongCollection(albumIds));
        model.addAttribute("rootAlbumPrefix",  StringUtils.join(I18nUtils.getString("site.gallery_Text", request.getLocale()), " '{GalleryDescription}': "));
        //model.addAttribute("albums", UserUtils.getAlbumList());

        return model;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String onSubmit(Role role, Long[] hdnCheckedAlbumIds, BindingResult errors, HttpServletRequest request,
                           HttpServletResponse response)
    throws Exception {
        if (request.getParameter("cancel") != null) {
            return getCancelView();
        }

        if (validator != null) { // validator is null during testing
            validator.validate(role, errors);

            if (errors.hasErrors() && request.getParameter("delete") == null) { // don't validate when deleting
                return "sys/roleAccessAlbumform";
            }
        }

        log.debug("entering 'onSubmit' method...");

        boolean isNew = (role.getId() == null);
        String success = getSuccessView();
        Locale locale = request.getLocale();

        if (request.getParameter("delete") != null) {
            roleManager.remove(role.getId());
            saveMessage(request, getText("role.deleted", locale));
        } else {
        	role = roleManager.get(role.getId());
        	/*if (role.getAlbum() != null && role.getAlbum().getId() != null)
        		role.setAlbum(albumManager.get(role.getAlbum().getId()));*/
        	/*if (role.getAlbums() != null && !role.getAlbums().isEmpty()) {
        		//List<Album> albums = Lists.newArrayList();
        		List<String> albumCodes = role.getAlbums().stream().map(Album::getCode).collect(Collectors.toList());
        		Searchable searchable = Searchable.newSearchable();
				searchable.addSearchFilter("code", SearchOperator.in, albumCodes);
				role.setAlbums(albumManager.findAll(searchable));
        	}*/
        	if (hdnCheckedAlbumIds != null && hdnCheckedAlbumIds.length > 0)
        		role.setAlbums(albumManager.find(hdnCheckedAlbumIds));
        	role.setCurrentUser(UserUtils.getLoginName());
        		
            roleManager.save(role);
            HelperFunctions.purgeCache();
            String key = (isNew) ? "role.added" : "role.updated";
            saveMessage(request, getText(key, locale));

            if (!isNew) {
                success = "redirect:roleAccessAlbumform?id=" + role.getId();
            }
        }

        return success;
    }
}

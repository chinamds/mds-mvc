/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.mds.aiotplayer.cm.content.nullobjects.NullContentObject;
import com.mds.aiotplayer.cm.exception.InvalidAlbumException;
import com.mds.aiotplayer.cm.exception.InvalidContentObjectException;
import com.mds.aiotplayer.cm.exception.InvalidGalleryException;
import com.mds.aiotplayer.cm.exception.UnsupportedImageTypeException;
import com.mds.aiotplayer.core.UiTemplateType;
import com.mds.aiotplayer.core.exception.ArgumentNullException;
import com.mds.aiotplayer.core.exception.BusinessException;
import com.mds.aiotplayer.cm.exception.UnsupportedContentObjectTypeException;
import com.mds.aiotplayer.cm.util.CMUtils;

/// <summary>
/// A collection of <see cref="UiTemplateBo" /> objects.
/// </summary>
public class UiTemplateBoCollection extends ArrayList<UiTemplateBo>{
	/// <overloads>
	/// Initializes a new instance of the <see cref="UiTemplateBoCollection"/> class.
	/// </overloads>
	/// <summary>
	/// Initializes a new instance of the <see cref="UiTemplateBoCollection"/> class.
	/// </summary>
	public UiTemplateBoCollection()	{
		 super(new ArrayList<UiTemplateBo>());
	}

	/// <summary>
	/// Initializes a new instance of the <see cref="UiTemplateBoCollection"/> class with the
	/// contents of <paramref name="items" />.
	/// </summary>
	public UiTemplateBoCollection(Iterable<UiTemplateBo> items)	{
		 super(Lists.newArrayList(items));
	}

	/// <summary>
	/// Adds the UI templates to the current collection.
	/// </summary>
	/// <param name="uiTemplates">The UI templates to add to the current collection.</param>
	/// <exception cref="ArgumentNullException">Thrown when <paramref name="uiTemplates"/> is null.</exception>
	public void addRange(Iterable<UiTemplateBo> uiTemplates){
		if (uiTemplates == null)
			throw new ArgumentNullException("uiTemplates");

		for (UiTemplateBo uiTemplate : uiTemplates)	{
			add(uiTemplate);
		}
	}

	/// <summary>
	/// Gets the template with the specified <paramref name="templateType"/> that applies to <paramref name="album"/>.
	/// Guaranteed to not return null. If multiple templates apply, the closest one is returned. Example, if there
	/// are two templates - one for the root album and one for the requested album's parent, the latter is returned.
	/// If multiple templates are assigned to the same album, the first one is returned (as sorted alphebetically by name).
	/// </summary>
	/// <param name="templateType">Type of the template.</param>
	/// <param name="album">The album for which the relevant template is to be returned.</param>
	/// <returns>
	/// Returns an instance of <see cref="UiTemplateBo"/>.
	/// </returns>
	/// <exception cref="BusinessException">Thrown when no relevant template is found.</exception>
	public UiTemplateBo get(UiTemplateType templateType, AlbumBo album)	{
		// Perf improvement: If there is only one template for the requested type, then return that one.
		List<UiTemplateBo> tmplItems = stream().filter(t->t.TemplateType == templateType).collect(Collectors.toList());
		if (tmplItems.size() == 1){
			return tmplItems.get(0);
		}

		ContentObjectBo curAlbum = album;

		if (album.getIsVirtualAlbum()){
			try {
				// We want the template for the root album.
				curAlbum = CMUtils.loadRootAlbumInstance(album.getGalleryId());
			} catch (UnsupportedContentObjectTypeException | InvalidGalleryException | UnsupportedImageTypeException
					| InvalidContentObjectException | InvalidAlbumException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		UiTemplateBo template;
		do{
			long albumId = curAlbum.getId();
			template = tmplItems.stream().filter(t->t.RootAlbumIds.contains(albumId)).findFirst().orElse(null);
					//(from t in tmplItems where t.RootAlbumIds.Contains(curAlbum.Id) select t).FirstOrDefault();
			if (template != null)
				break;

			curAlbum = (ContentObjectBo)curAlbum.getParent();
		} while (!(curAlbum instanceof NullContentObject));

		if (template == null)
			throw new BusinessException(MessageFormat.format("Missing UI template: No template was found in the data store with type '{0}' that applies to album ID {1}. There must be at least one record for this type with the name 'Default' and assigned to the root album. Try recycling the IIS app pool - data validation during app startup may be able to fix this.", templateType, album.getId()));
		
		return template;
	}
}

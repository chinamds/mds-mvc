/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import com.mds.aiotplayer.core.LongCollection;
import com.mds.aiotplayer.core.UiTemplateType;

/// <summary>
/// Contains data and behavior for managing a UI template.
/// </summary>
public class UiTemplateBo {
	public UiTemplateBo() {
		this.RootAlbumIds = new LongCollection();
	}

	/// <summary>
	/// Gets or sets the ID for the UI template.
	/// </summary>
	/// <value>The UI templateID.</value>
	public long UiTemplateId;

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew(){
		return (UiTemplateId == Long.MIN_VALUE);
	}

	/// <summary>
	/// Gets or sets the type of the template.
	/// </summary>
	/// <value>The type of the template.</value>
	public UiTemplateType TemplateType;

	/// <summary>
	/// Gets or sets the ID of the gallery this template is associated with.
	/// </summary>
	/// <value>The ID of the gallery this template is associated with.</value>
	public long GalleryId;

	/// <summary>
	/// Gets or sets the name of the template.
	/// </summary>
	/// <value>The name.</value>
	public String Name;

	/// <summary>
	/// Gets or sets a description of the template.
	/// </summary>
	/// <value>The description.</value>
	public String Description;

	/// <summary>
	/// Gets or sets the IDs of the albums to which the template applies.
	/// </summary>
	/// <value>The IDs of the albums to which the template applies.</value>
	public LongCollection RootAlbumIds;

	/// <summary>
	/// Gets or sets the template for rendering the HTML. String must be compatible with the
	/// jsRender syntax.
	/// </summary>
	/// <value>The template data.</value>
	public String HtmlTemplate;

	/// <summary>
	/// Gets or sets the template for rendering the JavaScript. String must be compatible with the
	/// jsRender syntax.
	/// </summary>
	/// <value>The javascript data.</value>
	public String ScriptTemplate;

	/// <summary>
	/// Creates a deep copy of this instance. It is not persisted to the data store.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public UiTemplateBo Copy()
	{
		UiTemplateBo tmplCopy = new UiTemplateBo();

		tmplCopy.UiTemplateId = Long.MIN_VALUE;
		tmplCopy.TemplateType = this.TemplateType;
		tmplCopy.GalleryId = this.GalleryId;
		tmplCopy.Name = this.Name;
		tmplCopy.Description = this.Description;
		tmplCopy.RootAlbumIds = new LongCollection(this.RootAlbumIds);
		tmplCopy.HtmlTemplate = this.HtmlTemplate;
		tmplCopy.ScriptTemplate = this.ScriptTemplate;

		return tmplCopy;
	}
}

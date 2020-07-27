package com.mds.aiotplayer.cm.rest;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mds.aiotplayer.core.ActionResult;
/// <summary>
/// A client-optimized object that wraps a meta item and the gallery items it applies to.
/// </summary>
@XmlRootElement(name = "ContentItemMeta")
public class ContentItemMeta
{
	/// <summary>
	/// An array of <see cref="ContentItem" /> instances.
	/// </summary>
	/// <value>The content items.</value>
	private ContentItem[] contentItems;

	/// <summary>
	/// Gets or sets the meta item that applies to <see cref="ContentItems" />.
	/// </summary>
	/// <value>The meta item.</value>
	private MetaItemRest metaItem;

	/// <summary>
	/// Gets or sets information about an action applied to this instance (e.g. when saving).
	/// </summary>
	private ActionResult actionResult;

	@JsonProperty(value = "ContentItems")
	public ContentItem[] getContentItems() {
		return contentItems;
	}

	public void setContentItems(ContentItem[] contentItems) {
		this.contentItems = contentItems;
	}

	@JsonProperty(value = "MetaItem")
	public MetaItemRest getMetaItem() {
		return metaItem;
	}

	public void setMetaItem(MetaItemRest metaItem) {
		this.metaItem = metaItem;
	}

	@JsonProperty(value = "ActionResult")
	public ActionResult getActionResult() {
		return actionResult;
	}

	public void setActionResult(ActionResult actionResult) {
		this.actionResult = actionResult;
	}
}
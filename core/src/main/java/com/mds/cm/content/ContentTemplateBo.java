package com.mds.cm.content;

/// <summary>
/// Represents a media template within MDS System. A media template describes the HTML and javascript that is used
/// to render a content object in a particular browser.
/// </summary>
public class ContentTemplateBo {
	//#region Constructors

	/// <summary>
	/// Initializes a new instance of the <see cref="ContentTemplate"/> class.
	/// </summary>
	public ContentTemplateBo()	{
	}

	//#endregion

	//#region Public Properties

	/// <summary>
	/// Gets or sets the value that uniquely identifies this media template.
	/// </summary>
	/// <value>The media template ID.</value>
	public long ContentTemplateId;

	/// <summary>
	/// Gets a value indicating whether this object is new and has not yet been persisted to the data store.
	/// </summary>
	/// <value><c>true</c> if this instance is new; otherwise, <c>false</c>.</value>
	public boolean isNew(){
		return (ContentTemplateId == Long.MIN_VALUE);
	}

	/// <summary>
	/// Gets or sets the identifier of a browser as specified in the .Net Framework's browser definition file. Every MIME type must
	/// have one media template where <see cref="ContentTemplateBo.BrowserId" /> = "default". Additional <see cref="ContentTemplateBo" /> objects
	/// may represent a more specific browser or browser family, such as Internet Explorer (<see cref="ContentTemplateBo.BrowserId" /> = "ie").
	/// </summary>
	/// <value>The identifier of a browser as specified in the .Net Framework's browser definition file.</value>
	public String BrowserId;

	/// <summary>
	/// Gets or sets the HTML template to use to render a content object in a web browser.
	/// </summary>
	/// <value>The HTML template to use to render a content object in a web browser.</value>
	public String HtmlTemplate;

	/// <summary>
	/// Gets or sets the javascript template to use when rendering a content object in a web browser.
	/// </summary>
	/// <value>The javascript template to use when rendering a content object in a web browser.</value>
	public String ScriptTemplate;

	/// <summary>
	/// Gets or sets the MIME type this media template applies to. Examples: image/*, video/*, video/quicktime, application/pdf.
	/// Notice that an asterisk (*) can be used to represent all subtypes within a type (e.g. "video/*" matches all videos).
	/// </summary>
	/// <value>The MIME type this media template applies to.</value>
	public String MimeType;

	//#endregion

	//#region Public Methods

	/// <summary>
	/// Creates a deep copy of this instance.
	/// </summary>
	/// <returns>Returns a deep copy of this instance.</returns>
	public ContentTemplateBo Copy()	{
		ContentTemplateBo bp = new ContentTemplateBo();

		bp.ContentTemplateId = Long.MIN_VALUE;
		bp.MimeType = this.MimeType;
		bp.BrowserId = this.BrowserId;
		bp.HtmlTemplate = this.HtmlTemplate;
		bp.ScriptTemplate = this.ScriptTemplate;

		return bp;
	}

	//#endregion
}

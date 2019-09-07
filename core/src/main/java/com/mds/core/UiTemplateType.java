package com.mds.core;

/// <summary>
/// Specifies the category a UI template belongs to. For example, when a template is designed for rendering
/// a content object, it will have the <see cref="UiTemplateType.ContentObject" /> enumeration value.
/// </summary>
public enum UiTemplateType{
	/// <summary>
	/// Specifies that no template type has been specified.
	/// </summary>
	NotSpecified(0),
	/// <summary>
	/// Specifies the Album UI template type.
	/// </summary>
	Album(1),
	/// <summary>
	/// Specifies the Content Object UI template type.
	/// </summary>
	ContentObject(2),
	/// <summary>
	/// Specifies the Header UI template type.
	/// </summary>
	Header(3),
	/// <summary>
	/// Specifies the UI template type for the left pane of a three-pane window.
	/// </summary>
	LeftPane(4),
	/// <summary>
	/// Specifies the UI template type for the right pane of a three-pane window.
	/// </summary>
	RightPane(5);
	
	private final int uiTemplateType;
    
    private UiTemplateType(int uiTemplateType) {
        this.uiTemplateType = uiTemplateType;
    }
}
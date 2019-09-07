package com.mds.cm.content;

import com.mds.core.MessageStyle;
import com.mds.core.MessageType;

/// <summary>
/// Contains settings for controlling the content and behavior for displaying a message to a user.
/// </summary>
public class ClientMessageOptions{
	public ClientMessageOptions() {}
	public ClientMessageOptions(MessageType messageId) {this.setMessageId(messageId);}
	private MessageType messageId;
	private String title;
	private String message;
	private Integer autoCloseDelay;
	private MessageStyle style;

	/// <summary>
	/// Gets or sets the type of the message. When specified, the remaining properties can be
	/// automatically determined. Specify <see cref="MessageType.None" /> when manually setting the
	/// remaining properties.
	/// </summary>
	/// <value>An instance of <see cref="MessageType" />.</value>
	public MessageType getMessageId() {
		return messageId;
	}
	
	public void setMessageId(MessageType messageId) {
		this.messageId = messageId;
	}

	/// <summary>
	/// Gets or sets the title. This value is displayed in the title bar of the control shown to the user.
	/// May be null.
	/// </summary>
	/// <value>A String.</value>
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	/// <summary>
	/// Gets or sets the message. This value is displayed in the body of the control shown to the user.
	/// May be null.
	/// </summary>
	/// <value>A String.</value>
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	/// <summary>
	/// The number of milliseconds to wait until a message auto-closes. Use 0 to never auto-close.
	/// Defaults to 4000 (4 seconds) when Style is <see cref="MessageStyle.Success" />; otherwise defaults
	/// to 0 (stay open).
	/// </summary>
	/// <value>An integer.</value>
	public int getAutoCloseDelay(){
		if (this.style == MessageStyle.Success)
			return autoCloseDelay == null ? 4000 : autoCloseDelay;
		else
			return autoCloseDelay == null ? 0 : autoCloseDelay;					
	}

	public void setAutoCloseDelay(int autoCloseDelay){
		this.autoCloseDelay = autoCloseDelay;
	}
	
	/// <summary>
	/// Gets or sets the category of message. This value controls the formatting of the control shown
	/// to the user. For example, the value <see cref="MessageStyle.Error" /> results in a red-themed
	/// display message.
	/// </summary>
	/// <value>An instance of <see cref="MessageStyle" />.</value>	
	public MessageStyle getStyle() {
		return style;
	}
	public void setStyle(MessageStyle style) {
		this.style = style;
	}
	
	public String getMsgType() {
		return style.toString().toLowerCase();
	}
}
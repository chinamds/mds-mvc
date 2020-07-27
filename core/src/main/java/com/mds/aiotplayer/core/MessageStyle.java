package com.mds.aiotplayer.core;

import com.mds.aiotplayer.util.StringUtils;

/// <summary>
/// Specifies the style of message to be displayed to the user.
/// </summary>
public enum MessageStyle{
	None(0),
	Success(1),
	Info(2),
	Notice(3),
	Error(4);
	
	private final int messageStyle;
    
    private MessageStyle(int messageStyle) {
        this.messageStyle = messageStyle;
    }
    
    public int value() {
    	return messageStyle;
    }
    
    public static MessageStyle getMessageStyle(String messageStyle) {
		for(MessageStyle value : MessageStyle.values()) {
			if (value.toString().equalsIgnoreCase(messageStyle))
				return value;
		}
		
		return MessageStyle.None;
	}
    
    public static MessageStyle parse(String messageStyle) {
		int val = StringUtils.toInteger(messageStyle);
		for(MessageStyle value : MessageStyle.values()) {
			if (value.value() == val)
				return value;
		}
		
		return MessageStyle.valueOf(messageStyle);
	}
}
package com.mds.core;

import com.mds.util.StringUtils;

/// <summary>
/// Specifies a particular message that is to be displayed to the user. The text of the message is extracted from the resource file.
/// </summary>
public enum MessageType{
	None(0),
	ThumbnailSuccessfullyAssigned(1),
	CannotAssignThumbnailNoObjectsExistInAlbum(2),
	CannotEditCaptionsNoEditableObjectsExistInAlbum(3),
	//CannotRearrangeNoObjectsExistInAlbum(4),
	CannotRotateNoRotatableObjectsExistInAlbum(5),
	CannotMoveNoObjectsExistInAlbum(6),
	CannotCopyNoObjectsExistInAlbum(7),
	CannotDeleteOriginalFilesNoObjectsExistInAlbum(8),
	CannotDeleteObjectsNoObjectsExistInAlbum(9),
	//OneOrMoreCaptionsExceededMaxLength(10),
	//CaptionExceededMaxLength(11),
	ContentObjectDoesNotExist(12),
	AlbumDoesNotExist(13),
	//NoScriptDefaultText(14),
	//SynchronizationSuccessful(15),
	//SynchronizationFailure(16),
	ObjectsSuccessfullyDeleted(17),
	OriginalFilesSuccessfullyDeleted(18),
	UserNameOrPasswordIncorrect(19),
	//AlbumNameExceededMaxLength(20),
	//AlbumSummaryExceededMaxLength(21),
	//AlbumNameAndSummaryExceededMaxLength(22),
	AlbumNotAuthorizedForUser(23),
	NoAuthorizedAlbumForUser(24),
	CannotOverlayWatermarkOnImage(25),
	CannotRotateObjectNotRotatable(26),
	ObjectsSuccessfullyMoved(27),
	ObjectsSuccessfullyCopied(28),
	ObjectsSuccessfullyRearranged(29),
	ObjectsSuccessfullyRotated(30),
	ObjectsSkippedDuringUpload(31),
	CannotRotateInvalidImage(32),
	CannotEditGalleryIsReadOnly(33),
	CannotDownloadObjectsNoObjectsExistInAlbum(34),
	GallerySuccessfullyChanged(35),
	SettingsSuccessfullyChanged(36),
	ObjectsBeingProcessedAsyncronously(37),
	CannotTransferObjectInsufficientPermission(38);
	
	private final int messageType;
    
    private MessageType(int messageType) {
        this.messageType = messageType;
    }
    
    public int value() {
    	return messageType;
    }
    
    public static MessageType getMessageType(String messageType) {
		for(MessageType value : MessageType.values()) {
			if (value.toString().equalsIgnoreCase(messageType))
				return value;
		}
		
		return MessageType.None;
	}
    
    public static MessageType getMessageType(int value) {
		for(MessageType messageType : MessageType.values()) {
			if (value == messageType.value())
				return messageType;
		}
		
		return MessageType.None;
	}
    
    public static MessageType parse(String messageType) {
		int val = StringUtils.toInteger(messageType);
		for(MessageType value : MessageType.values()) {
			if (value.value() == val)
				return value;
		}
		
		return MessageType.valueOf(messageType);
	}
}
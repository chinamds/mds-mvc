package com.mds.sys.model;

/**
 * Message manipulate
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum MessageOperate {

	/**
     * save as drafts
     */
    drafts("myMessage.messageoperate.drafts", "fa fa-file"),
    
    /**
     * new message
     */
    newmessage("myMessage.messageoperate.newmessage", "fa fa-file"),
    /**
     * mark message as read
     */
    markasread("myMessage.messageoperate.markasread", "fa fa-file"),
    /**
     * archive message
     */
    archive("myMessage.messageoperate.archive", "fa fa-archive"),
    /**
     * reply message
     */
    reply("myMessage.messageoperate.reply", "fa fa-reply"),
    /**
     * forward message
     */
    forward("myMessage.messageoperate.forward", "fa fa-forward"),
    /**
     * move message to archive folder
     */
    movetoarchive("myMessage.messageoperate.movetoarchive", "fa fa-file"),
	 /**
     * delete message
     */
    delete("myMessage.messageoperate.delete", "fa fa-times"),
	 /**
     * empty message folder
     */
    emptyfolder("myMessage.messageoperate.emptyfolder", "fa fa-file"),
	/**
     * View message
     */
    viewmessage("myMessage.messageoperate.viewmessage", "fa fa-eye"),
	 /**
     * send message
     */
    send("myMessage.messageoperate.send", "fa fa-paper-plane"),
	/**
     * Discard message
     */
    discard("myMessage.messageoperate.discard", "fa fa-trash"),
	/**
     * Move message to
     */
    move("myMessage.messageoperate.move", "fa fa-file");

    private final String info;
    private final String icon;

    private MessageOperate(String info, String icon) {
        this.info = info;
        this.icon = icon;
    }

    public String getInfo() {
        return info;
    }
    
    public String getIcon() {
        return icon;
    }
}

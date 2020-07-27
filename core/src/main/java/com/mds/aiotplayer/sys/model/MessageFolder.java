package com.mds.aiotplayer.sys.model;

/**
 * Message Folder
 * <p>User: John Lee
 * <p>Date: 02 December 2017 21:25:37
 * <p>Version: 1.0
 */
public enum MessageFolder {

    /**
     * inbox foler
     */
    inbox("myMessage.messagefolder.inbox", "fas fa-inbox"),
    /**
     * outbox folder
     */
    outbox("myMessage.messagefolder.outbox", "fa fa-paper-plane"),
    /**
     * archive folder
     */
    archive("myMessage.messagefolder.archive", "fa fa-archive"),
    /**
     * junk folder
     */
    junk("myMessage.messagefolder.junk", "fa fa-trash"),
    /**
     * drafts folder
     */
    drafts("myMessage.messagefolder.drafts", "fa fa-file-alt"),
    /**
     * deleted folder
     */
    deleted("myMessage.messagefolder.deleted", "fa fa-times");

    private final String info;
    private final String icon;

    private MessageFolder(String info, String icon) {
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

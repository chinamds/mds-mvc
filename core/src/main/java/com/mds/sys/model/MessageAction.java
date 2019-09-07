package com.mds.sys.model;

/**
 * Message Action
 * <p>User: John Lee
 * <p>Date: 2017/8/17 19:57
 * <p>Version: 1.0
 */
public enum MessageAction {
    re("Reply"),
    fw("Forward"),
	rt("receipt");

    private final String info;

    private MessageAction(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}

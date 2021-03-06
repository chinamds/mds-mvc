/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.sys.model;

import org.hibernate.search.filter.FilterKey;

public class MessageFolderFilter extends FilterKey {
    private String messageFolder;

    @Override
    public boolean equals(Object otherKey) {
        if(this.messageFolder == null || !(otherKey instanceof MessageFolderFilter)) {
            return false;
        }
        MessageFolderFilter otherMessageFolderFilterKey = (MessageFolderFilter) otherKey;
        return otherMessageFolderFilterKey.messageFolder != null && this.messageFolder.equals(otherMessageFolderFilterKey.messageFolder);
    }

    @Override
    public int hashCode() {
        if(this.messageFolder == null) {
            return 0;
        }
        return this.messageFolder.hashCode();
    }
    
    public void setMessageFolder(String messageFolder) {
        this.messageFolder = messageFolder;
    }
    
    public String getMessageFolder() {
        return this.messageFolder;
    }
}
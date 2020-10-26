/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.cm.content;

import java.util.EventObject;
import java.util.Objects;

/// <summary>
/// Provides functionality for creating and saving the files associated with gallery objects.
/// </summary>
public class ContentObjectEvent extends EventObject {
    private final ContentObjectBo contentObject;

    public ContentObjectEvent(Object source, ContentObjectBo contentObject) {
        super(source);
        this.contentObject = Objects.requireNonNull(contentObject);
    }

    public ContentObjectBo getContentObject() {
        return contentObject;
    }
}


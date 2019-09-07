package com.mds.cm.content;

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


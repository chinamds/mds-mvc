package com.mds.core;

import java.util.EventListener;

/// <summary>
/// Provides functionality for clear the id.
/// </summary>
public interface LongCollectionListener extends EventListener{
	 default void cleared(LongCollectionEvent event) { }
}


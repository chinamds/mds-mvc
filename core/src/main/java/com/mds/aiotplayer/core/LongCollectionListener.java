/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.core;

import java.util.EventListener;

/// <summary>
/// Provides functionality for clear the id.
/// </summary>
public interface LongCollectionListener extends EventListener{
	 default void cleared(LongCollectionEvent event) { }
}


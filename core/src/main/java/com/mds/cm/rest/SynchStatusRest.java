package com.mds.cm.rest;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;

/// <summary>
/// A simple object that contains synchronization status information. This class is used to pass information between the browser and the web server
/// via AJAX callbacks during a synchronization.
/// </summary>
public class SynchStatusRest{
	/// <summary>
	/// A GUID that uniquely identifies the current synchronization.
	/// </summary>
	public String SynchId;
	/// <summary>
	/// The status of the current synchronization. This is the text representation of the <see cref="MDS.Business.Interfaces.SynchronizationState" /> enumeration.
	/// </summary>
	public String Status;
	/// <summary>
	/// A user-friendly version of the status.
	/// </summary>
	public String StatusForUI;
	/// <summary>
	/// The total number of files in the directory or directories that are being processed in the current synchronization.
	///  </summary>
	public int TotalFileCount;
	/// <summary>
	/// The one-based index value of the current file being processed.
	/// </summary>
	public int CurrentFileIndex;
	/// <summary>
	/// The path, including the file name, to the current file being processed. The path is relative to the content object
	/// directory. For example, if the content objects directory is C:\mypics\ and the file currently being processed is
	/// at C:\mypics\vacations\india\buddha.jpg, this property is vacations\india\buddha.jpg.
	/// </summary>
	public String CurrentFile;
	/// <summary>
	/// The percent complete of the current synchronization.
	/// </summary>
	public int PercentComplete;
	/// <summary>
	/// The rate of the current synchronization (e.g. "28.1 objects/sec").
	/// </summary>
	public String SyncRate;
	/// <summary>
	/// A list of all files that were encountered during the synchronization but were not added. The key contains
	/// the name of the file; the value contains the reason why the object was skipped. Guaranteed to not be null.
	/// </summary>
	public List<ImmutablePair<String, String>> SkippedFiles;
}
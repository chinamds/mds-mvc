/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * https://github.com/chinamds/license/
 */
package com.mds.aiotplayer.common.model;

// Class is created to create its object and set the local variables as required by jTable.
//That object can be directly given to jTable.

import com.fasterxml.jackson.annotation.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class JTableResult {
    public String Result;
    public Object Record;
    public Object Records;
    public Object Options;
    public int TotalRecordCount;
    public String Message;
}

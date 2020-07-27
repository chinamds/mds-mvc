package com.mds.aiotplayer.common.model;

// Class is created to create its object and set the local variables as required by jTable.
//That object can be directly given to jTable.
public class JTableRequest {
    private int jtStartIndex;
    private int jtPageSize;
    private String jtSorting;

    public int getJtStartIndex() {
        return jtStartIndex;
    }

    public void setJtStartIndex(int jtStartIndex) {
        this.jtStartIndex = jtStartIndex;
    }

    public int getJtPageSize() {
        return jtPageSize;
    }

    public void setJtPageSize(int jtPageSize) {
        this.jtPageSize = jtPageSize;
    }

    public String getJtSorting() {
        return jtSorting;
    }

    public void setJtSorting(String jtSorting) {
        this.jtSorting = jtSorting;
    }
}

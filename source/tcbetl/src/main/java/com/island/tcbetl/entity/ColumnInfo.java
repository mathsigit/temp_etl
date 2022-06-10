package com.island.tcbetl.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
public class ColumnInfo implements Serializable {
    @Accessors(fluent = true)
    private boolean isSkipTransform;

    @Accessors(fluent = true)
    private boolean isParentColumn;

    private String sourceName;
    private String fieldName;
    private int fieldSequence;
    private String redefineOfField = "";
    private int redefineSequence;
    private String fieldActive;
    private int fieldType;
    // Chinese type
    private int fieldCcsid;
    private int fieldLen;
    private int field16Len;
    private int fieldScale;
    private String value16;
    private String value10;
    private int rawStartIndex;
}

package com.island.tcbetl.model;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Data
@Entity
public class InputFields implements Serializable {

    @Id
    @Column(name = "id")
    UUID newid;

    @Column(name = "groupid")
    String groupId;

    @Column(name = "sourcetype")
    String sourceType;

    @Column(name = "sourcename")
    String sourceName;

    @Column(name = "fieldsequence")
    Integer fieldSequence;

    @Column(name = "fieldname")
    String fieldName;

    @Column(name = "validfrom")
    String validFrom;

    @Column(name = "validuntil")
    String validUntil;

    @Column(name = "description")
    String description;

    @Column(name = "redefineoffield")
    String redefineOfField;

    @Column(name = "redefinesequence")
    Integer redefineSequence;

    @Column(name = "fieldactive")
    String fieldActive;

    @Column(name = "fieldtype")
    Integer fieldType;

    @Column(name = "fieldlen")
    Integer fieldLen;

    @Column(name = "fieldprecision")
    String fieldPsrecision;

    @Column(name = "fieldscale")
    Integer fieldScale;

    @Column(name = "fieldccsid")
    Integer fieldCcsid;

    @Column(name = "exitflag")
    String exitFlag;

    @Column(name = "userfieldexitno")
    String userFieldExitNo;

    @Column(name = "sysfieldexitno")
    String sysFieldExitNo;

    @Column(name = "adabasname")
    String adabasName;

    @Column(name = "withintable")
    String withinTable;

    @Column(name = "dbinternalsegcol")
    String dbinternalsegcol;

    @Column(name = "defaultvalue")
    String defaultValue;

    @Column(name = "defaultvaluetype")
    String defaultValueType;
}

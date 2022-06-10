package com.island.tcbetl.service;

import com.island.tcbetl.constants.SpecialColumn;
import com.island.tcbetl.entity.ColumnInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DecodeServiceTest {

    private final DecodeService decodeService = new DecodeService();
    private ColumnInfo columnInfo;

    @Before
    public void before() {
        columnInfo = new ColumnInfo();
        columnInfo.setSourceName("CNTP.REC");
        columnInfo.isSkipTransform(true);
        columnInfo.setFieldName("MBRNO");
        columnInfo.setValue10("");
    }

    @Test
    public void specialTransformRuleTest() {
        ColumnInfo ci = decodeService.specialTransformRule(this.columnInfo);
        Assert.assertTrue(SpecialColumn.SKIP_COLUMN_SOURCE.contains(ci.getSourceName()));
        Assert.assertEquals(" ",ci.getValue10());
        ci.setFieldName("TRNSDT");
        ci = decodeService.specialTransformRule(ci);
        Assert.assertEquals("",ci.getValue10());

    }
}
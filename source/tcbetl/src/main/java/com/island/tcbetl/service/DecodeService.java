package com.island.tcbetl.service;

import com.island.tcbetl.constants.SpecialColumn;
import com.island.tcbetl.entity.ColumnInfo;
import com.island.tcbetl.transform.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.SerializationUtils;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value = "DecodeService")
public class DecodeService {
    private Map<String,ColumnInfo> columnMeta;
    private Map<String, String> xTypeMap;
    private Map<String, String> ucs4TypeMap;

    /**
     * Transform column from 16 to 10
     * @param raw16byteData
     * @param columnMeta
     * @param xTypeMap
     * @param ucs4TypeMap
     * @return
     */
    public List<Map<String, ColumnInfo>> executeDecode(List<String> raw16byteData,
                                                       Map<String,ColumnInfo> columnMeta,
                                                       Map<String, String> xTypeMap,
                                                       Map<String, String> ucs4TypeMap) {
        this.columnMeta = columnMeta;
        this.xTypeMap = xTypeMap;
        this.ucs4TypeMap = ucs4TypeMap;
        List<Map<String, ColumnInfo>> resultMap = new ArrayList<>();
        log.info("Begin transform column data for each row");
        for (String r : raw16byteData) {
            Map<String, ColumnInfo> transformedRow = this.transformRow(r);
            resultMap.add(transformedRow);
        }
        log.info("End transform column data for each row");
        return resultMap;
    }

    /**
     *
     * @param row16ByteData Row of 16 byte data
     * @return
     */
    private Map<String, ColumnInfo> transformRow(String row16ByteData) {
        int startIndexOfRaw = 0;
        TypeTransformer typeTransformer = null;
        Map<String, ColumnInfo> returnRow = new LinkedHashMap<>();
        for (Map.Entry<String, ColumnInfo> cd : this.columnMeta.entrySet()) {
            cd.getValue().setRawStartIndex(startIndexOfRaw);
            ColumnInfo ci = SerializationUtils.clone(cd.getValue());
            //start to transform column
            typeTransformer = this.getTransformType(ci);
            ci = typeTransformer.transform(row16ByteData);
            //Special column rule
            ci = specialTransformRule(ci);
            //Set result to column meta for parent column
            cd.getValue().setValue16(ci.getValue16());
            cd.getValue().setValue10(ci.getValue10());
            if (ci.getRedefineOfField() == null || ci.getRedefineOfField().isEmpty()) {
                startIndexOfRaw += ci.getField16Len();
            }
            returnRow.put(ci.getFieldName(), ci);
        }
        return returnRow;
    }

    /**
     * Get Transform type class by ColumnInfo Object
     *
     * @param columnInfo current ColumnInfo Object
     * @return
     */
    private TypeTransformer getTransformType(ColumnInfo columnInfo) {
        TypeTransformer iType = null;
        int iTypeNO = columnInfo.getFieldType();
        if (iTypeNO == 2000) {
            iType = new Type2000(columnInfo, this.columnMeta, this.xTypeMap);
        } else if (iTypeNO == 2452) {
            if (columnInfo.getFieldCcsid() == 11835) {
                //TODO: implements Type2452C
                throw new IllegalArgumentException("Illegal Argument: Field Ccsid is not support now ! (Field Ccsid = 11835)");
//                iType = new Type2452C(columnInfo, this.columnMeta, this.ucs4TypeMap);
            } else iType = new Type2452(columnInfo, this.columnMeta, this.xTypeMap);
        } else if (iTypeNO == 2484) {
            iType = new Type2484(columnInfo, this.columnMeta);
        } else if (iTypeNO == 2500) {
            iType = new Type2500(columnInfo, this.columnMeta);
        } else {
            throw new IllegalArgumentException("Filed Type:" + iTypeNO + " is Illegal.");
        }
        return iType;
    }

    /**
     * Special transform rule
     * @param columnInfo Data of column
     * @return
     */
    ColumnInfo specialTransformRule(ColumnInfo columnInfo) {
        ColumnInfo returnCI = SerializationUtils.clone(columnInfo);
        //Skip column rule
        if(SpecialColumn.SKIP_COLUMN_SOURCE.contains(returnCI.getSourceName())) {
            if (returnCI.isSkipTransform()) {
                if(returnCI.getFieldName().equals(SpecialColumn.SKIP_COLUMN_OF_CNTP_REC.TRNSDT.toString())){
                    log.debug("Begin transform special column: " + returnCI.getFieldName() + " for source file: "+ returnCI.getSourceName());
                    returnCI.setValue10("");
                } else if (returnCI.getFieldName().equals(SpecialColumn.SKIP_COLUMN_OF_CNTP_REC.MBRNO.toString())){
                    if (returnCI.getValue10().equals("")) {
                        log.debug("Begin transform special column: " + returnCI.getFieldName() + " for source file: "+ returnCI.getSourceName());
                        returnCI.setValue10(" ");
                    }
                }
            }
        }
        return returnCI;
    }
}

package com.island.tcbetl.transform;

import com.island.tcbetl.entity.ColumnInfo;
import lombok.Data;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

@Data
public abstract class TypeTransformer {
    private ColumnInfo columnInfo;
    private Map<String, ColumnInfo> columnMeta;
    private Map<String, String> typeMap;

    public TypeTransformer(ColumnInfo columnInfo, Map<String, ColumnInfo> columnMeta) {
        this.columnInfo = columnInfo;
        this.columnMeta = columnMeta;
    }

    public TypeTransformer(ColumnInfo columnInfo, Map<String, ColumnInfo> columnMeta, Map<String, String> typeMap) {
        this.columnInfo = columnInfo;
        this.columnMeta = columnMeta;
        this.typeMap = typeMap;
    }

    /**
     * Transform 16 byte String to 10 byte
     * @param raw16byteRowData 16 byte String
     * @return
     */
    public abstract ColumnInfo transform(String raw16byteRowData);

    /**
     * Get cell 16 byte String
     * @param raw16byteRowData
     * @return
     */
    public String getCell16byteString(String raw16byteRowData){
        return raw16byteRowData.substring(
                columnInfo.getRawStartIndex(), columnInfo.getRawStartIndex() + columnInfo.getField16Len()
        );
    }

    /**
     * Check nested column, and transforming if true.
     *
     * @return if true, finished nested column transform
     */
    boolean isNestColumn() {
        return this.columnInfo.getRedefineOfField() != null
                && !this.columnInfo.getRedefineOfField().isEmpty();
    }

    /**
     * Transform nested column.
     *
     * @return
     */
    String transNestedColumnFromParent() {
        String child10BytString = null;
        ColumnInfo childCI = this.columnInfo;
        ColumnInfo parentCI = this.columnMeta.get(childCI.getRedefineOfField());
        int childStringIndexForParent = 0;
        if (childCI.getRedefineSequence() > 1) {
            childStringIndexForParent =
                    this.columnMeta.values().stream()
                            .filter(
                                    columnInfo ->
                                            columnInfo.getRedefineOfField().equals(parentCI.getFieldName())
                                                    && columnInfo.getRedefineSequence() < childCI.getRedefineSequence())
                            .mapToInt(ColumnInfo::getFieldLen)
                            .sum();
        }
        if (parentCI.getValue10().isEmpty()) {
            child10BytString = "0";
        } else {
            child10BytString =
                    parentCI.getValue10().substring(
                                    childStringIndexForParent, childStringIndexForParent + childCI.getFieldLen()
                            );
        }
        return child10BytString;
    }

    /**
     * Transform 16 byte substring of XType to 10 byte String. For example: f0f1f1f0f0f0f0f0c1f0f6f0=>01100000A060
     * @param source16byte 16 byte substring
     * @return
     */
    String xTypeTrans(String source16byte) {
        String[] xTypePairWithTwoString = source16byte.split("(?<=\\G.{2})");
        String string10Byte = "";
        for(String s : xTypePairWithTwoString){
            string10Byte += this.typeMap.get(s.toUpperCase());
        }
        return string10Byte;
    }

    /**
     * Check String is numeric
     * @param strNum String of number
     * @return
     */
    boolean isNumeric(String strNum) {
        if (strNum == null || strNum.isEmpty()) {
            return false;
        }
        try {
            BigInteger bigInteger = new BigInteger(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Convert String to numeric. For instance, "000123400" and FieldScale equals 2, the final result
     * would be "1234.00"
     *
     * @param targetString String of target.
     * @return
     */
    String numericProcessed(String targetString, int scale) {
        String resultString = targetString;
        if (this.isNumeric(targetString)) {
            int offset = (int) Math.pow(10, scale);
            resultString =
                    new BigDecimal(String.valueOf(new BigDecimal(targetString).doubleValue() / offset))
                            .setScale(scale)
                            .toString();
        }
        return resultString;
    }
}

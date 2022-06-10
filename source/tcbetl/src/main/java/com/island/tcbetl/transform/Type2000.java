package com.island.tcbetl.transform;

import com.island.tcbetl.entity.ColumnInfo;

import java.util.Map;

/**
 * Numeric Type for result
 */
public class Type2000 extends TypeTransformer {

    public Type2000(ColumnInfo columnInfo, Map<String, ColumnInfo> columnMeta, Map<String, String> xTypeMap) {
        super(columnInfo, columnMeta, xTypeMap);
    }

    @Override
    public ColumnInfo transform(String raw16byteRowData) {
        ColumnInfo columnInfo = this.getColumnInfo();
        String cell10byteString = "";
        // Child Column
        if (this.isNestColumn()) {
            cell10byteString = this.transNestedColumnFromParent().trim();
        } else { // Normal Column
            // Get 16 byte column value
            String cell16byteString = this.getCell16byteString(raw16byteRowData);
            // Transform data from 16 to 10
            cell10byteString = this.xTypeTrans(cell16byteString);
            if (cell10byteString.contains("SP")) {
                cell10byteString = "";
            } else if (!this.isNumeric(cell10byteString)) {
                cell10byteString = "";
            } else {
                cell10byteString = this.numericProcessed(cell10byteString, columnInfo.getFieldScale());
            }
            columnInfo.setValue16(cell16byteString);
        }
        columnInfo.setValue10(cell10byteString);
        return columnInfo;
    }
}

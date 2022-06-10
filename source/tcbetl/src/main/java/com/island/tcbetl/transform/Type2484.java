package com.island.tcbetl.transform;

import com.island.tcbetl.entity.ColumnInfo;

import java.util.Arrays;
import java.util.Map;

/**
 * Numeric Type for result
 */
public class Type2484 extends TypeTransformer {

    public Type2484(ColumnInfo columnInfo, Map<String, ColumnInfo> columnMeta) {
        super(columnInfo, columnMeta);
    }

    @Override
    public ColumnInfo transform(String raw16byteRowData) {
        ColumnInfo columnInfo = this.getColumnInfo();
        String cell10byteString = "";
        // Child Column
        if (this.isNestColumn()) {
            cell10byteString = this.transNestedColumnFromParent();
        } else { // Normal Column
            // Get 16 byte column value
            String cell16byteString = this.getCell16byteString(raw16byteRowData);
            // Transform data from 16 to 10
            // "c" means Plus=>'+', "d" means Minus=>"-" , and "f" means none
            String[] lastCharacterOf2484Type = {"d", "c", "f"};
            String lastCharacter = cell16byteString.substring(cell16byteString.length() - 1);
            boolean contains = Arrays.asList(lastCharacterOf2484Type).contains(lastCharacter);
            if (contains) {
                String removeLastChar = cell16byteString.substring(0, cell16byteString.length() - 1);
                cell10byteString = this.numericProcessed(removeLastChar, columnInfo.getFieldScale());
                if (!this.isNumeric(removeLastChar)) {
                    cell10byteString = "";
                } else if (lastCharacter.equals("d")) {
                    cell10byteString += "-" + cell10byteString;
                }
            }
            columnInfo.setValue16(cell16byteString);
        }
        columnInfo.setValue10(cell10byteString);
        return columnInfo;
    }
}

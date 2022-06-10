package com.island.tcbetl.transform;

import com.island.tcbetl.entity.ColumnInfo;
import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Chinese Type for result, FieldCcsid = 11835
 */
public class Type2452C extends TypeTransformer {

    /**
     *
     * @param columnInfo
     * @param columnMeta
     * @param typeMap Chinese map of JIS and UCS4.0
     */
    public Type2452C(ColumnInfo columnInfo, Map<String, ColumnInfo> columnMeta, Map<String, String> typeMap) {
        super(columnInfo, columnMeta, typeMap);
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
            // Transform to chinese
            cell10byteString = this.getChineseFromUCS4(
                    this.getTypeMap().get(
                            this.getJISFromEBCDIC(cell16byteString)
                    )
            );
            columnInfo.setValue16(cell16byteString);
        }
        columnInfo.setValue10(cell10byteString);
        return columnInfo;
    }

    /**
     *
     * @param ebcdic EBCDIC
     * @return
     */
    private String getJISFromEBCDIC(String ebcdic) {
        //TODO: Setting mapping of EBCDIC and JIS with hardcode
        return "";
    }

    /**
     *
     * @param ucs4String
     * @return
     */
    private String getChineseFromUCS4(String ucs4String){
        String uft32String = StringUtils.leftPad(ucs4String, 8, '0');
        ByteBuffer buffUCS4 = getByteBuffer(uft32String);
        Charset cs_UCS4 = Charset.forName("UTF-32BE");
        return cs_UCS4.decode(buffUCS4).toString();
    }

    /**
     *
     * @param ucs4String
     * @return
     */
    private ByteBuffer getByteBuffer(String ucs4String) {
        ByteBuffer buff = ByteBuffer.allocate(ucs4String.length() / 2);

        for (int i = 0; i < ucs4String.length(); i += 2) {
            buff.put((byte) Integer.parseInt(ucs4String.substring(i, i + 2), 16));
        }
        buff.rewind();
        return buff;
    }
}

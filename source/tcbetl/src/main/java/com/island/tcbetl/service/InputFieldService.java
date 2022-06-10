package com.island.tcbetl.service;

import com.island.tcbetl.constants.SpecialColumn;
import com.island.tcbetl.entity.ColumnInfo;
import com.island.tcbetl.model.InputFields;
import com.island.tcbetl.repository.InputFieldsRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service(value = "InputFieldService")
public class InputFieldService {


    final List<Integer> doubleLenList = Arrays.asList( 2000, 2452, 2500 );
    final List<Integer> otherLenList = Arrays.asList(2484);

    @Autowired
    @Qualifier("InputFieldsRepository")
    private InputFieldsRepository inputFieldsRepository;

    /**
     *
     * @param sourceName File source name.
     * @return
     */
    public Map<String, ColumnInfo> loadFiledInfoBySourceName(String sourceName) {
        log.info("Start to load file : "+ sourceName +" field data from database.");
        List<InputFields> inputFieldList = inputFieldsRepository.findByNameOrderByFieldSequenceAsc(sourceName);
        if(inputFieldList.size()==0)
            throw new IllegalArgumentException("Source Name: "+ sourceName +" is not exist in database.");

        return getColumnInfoList(inputFieldList);
    }

    /**
     * Convert List<InputFields>  to Map<String, ColumnInfo>
     * @param inputFieldList
     * @return
     */
    private Map<String, ColumnInfo> getColumnInfoList(List<InputFields> inputFieldList) {
        Map<String, String> nestedParentColumn = new HashMap<>();
        Map<String, ColumnInfo> result = new LinkedHashMap();
        inputFieldList.forEach(
                f -> {
                    ColumnInfo ci = new ColumnInfo();
                    ci.setFieldName(f.getFieldName().trim());
                    ci.setFieldActive(f.getFieldActive().trim());
                    ci.setFieldLen(f.getFieldLen());
                    ci.setField16Len(this.getField16Len(f.getFieldType(),f.getFieldLen()));
                    ci.setFieldType(f.getFieldType());
                    ci.setFieldCcsid(f.getFieldCcsid());
                    ci.setFieldScale(f.getFieldScale());
                    ci.setFieldSequence(f.getFieldSequence());
                    ci.setSourceName(f.getSourceName().trim());
                    if (f.getRedefineOfField() != null) {
                        String field = f.getRedefineOfField().trim();
                        nestedParentColumn.put(field, field);
                        ci.setRedefineOfField(f.getRedefineOfField().trim());
                    }
                    ci.setRedefineSequence(f.getRedefineSequence());
                    result.put(ci.getFieldName(), ci);
                }
        );
        result.forEach(
                (k, v) ->{
                    // Checking nested Parent Column
                    v.isParentColumn(nestedParentColumn.containsKey(k));
                    // skip transform column for special rule.
                    if(SpecialColumn.SKIP_COLUMN_SOURCE.contains(v.getSourceName())) {
                        v.isSkipTransform(SpecialColumn.getSkipColumnBySource(v.getSourceName()).contains(k));
                    }
                }
        );

        return result;
    }

    /**
     * Get 16 byte of Length
     * @param typeNo
     * @param fieldLen
     * @return
     */
    private int getField16Len(int typeNo, int fieldLen){
        int doubleLen = 0;
        if(doubleLenList.contains(typeNo)) {
            doubleLen = fieldLen * 2;
        } else if(otherLenList.contains(typeNo)) {
            doubleLen = ((fieldLen / 2) + 1) * 2;
        }
        return doubleLen;
    }

}

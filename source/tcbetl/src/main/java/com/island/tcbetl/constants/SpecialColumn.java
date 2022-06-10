package com.island.tcbetl.constants;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpecialColumn {

    public enum SKIP_COLUMN_OF_CNTP_REC{
        TRNSDT,
        MBRNO
    }

    /**
     * Get SKIP COLUMN OF CNTP_REC
     * @return
     */
    private static List<String> getSkipColOfCNTPREC() {
        SKIP_COLUMN_OF_CNTP_REC[] array = SKIP_COLUMN_OF_CNTP_REC.values();
        List<String> resultList = new ArrayList<>();
        for(SKIP_COLUMN_OF_CNTP_REC s :array) {
            resultList.add(s.name());
        }
        return resultList;
    }

    /**
     * Get SKIP COLUMN OF source name
     * @param sourceName source name
     * @return
     */
    public static List<String> getSkipColumnBySource(String sourceName) {
        List<String> resultList = new ArrayList<>();
        if ("CNTP.REC".equals(sourceName)) {
            resultList = getSkipColOfCNTPREC();
        }
        return resultList;
    }

    /**
     * Get Skip Column Source List
     */
    public static final List<String> SKIP_COLUMN_SOURCE = Stream.of("CNTP.REC")
        .collect(Collectors.toList());


}

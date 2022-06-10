package com.island.filecomparing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service(value = "EntryService")
public class EntryService {

    private static final Logger logger = LoggerFactory.getLogger(EntryService.class);

    public void executed(String compareFilePath, String answerFilePath) throws Exception {
        StringBuilder sbResult = new StringBuilder("");
        List<String> compareList = readLineByLineToList(compareFilePath);
        List<String> answerList = readLineByLineToList(answerFilePath);
        if(compareList.size() != answerList.size()) {
            throw new IllegalArgumentException("Size of "+ compareFilePath + " and " +answerFilePath +" are different!");
        }
        for(int i = 0; i < compareList.size(); i++) {
            String[] compareRow = compareList.get(i).split("\\|", -1);
            String[] answerRow = answerList.get(i).split("\\|", -1);
            int checkRowColumnIndex = 0;
            for(int k = 0;k < compareRow.length; k++) {
                String compareValue = compareRow[checkRowColumnIndex];
                String answerValue = answerRow[checkRowColumnIndex];
                if (!compareValue.equals(answerValue)) {
                    sbResult.append("line ");
                    sbResult.append(i + 1);
                    sbResult.append(
                            " (expect value: " + answerValue + " , and transform result: " + compareValue + " )");
                    sbResult.append("\n");
                }
                checkRowColumnIndex += 1;
            }
        }
        logger.info("==================================Data Compare Result==================================");
        if(sbResult.toString().isEmpty()) {
            logger.info("Content values of two files "+ compareFilePath +" and " + answerFilePath +" are totally the same!");
        } else
            logger.info(sbResult.toString());

    }

    /**
     * Read csv file and convert to List<String>
     * @param filePath Source file path
     * @return List<String>
     */
    List<String> readLineByLineToList(String filePath) throws IOException {
        List<String> lineList = new ArrayList<>();
        Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8);
        stream.forEach(lineList::add);
        return lineList;
    }
}

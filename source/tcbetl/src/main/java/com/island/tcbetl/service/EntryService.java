package com.island.tcbetl.service;

import com.island.tcbetl.constants.ApEnv;
import com.island.tcbetl.entity.ColumnInfo;
import com.island.tcbetl.entity.EntryInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value = "EntryService")
public class EntryService {


    @Autowired
    @Qualifier("InputFieldService")
    InputFieldService inputFieldService;

    @Autowired
    @Qualifier("FileService")
    FileService fileService;

    @Autowired
    @Qualifier("DecodeService")
    DecodeService decodeService;

    @Autowired
    @Qualifier("XTypeService")
    XTypeService xTypeService;

    public void executed(EntryInput entryInput) throws Exception {
        File inputFile = new File(entryInput.getInputFolderPath());
        if(!inputFile.isDirectory())
            throw new IllegalArgumentException(entryInput.getInputFolderPath() + " is not a directory, Please confirm the property -Dinput.folder.path");

        List<File> fileList = fileService.getFileList(entryInput);

        if(entryInput.getApMode().equals(EntryInput.modeCompare)) {
            if (fileList.size() > 1)
                throw new IllegalArgumentException("There should be only one input file when -Dmode="
                        + EntryInput.modeCompare);
            File answerFile = new File(entryInput.getAnswerFilePath());
            if(!answerFile.isFile())
                throw new IllegalArgumentException(entryInput.getAnswerFilePath() + " is not a file, Please confirm the property -Danswer.file.path");
        } else {
            File outputFile = new File(entryInput.getOutputFolderPath());
            if(!outputFile.isDirectory())
                throw new IllegalArgumentException(entryInput.getOutputFolderPath() + " is not a directory, Please confirm the property -Doutput.folder.path");
        }

        Map<String, String> xType = xTypeService.loadFiledInfoBySourceName();
        Map<String, String> ucs4Map = new HashMap<>();
        for (File f : fileList) {
            File fileInProcess = null;
            try {
                log.info("Begin decoding source data: " + f.getName());
                //Copy file to process folder
                fileInProcess = fileService.copyFileTo(f, ApEnv.TRANSFORM_STATUS.process);
                Map<String, ColumnInfo> columnMeta = inputFieldService.loadFiledInfoBySourceName(f.getName());
                List<String> raw16byteData = fileService.getRawDataByRowWithFile(f);
                List<Map<String, ColumnInfo>> resultList = decodeService.executeDecode(raw16byteData, columnMeta, xType, ucs4Map);
                if (entryInput.getApMode().equals(EntryInput.modeCompare)) {
                    //Compare two files
                    String compareResult = fileService.getCompareResult(resultList, f.getName(), entryInput.getAnswerFilePath());
                    log.info("==================================Beginning Data Compare Result==================================");
                    if(compareResult.isEmpty()) {
                        log.info("Content values of decoding result: "+ f.getName() +" and answer file: " + entryInput.getAnswerFilePath() +" are totally the same!");
                    } else
                        log.warn("\n"+compareResult);
                    log.info("==================================End Data Compare Result==================================");
                } else {
                    //Save decoding result as a files.
                    fileService.saveToFile(entryInput, f.getName(), resultList);
                    log.info("Finished decoding source data: " + f.getName());
                }
            } catch(Exception ex) {
                //Move file to folder fail if catch any exception
                fileService.moveFileTo(fileInProcess, ApEnv.TRANSFORM_STATUS.fail);
                log.error(fileInProcess.getAbsolutePath() + " fail to decode. cause by: " + ex.getMessage());
            } finally {
                //Remove file from process folder if save file success or fail
                fileService.deleteFileFrom(fileInProcess);
            }
        }
    }

}

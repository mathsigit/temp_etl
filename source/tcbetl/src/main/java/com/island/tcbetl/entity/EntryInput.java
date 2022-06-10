package com.island.tcbetl.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryInput {
    public static final String modeCompare = "compare";
    /*
    Input path of folder path
     */
    String inputFolderPath;
    /*
    Output path of folder path
     */
    String outputFolderPath;
    /*
    Input file name. Accept wildcard characters, You can use the asterisk *.
    Example: TD.* means that all files whose names start with TD. will be processed
     */
    String fileName;
    /*
    File path of DWHDAY.TXT
     */
    String dateFilePath;

    /**
     * Path of compared answer file
     */
    String answerFilePath;

    /**
     * Application mode.
     * Saving decoding result as a file when ""; Only comparing and printing the result on console with answer file when 'compare' .
     */
    String apMode="";
}

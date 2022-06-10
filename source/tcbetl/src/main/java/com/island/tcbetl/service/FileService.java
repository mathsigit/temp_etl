package com.island.tcbetl.service;

import com.island.tcbetl.constants.ApEnv;
import com.island.tcbetl.entity.ColumnInfo;
import com.island.tcbetl.entity.EntryInput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Service(value = "FileService")
public class FileService {

    String ROW_SEPARATE_0D0A = "0d0a";

    /**
     *
     * @param targetFile Target file which copied to path
     * @param status File process status. process, fail and success.
     * @throws IOException
     */
    public File copyFileTo(File targetFile, ApEnv.TRANSFORM_STATUS status) throws IOException {
        String copyToPath = this.buildPathUsingStringJoiner(ApEnv.BASE_PATH_OF_JAR(), status.toString());
        copyToPath = this.buildPathUsingStringJoiner(copyToPath, targetFile.getName());
        File copyToFile = new File(copyToPath);
        FileUtils.copyFile(targetFile, copyToFile, REPLACE_EXISTING);
        log.info("Copied file "+ targetFile.getAbsolutePath() +" to " + copyToPath);
        return copyToFile;
    }

    /**
     *
     * @param targetFile Target file which copied to path
     * @param status File process status. process, fail and success.
     * @throws IOException
     */
    public void moveFileTo(File targetFile, ApEnv.TRANSFORM_STATUS status) throws IOException {
        String moveToPath = this.buildPathUsingStringJoiner(ApEnv.BASE_PATH_OF_JAR(), status.toString());
        moveToPath = this.buildPathUsingStringJoiner(moveToPath, targetFile.getName());
        File moveToFile = new File(moveToPath);
        if (moveToFile.exists()) {
            FileUtils.deleteQuietly(moveToFile);
        }
        FileUtils.moveFile(targetFile, moveToFile);
        log.info("Moved file "+ targetFile.getAbsolutePath() +" to " + moveToPath);
    }

    /**
     *
     * @param targetFile Target file which copied to path
     * @throws IOException
     */
    public void deleteFileFrom(File targetFile) throws IOException {
        if (targetFile.exists())
            FileUtils.delete(targetFile);
        log.info("Removed file "+ targetFile.getAbsolutePath());
    }

    /**
     * Split 16 byte file to a String List.
     * @param filePath File path which converted.
     * @return List String array contains 16 byte 'String' each row
     */
    public List<String> getRawDataByRowWithFile(File filePath) throws IOException {
        String encodeSource = decode16ByteAsString(filePath);
        String[] encodeSourceArray = encodeSource.split(ROW_SEPARATE_0D0A);
        return new ArrayList<>(Arrays.asList(encodeSourceArray));
    }

    /**
     * Transform 16 byte as String from file
     *
     * @param filePath 16 byte file path
     * @return 16 bytes content as 'String'
     */
    private String decode16ByteAsString(File filePath) throws IOException {
        FileInputStream fin = new FileInputStream(filePath);
        StringWriter sw = new StringWriter();
        int len = 1;
        byte[] temp = new byte[len];
        log.info("Start decode 16 byte as String from file: " + filePath.getName());
        while ((fin.read(temp, 0, len)) != -1) {
            if (temp[0] > 0xf & temp[0] <= 0xff) {
                sw.write(Integer.toHexString(temp[0]));
            } else if (temp[0] >= 0x0 & temp[0] <= 0xf) {
                sw.write("0" + Integer.toHexString(temp[0]));
            } else {
                sw.write(Integer.toHexString(temp[0]).substring(6));
            }
        }
        return sw.toString();
    }

    /**
     * Get File list from EntryInput.InputFolderPath with EntryInput.FileName
     * @param entryInput EntryInput object
     * @return List of File object
     */
    public List<File> getFileList(EntryInput entryInput) {
        File folder = new File(entryInput.getInputFolderPath());
        FileFilter fileFilter = new WildcardFileFilter(entryInput.getFileName());
        return Arrays.asList(Objects.requireNonNull(folder.listFiles(fileFilter)));
    }

    /**
     *
     * @param entryInput EntryInput object
     * @param fileName name of source file
     * @param fileSource File source which saved as a CSV file.
     * @throws IOException
     */
    public void saveToFile(EntryInput entryInput, String fileName, List<Map<String, ColumnInfo>> fileSource) throws IOException, ParseException {
        //.D File
        writeDFile(entryInput, fileName, fileSource);
        //.H File.
        writeHFile(entryInput, fileName, fileSource);
    }

    /**
     * Save .D file to the path
     * @param entryInput EntryInput object
     * @param fileName name of source file
     * @param fileSource File source which saved as a CSV file.
     * @throws IOException
     */
    void writeDFile(EntryInput entryInput, String fileName, List<Map<String, ColumnInfo>> fileSource) throws IOException {
        String dotDFileName = getDotFileName(entryInput, fileName, "D") ;
        String dotDFileFullPath = buildPathUsingStringJoiner(entryInput.getOutputFolderPath(), dotDFileName);
        Writer writer = getFileWriterWithUTF8(dotDFileFullPath);
        for (Map<String, ColumnInfo> ci : fileSource) {
            for (Map.Entry<String, ColumnInfo> e : ci.entrySet()) {
                String value10 = e.getValue().getValue10();
                if (e.getValue().getFieldType() == 2452) {
                    value10 = "\"" + value10 + "\"";
                }
                writer.append(value10 + "|");
            }
            writer.append("\n");
            writer.flush();
        }
        writer.close();
        log.info("Saved file : " + dotDFileFullPath + " successfully");
    }

    /**
     * Save .H file to the path. Total size of content must be 80.
     * @param entryInput EntryInput object
     * @param fileName name of source file
     * @param fileSource File source which saved as a CSV file.
     * @throws IOException
     * @throws ParseException
     */
    void writeHFile(EntryInput entryInput, String fileName, List<Map<String, ColumnInfo>> fileSource) throws IOException, ParseException {
        String dotHFileName = getDotFileName(entryInput, fileName, "H") ;
        String dotHFileFullPath = buildPathUsingStringJoiner(entryInput.getOutputFolderPath(), dotHFileName);
        SimpleDateFormat dateFormatCurrentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormatOfBeginAndEndDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormatDWHDAY = new SimpleDateFormat("yyyyMMdd");
        Date dwhDAY = dateFormatDWHDAY.parse(getDateFormDWHDAY(entryInput));
        Date currentDate = new Date();

        Writer writer = getFileWriterWithUTF8(dotHFileFullPath);
        writer.append(dateFormatOfBeginAndEndDate.format(dwhDAY));
        writer.append(dateFormatOfBeginAndEndDate.format(dwhDAY));
        writer.append(StringUtils.rightPad(getConvertFileName(fileName),32," "));
        writer.append(dateFormatCurrentDate.format(currentDate));
        writer.append(StringUtils.leftPad(String.valueOf(fileSource.size()),9,"0"));
        writer.flush();
        writer.close();
        log.info("Saved file : " + dotHFileFullPath + " successfully");
    }

    /**
     *
     * @param fileFullPath Full file path which saved.
     * @return
     * @throws FileNotFoundException
     */
    Writer getFileWriterWithUTF8(String fileFullPath) throws FileNotFoundException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(fileFullPath),
                        StandardCharsets.UTF_8
                )
        );
    }

    /**
     * Get file name which converted from '.' to '_'
     * @param fileName name of source file
     * @return Input 'ABC.REC' and get 'ABC_REC'
     */
    String getConvertFileName(String fileName) {
        return fileName.replace(".","_");
    }

    /**
     *
     * @param entryInput EntryInput object
     * @return Date of DWHDAY.txt content.
     * @throws IOException
     */
    String getDateFormDWHDAY(EntryInput entryInput) throws IOException {
        File fileDWHDAY = new File(entryInput.getDateFilePath());
        String[] content = FileUtils.readFileToString(fileDWHDAY, "UTF-8").split(",");
        return content[0];
    }

    /**
     *
     * @param entryInput EntryInput object
     * @param fileName name of source file
     * @param type D or H
     * @throws IOException
     */
    String getDotFileName(EntryInput entryInput, String fileName, String type) throws IOException {
        String currentDateString = getDateFormDWHDAY(entryInput);
        String returnName = getConvertFileName(fileName) + "_" + currentDateString;
        switch (type) {
            case "D":
                returnName += ".D";
                break;
            case "H":
                returnName += ".H";
                break;
            default:
                throw new IllegalArgumentException("Type: "+ type + " is a illegal type. It only D or H");
        }
        return  returnName;
    }

    String buildPathUsingStringJoiner(String path1, String path2) {
        StringJoiner joiner = new StringJoiner(File.separator);
        joiner.add(path1);
        joiner.add(path2);
        return joiner.toString();
    }

    /**
     * Comparing decoding result with the answer file.
     * @param compareSource Decoding result list
     * @param inputFilePath File path which decoded
     * @param answerFilePath File path of the answer which compared with "compareSource"
     * @return
     * @throws IOException
     */
    String getCompareResult(List<Map<String, ColumnInfo>> compareSource, String inputFilePath, String answerFilePath) throws IOException {
        StringBuilder sbResult = new StringBuilder("");
        List<String> answerList = readLineByLineToList(answerFilePath);
        if(compareSource.size() != answerList.size()) {
            throw new IllegalArgumentException("Size of "+ inputFilePath +" and " +answerFilePath +" are different!");
        }
        for (int i = 0; i < answerList.size(); i++) {
            String[] checkRow = answerList.get(i).split("\\|", -1);
            int checkRowColumnIndex = 0;
            for (Map.Entry<String, ColumnInfo> m : compareSource.get(i).entrySet()) {
                String targetValue = m.getValue().getValue10();
                String checkValue = checkRow[checkRowColumnIndex];
                if (m.getValue().getFieldType() == 2452) {
                    targetValue = "\"" + targetValue + "\"";
                }
                if (!targetValue.equals(checkValue)) {
                    sbResult.append("line ");
                    sbResult.append(i + 1);
                    sbResult.append(" , and column name: " + m.getKey() + " error occurs :");
                    sbResult.append(
                            " (expect value: " + checkValue + " , and transform result: " + targetValue + " )");
                    sbResult.append("\n");
                }
                checkRowColumnIndex += 1;
            }
        }
        return sbResult.toString();
    }

    /**
     * Read csv file and convert to List<String>
     * @param filePath Source file path
     * @return List<String>
     */
    private List<String> readLineByLineToList(String filePath) throws IOException {
        List<String> lineList = new ArrayList<>();
        Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8);
        stream.forEach(lineList::add);
        return lineList;
    }
}

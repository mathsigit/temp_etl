package com.island.tcbetl.service;

import com.island.tcbetl.entity.EntryInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
public class FileServiceTest {

    private final FileService fileService = new FileService();
    private EntryInput entryInput;

    @Before
    public void before() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("input")).getFile());
        entryInput = EntryInput.builder()
                .inputFolderPath(file.getAbsolutePath())
                .outputFolderPath(file.getAbsolutePath())
                .dateFilePath(file.getAbsolutePath()+"/DWHDAY.TXT")
                .fileName("CNTP.*")
                .build();
    }

    @Test
    public void getFileListTest() {
        List<File> fileList = fileService.getFileList(entryInput);
        String[] fileNameList = {"CNTP.REC","CNTP.AD"};
        Assert.assertEquals(2, fileList.size());
        fileList.forEach(
                f->{
                    Assert.assertTrue(Arrays.asList(fileNameList).contains(f.getName()));
                }
        );

    }

    @Test
    public void getDotFileNameTest() throws IOException {
        String fileName = "CNTP.REC";
        String dotDFileName = fileService.getDotFileName(entryInput, fileName,"D");
        String dotHFileName = fileService.getDotFileName(entryInput, fileName,"H");
        Assert.assertEquals("CNTP_REC_20220601.D",dotDFileName);
        Assert.assertEquals("CNTP_REC_20220601.H",dotHFileName);
    }
}
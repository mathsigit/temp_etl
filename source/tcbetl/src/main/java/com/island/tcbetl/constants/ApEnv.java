package com.island.tcbetl.constants;

import java.io.File;
import java.io.IOException;

public class ApEnv {

    public static String BASE_PATH_OF_JAR() throws IOException {
        return new File(".").getCanonicalPath();
    }

    public enum TRANSFORM_STATUS{
        process,
        fail,
        success
    }
}

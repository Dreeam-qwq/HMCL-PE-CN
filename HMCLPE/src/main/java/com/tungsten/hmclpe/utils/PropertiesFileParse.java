package com.tungsten.hmclpe.utils;

import android.content.Context;
import java.io.*;
import java.util.*;

public class PropertiesFileParse {
    private Properties properties;
    public PropertiesFileParse(String propertiesFileName, Context context) {
        properties = new Properties();
        try {
            InputStream in = context.getAssets().open(propertiesFileName);
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public PropertiesFileParse(String propertiesFile) {
        properties = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(propertiesFile));
            properties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public Properties getProperties() {
        return properties;
    }
}
package com.jonas.breathinganalysis;


import android.annotation.SuppressLint;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class DataLogger {

    DataLogger(MeasuredData measuredData) {
        if(externalStorageIsWritable()) {
            try {
                log(measuredData.getMeasuredDataSequence());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Problem logging the measured data to the external storage!");
        }
    }

    private static boolean externalStorageIsWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private static void log(List<String[]> list) throws IOException {
        //checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //System.out.println("isExternalStorageWritable(): " + externalStorageIsWritable());

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, getCurrentDateTime() + "-measuredData.csv");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
        CSVWriter writer;

        System.out.println("file.exists(): " + file.exists());
        // File exist
        if(file.exists() && !file.isDirectory()){
            return;
        }
        else {
            System.out.println("Creating File: " + file.getName());
            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()), ';');
        }

        writer.writeAll(list, true);

        writer.close();
    }

    private static String getCurrentDateTime() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return dateFormat.format(new Date());
    }
}

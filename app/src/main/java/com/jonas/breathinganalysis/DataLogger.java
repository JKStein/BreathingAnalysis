package com.jonas.breathinganalysis;


import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

class DataLogger {

    DataLogger(MeasuredData measuredData) {

    }

    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    static void log(String key, long timestamp, float value) throws IOException {
        //checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        System.out.println("isExternalStorageWritable(): " + isExternalStorageWritable());

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "Pitch.csv");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
        CSVWriter writer;

        System.out.println("file.exists(): " + file.exists());
        // File exist
        if(file.exists() && !file.isDirectory()){
            return;
            //FileWriter mFileWriter = new FileWriter(file.getName() , true);
            //writer = new CSVWriter(mFileWriter);
        }
        else {
            System.out.println("shit");

            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()));
        }
        String[] data = {Long.toString(timestamp),String.valueOf(value)};
        String[] titles = {"timestamp", "pitch"};

        System.out.println("Timestamp: " + timestamp + "\t + value: " + value);

        //Titles
        writer.writeNext(titles);

        writer.writeNext(data);

        System.out.println("Wrote data | Timestamp: " + timestamp + "\t + value: " + value);

        writer.close();
    }
}

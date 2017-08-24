package com.jonas.breathinganalysis;


import android.annotation.SuppressLint;
import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

class DataLogger {

    DataLogger(MeasuredData measuredData) {
        if(externalStorageIsWritable()) {
            try {
                log(measuredData.getMeasuredDataSequence());
                logToTxt(measuredData.getTxtOverhead(), measuredData.getMeasuredDataSeries());
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
            //FileWriter mFileWriter = new FileWriter(file.getName() , true);
            //writer = new CSVWriter(mFileWriter);
        }
        else {
            System.out.println("Creating File: " + file.getName());
            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()), ';');
        }

        /*String[] data = {Long.toString(timestamp),String.valueOf(value)};
        String[] titles = {"timestamp", "acceleration for x-axis"};

        System.out.println("Timestamp: " + timestamp + "\t + value: " + value);*/


        writer.writeAll(list, true);

        writer.close();
    }

    private static void logToTxt(String[] overhead,  List<String> measurementSeries) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, getCurrentDateTime() + "-measuredDataForWEKA.txt");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
        FileOutputStream fileOutput = new FileOutputStream(file, true);
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutput);
        for (String anOverheadLine : overhead) {
            outputStreamWriter.write(anOverheadLine);
        }
        outputStreamWriter.write("\n@data\n");
        for (String measurement: measurementSeries) {
            outputStreamWriter.write(measurement);
        }

        outputStreamWriter.close();

        fileOutput.flush();
        fileOutput.close();

    }

    private static String getCurrentDateTime() {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return dateFormat.format(new Date());
    }
}

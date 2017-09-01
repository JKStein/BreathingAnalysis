package com.jonas.breathinganalysis;


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
import java.util.Locale;

/**
 * Tries to save the supplied list to a comma separated value file on the external storage of the android device.
 * To be more precise: in the 'Documents' directory.
 */
class DataLogger {

    /**
     * Checks if the external storage is writable. If so, the data will be written there.
     * @param list The data to be saved in a .csv file.
     */
    DataLogger(List<String[]> list, List<String> list2) {
        if(externalStorageIsWritable()) {
            try {
                log(list);
                log2(list2);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Problem logging the measured data to the external storage!");
        }
    }

    /**
     * Checks if the external storage is writable.
     * @return true if it is; false if its not.
     */
    private static boolean externalStorageIsWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Saves the supplied list to a comma separated value file.
     * @param list The data to be saved in a .csv file.
     * @throws IOException Gets thrown if the file cannot be created for any reason.
     */
    private static void log(List<String[]> list) throws IOException {
        //checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, getCurrentDateTime() + "-measuredData.csv");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
        CSVWriter writer;

        System.out.println("file.exists(): " + file.exists());

        if(file.exists() && !file.isDirectory()){
            //TODO try to implement a solution for this; the file should still be saved!
            return;
        }
        else {
            System.out.println("Creating File: " + file.getName());
            writer = new CSVWriter(new FileWriter(file.getAbsolutePath()), ';');
        }

        writer.writeAll(list, true);

        writer.close();
    }

    private static void log2(List<String> list) throws IOException {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, getCurrentDateTime() + "-measuredData.arff");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());
        FileOutputStream fileOutput = new FileOutputStream(file, true);
        OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutput);
        for (String line : list) {
            outputStreamWriter.write(line);
        }
        outputStreamWriter.close();

        fileOutput.flush();
        fileOutput.close();

    }

    /**
     * Generates the current date time in the format "yyyy-MM-dd-HH-mm-ss"
     * @return The current date time in the format "yyyy-MM-dd-HH-mm-ss"
     */
    private static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.GERMAN);
        return dateFormat.format(new Date());
    }
}

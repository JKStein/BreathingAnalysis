package com.jonas.breathinganalysis;


import android.os.Environment;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Tries to save the supplied list to a comma separated value file on the external storage of the android device.
 * To be more precise: in the 'Documents' directory.
 */
class DataLogger {

    /**
     * Checks if the external storage is writable. If so, the data will be written there.
     * @param list The data to be saved in a .csv file.
     */
    /*DataLogger(List<String[]> list, List<String> list2, Context context) {
        CharSequence text = "";
        if(externalStorageIsWritable()) {
            try {
                log(list);
                log2(list2);
                text = "Saving successful!";
            }
            catch (IOException e) {
                e.printStackTrace();
                text = "Saving failed!";
            }
        }
        else {
            text = "External storage not writable!";
            System.out.println("Problem logging the measured data to the external storage!");
        }
        final int duration = Toast.LENGTH_SHORT;

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }*/

    static boolean writeToFiles(List<String[]> list, List<String> list2, File csvFile, File arffFile) {
        if(externalStorageIsWritable()) {
            try {
                log(list, csvFile);
                log2(list2, arffFile);
                return false;//"Saving successful!";
            }
            catch (IOException e) {
                e.printStackTrace();
                return true;//"Saving failed!";
            }
        }
        else {
            System.out.println("Problem logging the measured data to the external storage!");
            return true;//"External storage not writable!";
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
    private static void log(List<String[]> list, File csvFile) throws IOException {
        CSVWriter writer;

        if(csvFile.exists() && !csvFile.isDirectory()){
            System.out.println("The file already exists or is a directory!");
            return;
        }
        else {
            System.out.println("Creating File: " + csvFile.getName());
            writer = new CSVWriter(new FileWriter(csvFile.getAbsolutePath()), ';');
        }

        writer.writeAll(list, true);

        writer.close();
    }

    private static void log2(List<String> list, File arffFile) throws IOException {
        /*File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(path, getCurrentDateTime() + "-measuredData.arff");
        System.out.println("file.getAbsolutePath(): " + file.getAbsolutePath());*/
        FileOutputStream fileOutput = new FileOutputStream(arffFile, true);
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
    /*private static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.GERMAN);
        return dateFormat.format(new Date());
    }*/

    static File getFilePath(String playerName, String instrument, String exerciseName, String fileType) {
        File directory =  new File(Environment.getExternalStorageDirectory()
                + File.separator + "BreathingAnalysis"
                + File.separator + playerName
                + File.separator + instrument
                + File.separator + exerciseName);
        System.out.println("Directory: " + directory.getAbsolutePath());
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        System.out.println("path: " + path.getAbsolutePath());

        if(!directory.mkdirs()) {
            System.out.println("Failed creating the Directories!");
        }
        if(directory.exists()) {
            System.out.println("Directory already exists!");
        }

        String fileName = retrieveRecordingID(getFiles(directory)) + fileType;

        return new File(directory, fileName);
    }

    private static ArrayList<File> getFiles(File path) {
        ArrayList<File> files = new ArrayList<>();
        if(path.listFiles() == null) {
            return files;
        }
        for (File f : path.listFiles()) {
            if(f.isFile())
                files.add(f);
        }
        return files;
    }

    private static int retrieveRecordingID(ArrayList<File> files) {
        int max = Integer.MIN_VALUE;

        for (File file : files) {
            System.out.println("file.getName(): " + file.getName());
            int value = Integer.parseInt(file.getName().split("\\.")[0]);

            if(value > max) {
                max = value;
            }
        }
        if(max < 0) {
            return 0;
        }
        return (max + 1);
    }
}

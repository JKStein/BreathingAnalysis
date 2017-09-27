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
 * Tries to store the supplied lists in the respectively suiting files
 * on the external storage of the android device.
 */
class DataLogger {

    /**
     * Checks if the external storage is writable. If so, the data will be written there.
     * @param csvList The data to be saved to a Comma-separated values file.
     * @param arffList The data to be saved to an Attribute-Relation File Format file.
     * @param csvFile The destination file of the Comma-separated values file.
     * @param arffFile The destination file of the Attribute-Relation File Format file.
     * @return True if saving was successful, false if it failed.
     */
    static boolean writeToFiles(List<String[]> csvList, List<String> arffList, File csvFile, File arffFile) {
        if(externalStorageIsWritable()) {
            try {
                logToCSV(csvList, csvFile);
                logToARFF(arffList, arffFile);
                return false;
            }
            catch (IOException e) {
                e.printStackTrace();
                return true;
            }
        }
        else {
            return true;
        }
    }

    /**
     * Checks if the external storage is writable.
     * @return True, if it is; false, if its not.
     */
    private static boolean externalStorageIsWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * Stores the supplied list in a comma separated value file.
     * @param list The data to be saved to a Comma-separated values file.
     * @param csvFile The destination file of the Comma-separated values file.
     * @throws IOException Gets thrown if the file cannot be created for any reason.
     */
    private static void logToCSV(List<String[]> list, File csvFile) throws IOException {
        CSVWriter writer;

        if(csvFile.exists() && !csvFile.isDirectory()){
            return;
        }
        else {
            writer = new CSVWriter(new FileWriter(csvFile.getAbsolutePath()), ';');
        }

        writer.writeAll(list, true);

        writer.close();
    }

    /**
     * Stores the supplied list in an Attribute-Relation File Format file.
     * @param list The data to be stored in an Attribute-Relation File Format file.
     * @param arffFile The destination file of the Attribute-Relation File Format file.
     * @throws IOException Gets thrown if the file cannot be created for any reason.
     */
    private static void logToARFF(List<String> list, File arffFile) throws IOException {
        FileOutputStream fileOutput = new FileOutputStream(arffFile, true);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutput);
        for (String line : list) {
            outputStreamWriter.write(line);
        }
        outputStreamWriter.close();

        fileOutput.flush();
        fileOutput.close();
    }

    /**
     *
     * @param playerName The name of the recorded player.
     * @param instrument The name of the recorded instrument.
     * @param exerciseName The name of the recorded exercise.
     * @param fileType The type of file to be searched for, typically either ".arff" or ".csv"
     * @return A new File in which the recording can be stored.
     */
    static File getFilePath(String playerName, String instrument, String exerciseName, String fileType) {
        File directory =  new File(Environment.getExternalStorageDirectory()
                + File.separator + "BreathingAnalysis"
                + File.separator + playerName
                + File.separator + instrument
                + File.separator + exerciseName);
        String fileName = getRecordingID(scanForFiles(directory)) + fileType;

        if(!directory.exists()) {
            if(!directory.mkdirs()) {
                return null;
            }
        }

        return new File(directory, fileName);
    }

    /**
     * Retrieves an ArrayList of a files in the supplied directory.
     * @param directory The directory to scan.
     * @return An ArrayList containing all files in the passed directory.
     */
    private static ArrayList<File> scanForFiles(File directory) {
        ArrayList<File> files = new ArrayList<>();
        if(directory.listFiles() == null) {
            return files;
        }
        for (File f : directory.listFiles()) {
            if(f.isFile())
                files.add(f);
        }
        return files;
    }

    /**
     * Finds the next unused recording ID in the supplied files.
     * @param files An ArrayList containing all previous recordings.
     * @return The smallest unused integer that is bigger than all previously used IDs.
     */
    private static int getRecordingID(ArrayList<File> files) {
        int max = Integer.MIN_VALUE;

        for (File file : files) {
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

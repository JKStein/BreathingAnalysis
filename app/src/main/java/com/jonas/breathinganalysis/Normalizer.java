package com.jonas.breathinganalysis;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import java.util.ArrayList;
import java.util.List;

class Normalizer {
    /**
     * interpolates the missing values of an index-value paired array in which
     * the index array does not ascend in steps of one
     * @param timestamps the array of indices
     * @param values the array of values
     * @return an array with millisecond subsequent values
     */
    static double[] interpolate(double[] timestamps, double[] values) {
        if(timestamps.length <= 0) {
            return new double[0];
        }

        double startTime = timestamps[0];
        double endTime = timestamps[timestamps.length-1];
        int lengthOfTimeSeries = (int) (endTime - startTime);

        SplineInterpolator splineInterpolator = new SplineInterpolator();
        PolynomialSplineFunction f = splineInterpolator.interpolate(timestamps, values);

        double[] result = new double[lengthOfTimeSeries];

        for (double i = startTime; i < endTime; i++) {
            result[(int) (i - startTime)] = f.value(i);
        }
        return result;
    }

    /**
     * Instantiates the double arrays representing the information from a corresponding
     * Sound ArrayList in measuredData
     * @param measuredData the series of measurement containing the ArrayList to process
     */
    static void instantiateAudioDataArrays(MeasuredData measuredData) {
        ArrayList<Sound> list = measuredData.getAudio();
        int length = list.size();

        if(length <= 0) {
            return;
        }

        double[] timestamps = new double[length];
        double[] pitches = new double[length];
        double[] probabilities = new double[length];
        double[] spls = new double[length];

        for(Sound dataEntry : list) {
            int i = list.indexOf(dataEntry);
            timestamps[i] = dataEntry.getTimestamp();
            pitches[i] = dataEntry.getPitch();
            probabilities[i] = dataEntry.getProbability();
            spls[i] = dataEntry.getSpl();
        }

        measuredData.setAudioArrays(timestamps, pitches, probabilities, spls);
    }

    /**
     * Instantiates the double arrays representing the information from a corresponding
     * SensorDate ArrayList in measuredData
     * @param measuredData the series of measurement containing the ArrayList to process
     * @param dataType the data type of the ArrayList to normalize
     */
    static void instantiateSensorDataArrays(MeasuredData measuredData, DataType dataType) {
        //dataType.print();
        ArrayList<SensorDate> list = measuredData.getArrayList(dataType);
        int length = list.size();

        if(length <= 0) {
            System.out.println("Shiiiiit!");
            return;
        }

        double[] timestamps = new double[length];
        double[] xValues = new double[length];
        double[] yValues = new double[length];
        double[] zValues = new double[length];

        int i = 0;

        for(SensorDate dataEntry : list) {
            //dataEntry.print();
            //System.out.println("i: " + i);
            timestamps[i] = dataEntry.getTimestamp();
            xValues[i] = dataEntry.getXValue();
            yValues[i] = dataEntry.getYValue();
            zValues[i] = dataEntry.getZValue();
            i++;
        }



        switch (dataType) {
            case ACCELERATION:
                measuredData.setAccelerationArrays(timestamps, xValues, yValues, zValues);
                break;
            case ROTATION:
                measuredData.setRotationArrays(timestamps, xValues, yValues, zValues);
                break;
            case MAGNET:
                measuredData.setMagnetArrays(timestamps, xValues, yValues, zValues);
                break;
        }
    }

    static void printDoubleArray(double[] array) {
        for (int i = 0; i < array.length; i++) {
            System.out.println("array[" + i + "]: " + array[i]);
        }
    }

    /**
     * Calculates the average of lost information by deleting a redundant entry
     * @param list the sensor data to process
     * @return the average of lost information by deleting a redundant entry
     */
    static double averageErrorOfSonsorDataRedundancies(ArrayList<SensorDate> list) {
        int amountOfRedundancies = 0;
        double overallRoundingError = 0;
        double averageEstimationError = 0;

        for(int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i-1).getTimestamp() >= list.get(i).getTimestamp()) {
                amountOfRedundancies++;
                overallRoundingError += Math.abs(list.get(i).getXValue() - list.get(i-1).getXValue());
                overallRoundingError += Math.abs(list.get(i).getYValue() - list.get(i-1).getYValue());
                overallRoundingError += Math.abs(list.get(i).getZValue() - list.get(i-1).getZValue());
            }
        }
        if(amountOfRedundancies > 0) {
            averageEstimationError = overallRoundingError/(amountOfRedundancies * 3);
        }
        return averageEstimationError;
    }

    /**
     * deletes the redundant entries
     * @param list the sensor data to process
     */
    static void removeSonsorDataRedundancies(ArrayList<SensorDate> list) {
        for(int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i-1).getTimestamp() >= list.get(i).getTimestamp()) {
                list.remove(i);
                i--;
                //without the i-- we would miss every list entry right after a redundant one
            }
        }
        System.out.println("list.size(): " + list.size());
    }

    /**
     * deletes the redundant entries
     * @param list the audio data to process
     */
    static void removeAudioDataRedundancies(ArrayList<Sound> list) {
        for(int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i-1).getTimestamp() >= list.get(i).getTimestamp()) {
                list.remove(i);
                i--;
                //without the i-- we would miss every list entry right after a redundant one
            }
        }
    }

    /**
     * creates a double array with integer steps of one starting from the first entry
     * of the input array and ending on the last entry of the input array
     * @param timestamps the incomplete timestamp array (all entries should have an intger value!)
     * @return a normalized timestamp array (all entries have an intger value!)
     */
    static double[] normalizeTimestampArray(double[] timestamps) {
        if(timestamps.length <= 0) {
            System.out.println("Ssydifb");
            return new double[0];
        }
        long start = (long) timestamps[0];
        long end = (long) timestamps[timestamps.length - 1];
        int length = (int) (end - start);

        System.out.println("timestamps.length: " + timestamps.length);
        System.out.println("start: " + timestamps[0]);
        System.out.println("end: " + timestamps[timestamps.length - 1]);
        System.out.println("length: " + length);

        double[] result = new double[length];
        for(int i = 0; i < length; i++) {
            result[i] = start + i;
        }
        return result;
    }


    static double[] shrinkArray(final int startIndex, final int endIndex, final double[] array) {
        int length = endIndex - startIndex + 1;
        double[] result = new double[length];
        System.arraycopy(array, startIndex, result, 0, length);
        return result;
    }

    static void instantiateMeasuredDataSequence(MeasuredData measuredData) {
        List<String[]> measuredDataSequence = new ArrayList<>();

        String[] columnHeadings = {"Timestamp", "x-axis acceleration", "y-axis acceleration", "z-axis acceleration",
                "x-axis rotation", "y-axis rotation", "z-axis rotation", "x-axis magnetic", "y-axis magnetic",
                "z-axis magnetic", "pitch", "probability", "spl"};

        measuredDataSequence.add(columnHeadings);

        for(int i = 0; i < measuredData.getAccelerationTimestamps().length; i++) {
            String timestamp = String.valueOf(i);
            String xAcceleration = Double.toString(measuredData.getAccelerationXValues()[i]);
            String yAcceleration = Double.toString(measuredData.getAccelerationYValues()[i]);
            String zAcceleration = Double.toString(measuredData.getAccelerationZValues()[i]);

            String xGyroscope = Double.toString(measuredData.getRotationXValues()[i]);
            String yGyroscope = Double.toString(measuredData.getRotationYValues()[i]);
            String zGyroscope = Double.toString(measuredData.getRotationZValues()[i]);

            String xMagnetometer = Double.toString(measuredData.getMagnetXValues()[i]);
            String yMagnetometer = Double.toString(measuredData.getMagnetYValues()[i]);
            String zMagnetometer = Double.toString(measuredData.getMagnetZValues()[i]);

            String pitch = Double.toString(measuredData.getAudioPitches()[i]);
            String probability = Double.toString(measuredData.getAudioProbabilities()[i]);
            String spl = Double.toString(measuredData.getAudioSpls()[i]);

            String[] data = {timestamp, xAcceleration, yAcceleration, zAcceleration,
                    xGyroscope, yGyroscope, zGyroscope, xMagnetometer, yMagnetometer, zMagnetometer,
                    pitch, probability, spl};
            measuredDataSequence.add(data);
        }

        measuredData.setMeasuredDataSequence(measuredDataSequence);
    }
}
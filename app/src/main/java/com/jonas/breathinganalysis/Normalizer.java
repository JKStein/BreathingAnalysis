package com.jonas.breathinganalysis;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;
import static java.lang.Math.pow;

class Normalizer {

    private static final int MIDITABLESIZE = 128;


    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
    /*static double averageErrorOfSensorDataRedundancies(ArrayList<SensorDate> list) {
        int amountOfRedundancies = 0;
        double overallRoundingError = 0;
        double averageEstimationError = 0;

        for(int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i-1).getTimestamp() >= list.get(i).getTimestamp()) {
                amountOfRedundancies++;
                overallRoundingError += abs(list.get(i).getXValue() - list.get(i-1).getXValue());
                overallRoundingError += abs(list.get(i).getYValue() - list.get(i-1).getYValue());
                overallRoundingError += abs(list.get(i).getZValue() - list.get(i-1).getZValue());
            }
        }
        if(amountOfRedundancies > 0) {
            averageEstimationError = overallRoundingError/(amountOfRedundancies * 3);
        }
        return averageEstimationError;
    }*/

    /**
     * deletes quasi redundant entries
     * sometimes, more than one sensor update happens during a millisecond,
     * in such a case this method deletes all 'redundant' (jet different)
     * data entries but the first of each millisecond
     * @param list the sensor data to process
     */
    static void removeSensorDataRedundancies(ArrayList<SensorDate> list) {
        if(list != null) {

            for (int i = 0; i < list.size(); i++) {
                if (i > 0 && list.get(i - 1).getTimestamp() >= list.get(i).getTimestamp()) {
                    list.remove(i);
                    i--;
                    //without the i-- we would miss every list entry right after a redundant one
                }
            }
            System.out.println("list.size(): " + list.size());
        }
    }



    /*static double[] shrinkArray(final int startIndex, final int endIndex, final double[] array) {
        int length = endIndex - startIndex + 1;
        double[] result = new double[length];
        System.arraycopy(array, startIndex, result, 0, length);
        return result;
    }*/

    /*static double[] filledPercussionArray(final double startTimestamp, final double endTimestamp, final double[] array) {
        int length = (int) (endTimestamp - startTimestamp) + 1;
        double[] result = new double[length];
        for(int i = 0; i < length; i++) {
            if(contains(array, i + startTimestamp)) {
                result[i] = 1;
            }
            else{
                result[i] = 0;
            }
        }
        return result;
    }



    /**
     * For .csv file.
     * @param measuredData
     */
    static void instantiateMeasuredDataSequenceNew(MeasuredData measuredData) {
        List<String[]> measuredDataSequence = new ArrayList<>();

        String[] columnHeadings = {"Timestamp"};

        for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
            columnHeadings = ArrayUtils.addAll(columnHeadings,measurementSeries.getValues());
        }

        measuredDataSequence.add(columnHeadings);

        for(long i = measuredData.getSmallestStartTimestamp(); i < measuredData.getBiggestEndTimestamp(); i++) {

            String[] measurementAtASpecificTimestamp = {String.valueOf(i)};

            for (MeasurementSeries measurementSeries : measuredData.getAllMeasuredData()) {
                measurementAtASpecificTimestamp = ArrayUtils.addAll(measurementAtASpecificTimestamp,measurementSeries.contains(i));
            }

            measuredDataSequence.add(measurementAtASpecificTimestamp);
        }

        measuredData.setMeasuredDataSequence(measuredDataSequence);
    }


    /**
     * For .arff file.
     * @param //measuredData
     */
    /*static void instantiateMeasuredDataSeries(MeasuredData measuredData) {
        String[] overhead = new String[480000];//TODO: fix magic number (use metronome class)
        List<String> measuredDataSeries = new ArrayList<>();

        String[] columnHeadings = {"x-axis acceleration", "y-axis acceleration", "z-axis acceleration",
                "x-axis rotation", "y-axis rotation", "z-axis rotation", "x-axis magnetic", "y-axis magnetic",
                "z-axis magnetic", "pitch", "probability", "spl", "MIDI note", "deviation", "percussion"};

        int j = 0;
        for (String columnHeading : columnHeadings) {
            for (int i = 0; i < 32000; i++) {
                //System.out.println("a: " + i);
                overhead[i + j*32000] = "@attribute " + "'" + columnHeading + " " + i + "'" + " numeric\n";
            }
            j++;
        }

        for (int i = 0; i < 32000; i++) {
            //System.out.println("b: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAccelerationXValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("c: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAccelerationYValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("d: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAccelerationZValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("e: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getRotationXValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("f: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getRotationYValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("g: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getRotationZValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("h: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getMagnetXValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("i: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getMagnetYValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("j: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getMagnetZValues()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("k: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAudioPitches()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("l: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAudioProbabilities()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("m: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAudioSpls()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("n: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAudioMidiNoteNumber()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("o: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getAudioNoteDeviation()[i]) + ",");
        }
        for (int i = 0; i < 32000; i++) {
            //System.out.println("p: " + i);
            measuredDataSeries.add(Double.toString(measuredData.getPercussionSignal()[i]) + ",");
        }
        System.out.println("q");

        measuredData.setTxtOverhead(overhead);


        measuredData.setMeasuredDataSeries(measuredDataSeries);
    }*/

    static int getMidiNote(float frequency, float[] midiTable) {
        if(frequency < midiTable[0] || frequency > midiTable[MIDITABLESIZE - 1]) {
            return -1;
        }

        float deviation = Float.MAX_VALUE;
        int midiNote = -1;

        for(int i = 0; i < MIDITABLESIZE; i++) {
            if(abs(deviation) > abs(frequency - midiTable[i])) {
                deviation = frequency - midiTable[i];
                midiNote = i;
            }
        }
        return midiNote;
    }

    /**
     * calculates the deviation of the note in cents
     * @param frequency the measured frequency of the note
     * @param midiTable the MIDI table initialized with the tuning in hz (standard is 442 hz)
     * @return the deviation of the note in cents
     */
    static float getPitchDeviation(float frequency, float[] midiTable) {

        if(frequency < midiTable[0] || frequency > midiTable[MIDITABLESIZE - 1]) {
            return 0f;
        }

        float deviation = Float.MAX_VALUE;
        float closestNoteFrequency = 0;
        float nearestDifferentNoteFrequency = 0;


        for(int i = 0; i < MIDITABLESIZE; i++) {
            if(abs(deviation) > abs(frequency - midiTable[i])) {
                deviation = frequency - midiTable[i];
                closestNoteFrequency = midiTable[i];
                if(deviation < 0 && i > 0) {
                    nearestDifferentNoteFrequency = midiTable[i-1];
                }
                else if (deviation > 0 && i < MIDITABLESIZE - 1) {
                    nearestDifferentNoteFrequency = midiTable[i+1];
                }
                else if(deviation == 0) {
                    return 0;
                }
                else {
                    return Float.MAX_VALUE;
                }
            }
        }
        double frequencyDifference = abs(closestNoteFrequency - nearestDifferentNoteFrequency);

        //centDeviation:
        return (float) (100 * (deviation / frequencyDifference));
    }

    static float[] midiTable(int tuningPitch) {
        float[] result = new float[MIDITABLESIZE];
        for(double i = 0; i < MIDITABLESIZE; i++) {
            result[(int) i] = (float) (tuningPitch * pow(2, ((-57 + i) / 12)));
        }
        return result;
    }


    static String midiNoteToString(int midiNote) {
        if(midiNote < 0) {
            return "";
        }
        String[] noteString = new String[] { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B" };

        int octave = midiNote / 12;
        int noteIndex = midiNote % 12;
        String note = noteString[noteIndex];
        return note + octave;
    }

    /*static double[] calculateMidiNumbers(double[] frequencies) {
        if(frequencies.length < 1) {
            return new double[0];
        }
        double[] result = new double[frequencies.length];

        for(int i = 0; i < frequencies.length; i++) {
            result[i] = getMidiNote(frequencies[i], DEFAULT_TUNING);
        }

        return result;
    }*/

}
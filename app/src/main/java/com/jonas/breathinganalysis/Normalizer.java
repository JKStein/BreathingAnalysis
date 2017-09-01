package com.jonas.breathinganalysis;

import java.util.ArrayList;

import static java.lang.Math.abs;

class Normalizer {

    private static final int MIDITABLESIZE = 128;

    /**
     * Calculates the average of lost information by deleting a redundant entry
     * @param list the sensor data to process
     * @return the average of lost information by deleting a redundant entry
     */
    @SuppressWarnings("unused")
    static double averageErrorOfSensorDataRedundancies(ArrayList<SensorDate> list) {
        int amountOfRedundancies = 0;
        double overallRoundingError = 0;
        double averageEstimationError = 0;

        for(int i = 0; i < list.size(); i++) {
            if(i > 0 && list.get(i-1).getTimestamp() >= list.get(i).getTimestamp()) {
                amountOfRedundancies++;
                for (int j = 0; j < list.get(i).getValues().length; j++) {
                    overallRoundingError += abs(list.get(i).getValues()[j] - list.get(i-1).getValues()[j]);
                }
            }
        }
        if(amountOfRedundancies > 0) {
            averageEstimationError = overallRoundingError/(amountOfRedundancies * 3);
        }
        return averageEstimationError;
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

}
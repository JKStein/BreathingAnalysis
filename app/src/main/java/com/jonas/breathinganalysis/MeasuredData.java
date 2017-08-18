package com.jonas.breathinganalysis;

import java.util.ArrayList;
import java.util.List;

class MeasuredData {
    private ArrayList<SensorDate> acceleration, rotation, magnet;
    private ArrayList<Sound> audio;
    private ArrayList<Long> percussion;

    private double[] accelerationTimestamps;
    private double[] accelerationXValues;
    private double[] accelerationYValues;
    private double[] accelerationZValues;

    private double[] rotationTimestamps;
    private double[] rotationXValues;
    private double[] rotationYValues;
    private double[] rotationZValues;

    private double[] magnetTimestamps;
    private double[] magnetXValues;
    private double[] magnetYValues;
    private double[] magnetZValues;

    private double[] audioTimestamps;
    private double[] audioPitches;
    private double[] audioProbabilities;
    private double[] audioSpls;
    private double[] audioMidiNoteNumber;
    private double[] audioNoteDeviation;

    private double[] percussionSignal;

    private List<String[]> measuredDataSequence;

    private double bestFittingStartTimestamp;

    MeasuredData(ArrayList<SensorDate> acceleration, ArrayList<SensorDate> rotation,
                    ArrayList<SensorDate> magnet, ArrayList<Sound> audio, ArrayList<Long> percussion, double bestFittingStartTimestamp) {
        this.acceleration = new ArrayList<>(acceleration);
        this.rotation = new ArrayList<>(rotation);
        this.magnet = new ArrayList<>(magnet);
        this.audio = new ArrayList<>(audio);
        this.percussion = new ArrayList<>(percussion);
        this.bestFittingStartTimestamp = bestFittingStartTimestamp;
    }

    void setMeasuredDataSequence(List<String[]> measuredDataSequence) {
        this.measuredDataSequence = measuredDataSequence;
    }

    List<String[]> getMeasuredDataSequence() {
        return measuredDataSequence;
    }

    ArrayList<SensorDate> getAcceleration() {
        return acceleration;
    }

    ArrayList<SensorDate> getRotation() {
        return rotation;
    }

    ArrayList<SensorDate> getMagnet() {
        return magnet;
    }

    ArrayList<Sound> getAudio() {
        return audio;
    }

    ArrayList<Long> getPercussion() {
        return percussion;
    }

    void setAccelerationArrays(double[] timestamps, double[] xValues, double[] yValues, double[] zValues) {
        this.accelerationTimestamps = timestamps;
        this.accelerationXValues = xValues;
        this.accelerationYValues = yValues;
        this.accelerationZValues = zValues;
    }

    void setRotationArrays(double[] timestamps, double[] xValues, double[] yValues, double[] zValues) {
        this.rotationTimestamps = timestamps;
        this.rotationXValues = xValues;
        this.rotationYValues = yValues;
        this.rotationZValues = zValues;
    }

    void setMagnetArrays(double[] timestamps, double[] xValues, double[] yValues, double[] zValues) {
        this.magnetTimestamps = timestamps;
        this.magnetXValues = xValues;
        this.magnetYValues = yValues;
        this.magnetZValues = zValues;
    }

    void setAudioArrays(double[] timestamps, double[] pitches, double[] probabilities, double[] spls, double[] midiNoteNumbers, double[] deviations) {
        this.audioTimestamps = timestamps;
        this.audioPitches = pitches;
        this.audioProbabilities = probabilities;
        this.audioSpls = spls;
        this.audioMidiNoteNumber = midiNoteNumbers;
        this.audioNoteDeviation = deviations;
    }

    void setPercussionArray(double[] entries) {
        this.percussionSignal = entries;
    }

    ArrayList<SensorDate> getArrayList(DataType dataType) {
        switch (dataType) {
            case ACCELERATION:
                return acceleration;
            case ROTATION:
                return rotation;
            case MAGNET:
                return magnet;
            default:
                return new ArrayList<>();
        }
    }

    double[] getAccelerationTimestamps() {
        return accelerationTimestamps;
    }

    double[] getAccelerationXValues() {
        return accelerationXValues;
    }

    double[] getAccelerationYValues() {
        return accelerationYValues;
    }

    double[] getAccelerationZValues() {
        return accelerationZValues;
    }

    double[] getRotationTimestamps() {
        return rotationTimestamps;
    }

    double[] getRotationXValues() {
        return rotationXValues;
    }

    double[] getRotationYValues() {
        return rotationYValues;
    }

    double[] getRotationZValues() {
        return rotationZValues;
    }

    double[] getMagnetTimestamps() {
        return magnetTimestamps;
    }

    double[] getMagnetXValues() {
        return magnetXValues;
    }

    double[] getMagnetYValues() {
        return magnetYValues;
    }

    double[] getMagnetZValues() {
        return magnetZValues;
    }

    double[] getAudioTimestamps() {
        return audioTimestamps;
    }

    double[] getAudioPitches() {
        return audioPitches;
    }

    double[] getAudioProbabilities() {
        return audioProbabilities;
    }

    double[] getAudioSpls() {
        return audioSpls;
    }

    double[] getAudioMidiNoteNumber() {
        return audioMidiNoteNumber;
    }

    double[] getAudioNoteDeviation() {
        return audioNoteDeviation;
    }

    double[] getPercussionSignal() {
        return percussionSignal;
    }

    boolean isIncomplete() {
        return accelerationTimestamps.length <= 0 || rotationTimestamps.length <= 0 || magnetTimestamps.length <= 0 || audioTimestamps.length <= 0;
    }

    void printAcceleration() {
        System.out.println("Acceleration:\n");
        for(int i = 0; i < accelerationTimestamps.length; i++) {
            System.out.println("Acceleration: \ni: " + i + " | Timestamp: " + accelerationTimestamps[i] + " | xAxis: " + accelerationXValues[i] + " | yAxis: " +
                    accelerationYValues[i] + " | zAxis: " + accelerationZValues[i]);
        }
    }

    void printRotation() {
        System.out.println("Rotation:\n");
        for(int i = 0; i < rotationTimestamps.length; i++) {
            System.out.println("Rotation: \ni: " + i + " | Timestamp: " + rotationTimestamps[i] + " | xAxis: " + rotationXValues[i] + " | yAxis: " +
                    rotationYValues[i] + " | zAxis: " + rotationZValues[i]);
        }
    }

    void printMagnet() {
        System.out.println("Magnet:\n");
        for(int i = 0; i < magnetTimestamps.length; i++) {
            System.out.println("Magnet: \ni: " + i + " | Timestamp: " + magnetTimestamps[i] + " | xAxis: " + magnetXValues[i] + " | yAxis: " +
                                    magnetYValues[i] + " | zAxis: " + magnetZValues[i]);
        }
    }

    void printAudio() {
        System.out.println("Audio:\n");
        for(int i = 0; i < audioTimestamps.length; i++) {
            System.out.println("Audio: \ni: " + i + " | Timestamp: " + audioTimestamps[i] + " | pitch: " + audioPitches[i] + " | probability: " +
                    audioProbabilities[i] + " | SPL: " + audioSpls[i]);
        }
    }

    double getBestFittingStartTimestamp() {
        return bestFittingStartTimestamp;
    }

    void setBestFittingStartTimestamp(double bestFittingStartTimestamp) {
        this.bestFittingStartTimestamp = bestFittingStartTimestamp;
    }
}
package com.jonas.breathinganalysis;

class Sound {
    private long timestamp;
    private float pitch, probability;
    private double spl;
    private int midiNote;
    private double deviation;

    Sound(long timestamp, float pitch, float probability, double spl, int midiNote, double deviation) {
        this.timestamp = timestamp;
        this.pitch = pitch;
        this.probability = probability;
        this.spl = spl;
        this.midiNote = midiNote;
        this.deviation = deviation;
    }

    long getTimestamp() {
        return timestamp;
    }

    float getPitch() {
        return pitch;
    }

    float getProbability() {
        return probability;
    }

    double getSpl() {
        return spl;
    }

    int getMidiNote() {
        return midiNote;
    }

    double getDeviation() {
        return deviation;
    }

    public String toString() {
        return "timestamp: " + timestamp + "  ||  pitch: " + pitch + " | probability: " + probability + " | spl: " + spl;
    }
}

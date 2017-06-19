package com.jonas.breathinganalysis;

class Sound {
    private long timestamp;
    private float pitch, probability;
    private double spl;

    Sound(long timestamp, float pitch, float probability, double spl) {
        this.timestamp = timestamp;
        this.pitch = pitch;
        this.probability = probability;
        this.spl = spl;
    }

    long getTimestamp() {
        return timestamp;
    }

    float getpitch() {
        return pitch;
    }

    float getprobability() {
        return probability;
    }

    double getspl() {
        return spl;
    }

    public String toString() {
        return "timestamp: " + timestamp + "  ||  pitch: " + pitch + " | probability: " + probability + " | spl: " + spl;
    }
}

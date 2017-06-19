package com.jonas.breathinganalysis;

public class SensorData {
    private long timeStamp;
    private Acceleration acceleration;
    private Rotation rotation;
    private Magnet magnet;
    private Sound sound;

    SensorData(long timeStamp, Acceleration acceleration, Rotation rotation, Magnet magnet, Sound sound) {
        this.timeStamp = timeStamp;
        this.acceleration = acceleration;
        this.rotation = rotation;
        this.magnet = magnet;
        this.sound = sound;
    }

    long getTimeStamp() {
        return timeStamp;
    }

    Acceleration getAcceleration() {
        return acceleration;
    }

    Rotation getRotation() {
        return rotation;
    }

     Magnet getMagnet() {
        return magnet;
    }

    Sound getSound() {
        return sound;
    }
}

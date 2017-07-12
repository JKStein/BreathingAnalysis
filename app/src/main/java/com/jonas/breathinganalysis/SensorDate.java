package com.jonas.breathinganalysis;

/**
 * The result of a measurement taken by a sensor at a point in time
 */
class SensorDate {
    private long timestamp;
    private float xValue, yValue, zValue;

    SensorDate(long timestamp, float x, float y, float z) {
        this.timestamp = timestamp;
        this.xValue = x;
        this.yValue = y;
        this.zValue = z;
    }

    long getTimestamp() {
        return timestamp;
    }

    float getXValue() {
        return xValue;
    }

    float getYValue() {
        return yValue;
    }

    float getZValue() {
        return zValue;
    }

    public String toString() {
        return "timestamp: " + timestamp + "  ||  x: " + xValue + " | y: " + yValue + " | z: " + zValue;
    }
}
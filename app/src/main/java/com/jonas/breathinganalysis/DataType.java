package com.jonas.breathinganalysis;

enum DataType {
    ACCELERATION, ROTATION, MAGNET;

    public String toString() {
        if(this == ACCELERATION) {
            return "ACCELERATION";
        }
        else if(this == ROTATION) {
            return "ROTATION";
        }
        else if(this == MAGNET) {
            return "MAGNET";
        }
        else {
            return "UNASSIGNED";
        }
    }
}

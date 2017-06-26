package com.jonas.breathinganalysis;

enum DataType {
    ACCELERATION, ROTATION, MAGNET;

    void print() {
        if(this == ACCELERATION) {
            System.out.println("ACCELERATION");
        }
        else if(this == ROTATION) {
            System.out.println("ROTATION");
        }
        else if(this == MAGNET) {
            System.out.println("MAGNET");
        }
        else {
            System.out.println("DAFUQ");
        }
    }
}

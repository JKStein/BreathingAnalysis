package com.jonas.breathinganalysis;

class DataPreprocessor {

    DataPreprocessor(MeasuredData measuredData) {
        processData(measuredData);
    }

    private void processData(MeasuredData measuredData) {
        removeRedundancies(measuredData);
        instantiateSensorDataArrays(measuredData);
        interpolateMeasurementResults(measuredData);
        normalizeLength(measuredData);

        /*measuredData.printAcceleration();
        measuredData.printRotation();
        measuredData.printMagnet();
        measuredData.printAudio();*/

        instantiateMeasuredDataSequence(measuredData);
    }

    private void removeRedundancies(MeasuredData measuredData) {
        Normalizer.removeSensorDataRedundancies(measuredData.getAcceleration());
        Normalizer.removeSensorDataRedundancies(measuredData.getRotation());
        Normalizer.removeSensorDataRedundancies(measuredData.getMagnet());
        Normalizer.removeAudioDataRedundancies(measuredData.getAudio());
    }

    private void instantiateSensorDataArrays(MeasuredData measuredData) {
        Normalizer.instantiateSensorDataArrays(measuredData, DataType.ACCELERATION);
        Normalizer.instantiateSensorDataArrays(measuredData, DataType.ROTATION);
        Normalizer.instantiateSensorDataArrays(measuredData, DataType.MAGNET);
        Normalizer.instantiateAudioDataArrays(measuredData);
        Normalizer.instantiatePercussionDataArray(measuredData);
    }

    private void interpolateMeasurementResults(MeasuredData measuredData) {
        double[] accelerationTimestamps = Normalizer.normalizeTimestampArray(measuredData.getAccelerationTimestamps());
        double[] accelerationXValues = Normalizer.interpolate(measuredData.getAccelerationTimestamps(), measuredData.getAccelerationXValues());
        double[] accelerationYValues = Normalizer.interpolate(measuredData.getAccelerationTimestamps(), measuredData.getAccelerationYValues());
        double[] accelerationZValues = Normalizer.interpolate(measuredData.getAccelerationTimestamps(), measuredData.getAccelerationZValues());
        measuredData.setAccelerationArrays(accelerationTimestamps, accelerationXValues, accelerationYValues, accelerationZValues);

        double[] rotationTimestamps = Normalizer.normalizeTimestampArray(measuredData.getRotationTimestamps());
        double[] rotationXValues = Normalizer.interpolate(measuredData.getRotationTimestamps(), measuredData.getRotationXValues());
        double[] rotationYValues = Normalizer.interpolate(measuredData.getRotationTimestamps(), measuredData.getRotationYValues());
        double[] rotationZValues = Normalizer.interpolate(measuredData.getRotationTimestamps(), measuredData.getRotationZValues());
        measuredData.setRotationArrays(rotationTimestamps, rotationXValues, rotationYValues, rotationZValues);

        double[] magnetTimestamps = Normalizer.normalizeTimestampArray(measuredData.getMagnetTimestamps());
        double[] magnetXValues = Normalizer.interpolate(measuredData.getMagnetTimestamps(), measuredData.getMagnetXValues());
        double[] magnetYValues = Normalizer.interpolate(measuredData.getMagnetTimestamps(), measuredData.getMagnetYValues());
        double[] magnetZValues = Normalizer.interpolate(measuredData.getMagnetTimestamps(), measuredData.getMagnetZValues());
        measuredData.setMagnetArrays(magnetTimestamps, magnetXValues, magnetYValues, magnetZValues);

        double[] audioTimestamps = Normalizer.normalizeTimestampArray(measuredData.getAudioTimestamps());
        double[] audioPitches = Normalizer.interpolate(measuredData.getAudioTimestamps(), measuredData.getAudioPitches());
        double[] audioProbabilities = Normalizer.interpolate(measuredData.getAudioTimestamps(), measuredData.getAudioProbabilities());
        double[] audioSpls = Normalizer.interpolate(measuredData.getAudioTimestamps(), measuredData.getAudioSpls());
        double[] midiNoteNumbers = Normalizer.calculateMidiNumbers(audioPitches);
        double[] deviations = Normalizer.calculateNoteDeviations(audioPitches);

        measuredData.setAudioArrays(audioTimestamps, audioPitches, audioProbabilities, audioSpls, midiNoteNumbers, deviations);
    }

    private void normalizeLength(MeasuredData measuredData) {
        double startTimestamp = getBiggestStartTimestamp(measuredData);
        double endTimestamp = getSmallestEndTimestamp(measuredData);

        int accelerationStartIndex = java.util.Arrays.binarySearch(measuredData.getAccelerationTimestamps(), startTimestamp);
        int accelerationEndIndex = java.util.Arrays.binarySearch(measuredData.getAccelerationTimestamps(), endTimestamp);

        int rotationStartIndex = java.util.Arrays.binarySearch(measuredData.getRotationTimestamps(), startTimestamp);
        int rotationEndIndex = java.util.Arrays.binarySearch(measuredData.getRotationTimestamps(), endTimestamp);

        int magnetStartIndex = java.util.Arrays.binarySearch(measuredData.getMagnetTimestamps(), startTimestamp);
        int magnetEndIndex = java.util.Arrays.binarySearch(measuredData.getMagnetTimestamps(), endTimestamp);

        int audioStartIndex = java.util.Arrays.binarySearch(measuredData.getAudioTimestamps(), startTimestamp);
        int audioEndIndex = java.util.Arrays.binarySearch(measuredData.getAudioTimestamps(), endTimestamp);

        double[] accelerationTimestamps = Normalizer.shrinkArray(accelerationStartIndex, accelerationEndIndex, measuredData.getAccelerationTimestamps());
        double[] accelerationXValues = Normalizer.shrinkArray(accelerationStartIndex, accelerationEndIndex, measuredData.getAccelerationXValues());
        double[] accelerationYValues = Normalizer.shrinkArray(accelerationStartIndex, accelerationEndIndex, measuredData.getAccelerationYValues());
        double[] accelerationZValues = Normalizer.shrinkArray(accelerationStartIndex, accelerationEndIndex, measuredData.getAccelerationZValues());
        measuredData.setAccelerationArrays(accelerationTimestamps, accelerationXValues, accelerationYValues, accelerationZValues);

        double[] rotationTimestamps = Normalizer.shrinkArray(rotationStartIndex, rotationEndIndex, measuredData.getRotationTimestamps());
        double[] rotationXValues = Normalizer.shrinkArray(rotationStartIndex, rotationEndIndex, measuredData.getRotationXValues());
        double[] rotationYValues = Normalizer.shrinkArray(rotationStartIndex, rotationEndIndex, measuredData.getRotationYValues());
        double[] rotationZValues = Normalizer.shrinkArray(rotationStartIndex, rotationEndIndex, measuredData.getRotationZValues());
        measuredData.setRotationArrays(rotationTimestamps, rotationXValues, rotationYValues, rotationZValues);

        double[] magnetTimestamps = Normalizer.shrinkArray(magnetStartIndex, magnetEndIndex, measuredData.getMagnetTimestamps());
        double[] magnetXValues = Normalizer.shrinkArray(magnetStartIndex, magnetEndIndex, measuredData.getMagnetXValues());
        double[] magnetYValues = Normalizer.shrinkArray(magnetStartIndex, magnetEndIndex, measuredData.getMagnetYValues());
        double[] magnetZValues = Normalizer.shrinkArray(magnetStartIndex, magnetEndIndex, measuredData.getMagnetZValues());
        measuredData.setMagnetArrays(magnetTimestamps, magnetXValues, magnetYValues, magnetZValues);

        double[] audioTimestamps = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioTimestamps());
        double[] audioPitches = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioPitches());
        double[] audioProbabilities = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioProbabilities());
        double[] audioSpls = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioSpls());
        double[] midiNoteNumbers = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioMidiNoteNumber());
        double[] deviations = Normalizer.shrinkArray(audioStartIndex, audioEndIndex, measuredData.getAudioNoteDeviation());
        measuredData.setAudioArrays(audioTimestamps, audioPitches, audioProbabilities, audioSpls, midiNoteNumbers, deviations);

        double[] percussionArray = Normalizer.filledPercussionArray(startTimestamp, endTimestamp, measuredData.getPercussionSignal());
        measuredData.setPercussionArray(percussionArray);
    }

    private double getBiggestStartTimestamp(MeasuredData measuredData) {
        if(measuredData.isIncomplete()) {
            return 0D;
        }
        double accelerationStartTimestamp = measuredData.getAccelerationTimestamps()[0];
        double rotationStartTimestamp = measuredData.getRotationTimestamps()[0];
        double magnetStartTimestamp = measuredData.getMagnetTimestamps()[0];
        double audioStartTimestamp = measuredData.getAudioTimestamps()[0];

        double biggest = accelerationStartTimestamp;

        if(rotationStartTimestamp > biggest) {
            biggest = rotationStartTimestamp;
        }
        if(magnetStartTimestamp > biggest) {
            biggest = rotationStartTimestamp;
        }
        if(audioStartTimestamp > biggest) {
            biggest = audioStartTimestamp;
        }
        if(measuredData.getBestFittingStartTimestamp() > biggest) {
            biggest = measuredData.getBestFittingStartTimestamp();
        }

        System.out.println("Biggest Start Timestamp: " + biggest);
        return biggest;
    }

    private double getSmallestEndTimestamp(MeasuredData measuredData) {
        if(measuredData.isIncomplete()) {
            return 0D;
        }
        double accelerationEndTimestamp = measuredData.getAccelerationTimestamps()[measuredData.getAccelerationTimestamps().length - 1];
        double rotationEndTimestamp = measuredData.getRotationTimestamps()[measuredData.getRotationTimestamps().length - 1];
        double magnetEndTimestamp = measuredData.getMagnetTimestamps()[measuredData.getMagnetTimestamps().length - 1];
        double audioEndTimestamp = measuredData.getAudioTimestamps()[measuredData.getAudioTimestamps().length - 1];

        double smallest = accelerationEndTimestamp;

        if(rotationEndTimestamp < smallest) {
            smallest = rotationEndTimestamp;
        }
        if(magnetEndTimestamp < smallest) {
            smallest = magnetEndTimestamp;
        }
        if(audioEndTimestamp < smallest) {
            smallest = audioEndTimestamp;
        }
        if(getBiggestStartTimestamp(measuredData) + 32000 < smallest) {
            smallest = getBiggestStartTimestamp(measuredData) + 32000;
        }
        else {
            System.out.println("Test cut off to soon!");
        }
        System.out.println("Smallest End Timestamp: " + smallest);

        return smallest;
    }

    private void instantiateMeasuredDataSequence(MeasuredData measuredData) {
        Normalizer.instantiateMeasuredDataSequence(measuredData);
    }
}

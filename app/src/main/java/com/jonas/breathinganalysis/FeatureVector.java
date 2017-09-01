package com.jonas.breathinganalysis;

/**
 * @author Jonas Stein
 */

class FeatureVector {

    private String featureName;
    private String featureValue;

    FeatureVector(String featureName, String featureValue) {
        this.featureName = featureName;
        this.featureValue = featureValue;
    }

    String getFeatureName() {
        return featureName;
    }

    String getFeatureValue() {
        return featureValue;
    }
}

package com.jonas.breathinganalysis;

/**
 * A representation of a feature in an Attribute-Relation File.
 * @author Jonas Stein
 */
class Feature {

    private String featureName;
    private String featureValue;
    private String featureType;

    Feature(String featureName, String featureType, String featureValue) {
        this.featureName = featureName;
        this.featureType = featureType;
        this.featureValue = featureValue;
    }

    Feature(String featureName, String featureValue) {
        this.featureName = featureName;
        this.featureType = "numeric";
        this.featureValue = featureValue;
    }


    String getFeatureName() {
        return featureName;
    }

    String getFeatureValue() {
        return featureValue;
    }

    String getFeatureType() {
        return featureType;
    }
}

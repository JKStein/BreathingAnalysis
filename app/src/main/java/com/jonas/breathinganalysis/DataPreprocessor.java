package com.jonas.breathinganalysis;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class DataPreprocessor {

    DataPreprocessor() {
        testInterpolation();
    }

    private void testInterpolation() {
        double[] x = {0, 1, 2, 3};
        double[] y = {0.1, 0.6, 0.123, 1.4};

        printValues(interpolate(x,y));
    }

    private double[] interpolate(double[] x, double[] y) {
        SplineInterpolator splineInterpolator = new SplineInterpolator();
        PolynomialSplineFunction f = splineInterpolator.interpolate(x,y);
        int xMax = (int) x[x.length];
        double[] result = new double[xMax];
        for(int i = 0; i < xMax; i++) {
            result[i] = f.value((double) i);
        }
        return result;
    }

    private void printValues(double[] values) {
        for(int i = 0; i < values.length; i++) {
            System.out.println("xi: " + i + "\t yi: " + values[i]);
        }
    }
}

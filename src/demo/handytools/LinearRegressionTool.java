package demo.handytools;

/**
 * A linear regression analysis tool for biological data.
 * 
 * This Java class provides functionality to perform linear regression analysis on TSV data files with headers.
 * It automatically computes correlation coefficients and provides methods for fitting models and making predictions.
 * 
 * Key features:
 * * Regression equation (slope, intercept)
 * * R² value
 * * Significance P value
 * * Confidence intervals
 */
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class LinearRegressionTool {

    private SimpleRegression regression;
    private double correlation;
    private double pValue;
    private double confidenceLevel = 0.95; // 默认 95% 置信区间

    /**
     * Constructor for LinearRegressionTool
     */
    public LinearRegressionTool() {
        regression = new SimpleRegression(true);
    }

    /**
     * Fit a linear regression model to the provided data
     * @param x The independent variable values
     * @param y The dependent variable values
     * @throws IllegalArgumentException if x and y arrays have different lengths
     */
    public void fit(double[] x, double[] y) {
        if (x.length != y.length)
            throw new IllegalArgumentException("x and y must be of equal length");

        regression.clear();
        for (int i = 0; i < x.length; i++) {
            regression.addData(x[i], y[i]);
        }

        PearsonsCorrelation pc = new PearsonsCorrelation();
        correlation = pc.correlation(x, y);

        int n = x.length;
        double tStat = correlation * Math.sqrt((n - 2) / (1 - correlation * correlation));
        TDistribution tDist = new TDistribution(n - 2);
        pValue = 2 * (1 - tDist.cumulativeProbability(Math.abs(tStat)));
    }

    /**
     * Predict dependent variable values based on the fitted model
     * @param x The independent variable values for prediction
     * @return The predicted dependent variable values
     */
    public double[] predict(double[] x) {
        double[] yPred = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            yPred[i] = regression.predict(x[i]);
        }
        return yPred;
    }

    // --- Getter methods ---

    /**
     * Get the slope of the fitted regression line
     * @return The slope value
     */
    public double getSlope() {
        return regression.getSlope();
    }

    /**
     * Get the intercept of the fitted regression line
     * @return The intercept value
     */
    public double getIntercept() {
        return regression.getIntercept();
    }

    /**
     * Get the R-squared value of the fitted model
     * @return The R-squared value
     */
    public double getR2() {
        return regression.getRSquare();
    }

    /**
     * Get the correlation coefficient of the data
     * @return The correlation coefficient
     */
    public double getCorrelation() {
        return correlation;
    }

    /**
     * Get the p-value for the significance of the regression
     * @return The p-value
     */
    public double getPValue() {
        return pValue;
    }

    /**
     * Get the standard error of the slope estimate
     * @return The standard error of the slope
     */
    public double getSlopeStdErr() {
        return regression.getSlopeStdErr();
    }

    /**
     * Get the standard error of the intercept estimate
     * @return The standard error of the intercept
     */
    public double getInterceptStdErr() {
        return regression.getInterceptStdErr();
    }

    /**
     * Get the confidence interval for the slope estimate
     * @return An array containing the lower and upper bounds of the confidence interval
     */
    public double[] getSlopeConfidenceInterval() {
        return getConfidenceInterval(regression.getSlope(), regression.getSlopeStdErr());
    }

    /**
     * Get the confidence interval for the intercept estimate
     * @return An array containing the lower and upper bounds of the confidence interval
     */
    public double[] getInterceptConfidenceInterval() {
        return getConfidenceInterval(regression.getIntercept(), regression.getInterceptStdErr());
    }

    /**
     * Calculate a confidence interval for an estimate
     * @param estimate The point estimate
     * @param stdErr The standard error of the estimate
     * @return An array containing the lower and upper bounds of the confidence interval
     */
    private double[] getConfidenceInterval(double estimate, double stdErr) {
        int df = (int) regression.getN() - 2;
        TDistribution tDist = new TDistribution(df);
        double tCrit = tDist.inverseCumulativeProbability(1 - (1 - confidenceLevel) / 2);
        double margin = tCrit * stdErr;
        return new double[]{estimate - margin, estimate + margin};
    }

    /**
     * Set the confidence level for confidence interval calculations
     * @param level The confidence level (must be between 0 and 1)
     * @throws IllegalArgumentException if level is not between 0 and 1
     */
    public void setConfidenceLevel(double level) {
        if (level <= 0 || level >= 1) throw new IllegalArgumentException("Must be between 0 and 1.");
        this.confidenceLevel = level;
    }

    /**
     * Read data from a TSV file and extract specified columns
     * @param filepath The path to the TSV file
     * @param xCol The name of the column to use as the independent variable
     * @param yCol The name of the column to use as the dependent variable
     * @return A map containing the x and y data arrays
     * @throws IOException if there is an error reading the file
     * @throws IllegalArgumentException if the specified columns are not found
     */
    public static Map<String, double[]> readTSV(String filepath, String xCol, String yCol) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filepath));
        String[] headers = lines.get(0).split("\t");

        int xIndex = -1, yIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(xCol)) xIndex = i;
            if (headers[i].equals(yCol)) yIndex = i;
        }

        if (xIndex == -1 || yIndex == -1)
            throw new IllegalArgumentException("Missing column: " + xCol + " or " + yCol);

        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split("\t");
            xList.add(Double.parseDouble(tokens[xIndex]));
            yList.add(Double.parseDouble(tokens[yIndex]));
        }

        Map<String, double[]> result = new HashMap<>();
        result.put("x", xList.stream().mapToDouble(Double::doubleValue).toArray());
        result.put("y", yList.stream().mapToDouble(Double::doubleValue).toArray());
        return result;
    }
}
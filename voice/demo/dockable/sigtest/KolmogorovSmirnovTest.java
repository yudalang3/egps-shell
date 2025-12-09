package demo.dockable.sigtest;

import java.util.*;

/**
 * Kolmogorov-Smirnov Test Implementation
 * Non-parametric test used to compare whether two samples have the same distribution
 */
public class KolmogorovSmirnovTest implements StatisticalTest {
    
    @Override
    public double calculatePValue(double[] group1, double[] group2) {
        if (group1.length == 0 || group2.length == 0) {
            return 1.0;
        }
        
        int n1 = group1.length;
        int n2 = group2.length;
        
        // Sort data
        double[] sorted1 = group1.clone();
        double[] sorted2 = group2.clone();
        Arrays.sort(sorted1);
        Arrays.sort(sorted2);
        
        // Calculate KS statistic
        double ksStatistic = calculateKSStatistic(sorted1, sorted2);
        
        // Calculate p-value
        double effectiveN = Math.sqrt((n1 * n2) / (double)(n1 + n2));
        return kolmogorovPValue(ksStatistic * effectiveN);
    }
    
    /**
     * Calculate KS statistic
     * @param sorted1 First sorted group of data
     * @param sorted2 Second sorted group of data
     * @return KS statistic value
     */
    private double calculateKSStatistic(double[] sorted1, double[] sorted2) {
        int n1 = sorted1.length;
        int n2 = sorted2.length;
        
        // Merge all unique values
        Set<Double> allValues = new TreeSet<>();
        for (double v : sorted1) allValues.add(v);
        for (double v : sorted2) allValues.add(v);
        
        double maxDiff = 0;
        
        for (double value : allValues) {
            double cdf1 = calculateECDF(sorted1, value);
            double cdf2 = calculateECDF(sorted2, value);
            maxDiff = Math.max(maxDiff, Math.abs(cdf1 - cdf2));
        }
        
        return maxDiff;
    }
    
    /**
     * Calculate empirical cumulative distribution function
     * @param sortedData Sorted data array
     * @param value Value to calculate ECDF for
     * @return ECDF value
     */
    private double calculateECDF(double[] sortedData, double value) {
        int count = 0;
        for (double v : sortedData) {
            if (v <= value) count++;
            else break;
        }
        return (double) count / sortedData.length;
    }
    
    /**
     * Calculate p-value for Kolmogorov distribution
     * @param x Value to calculate p-value for
     * @return P-value
     */
    private double kolmogorovPValue(double x) {
        if (x <= 0) return 1.0;
        if (x >= 10) return 0.0;
        
        // Use series expansion
        double sum = 0;
        for (int k = 1; k <= 100; k++) {
            double term = Math.exp(-2 * k * k * x * x);
            if (k % 2 == 1) {
                sum += term;
            } else {
                sum -= term;
            }
            
            // Convergence check
            if (term < 1e-10) break;
        }
        
        return 2 * sum;
    }
    
    @Override
    public String getMethodName() {
        return "Kolmogorov_Smirnov";
    }
}
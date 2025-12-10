package demo.dockable.sigtest;

import java.util.*;

/**
 * Wilcoxon Rank Sum Test Implementation
 * Non-parametric test used to compare distribution differences between two independent samples
 */
public class WilcoxonRankSumTest implements StatisticalTest {
    
    @Override
    public double calculatePValue(double[] group1, double[] group2) {
        if (group1.length == 0 || group2.length == 0) {
            return 1.0;
        }
        
        int n1 = group1.length;
        int n2 = group2.length;
        int n = n1 + n2;
        
        // Combine data
        double[] combined = new double[n];
        System.arraycopy(group1, 0, combined, 0, n1);
        System.arraycopy(group2, 0, combined, n1, n2);
        
        // Calculate ranks
        double[] ranks = calculateRanks(combined);
        
        // Calculate rank sum for group1
        double W = 0;
        for (int i = 0; i < n1; i++) {
            W += ranks[i];
        }
        
        // Calculate expected value and variance
        double expectedW = n1 * (n + 1) / 2.0;
        double varianceW = n1 * n2 * (n + 1) / 12.0;
        
        // Standardize
        double z = (W - expectedW) / Math.sqrt(varianceW);
        
        // Two-tailed test
        return 2 * (1 - standardNormalCDF(Math.abs(z)));
    }
    
    /**
     * Calculate ranks
     * @param data Data array to calculate ranks for
     * @return Array of ranks
     */
    private double[] calculateRanks(double[] data) {
        int n = data.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }
        
        // Sort by value
        Arrays.sort(indices, (i, j) -> Double.compare(data[i], data[j]));
        
        double[] ranks = new double[n];
        int i = 0;
        while (i < n) {
            int j = i;
            // Find range of identical values
            while (j < n && data[indices[j]] == data[indices[i]]) {
                j++;
            }
            // Calculate average rank
            double avgRank = (i + j + 1) / 2.0;
            for (int k = i; k < j; k++) {
                ranks[indices[k]] = avgRank;
            }
            i = j;
        }
        
        return ranks;
    }
    
    /**
     * Standard normal distribution cumulative distribution function
     * @param x Value
     * @return Cumulative distribution function value
     */
    private double standardNormalCDF(double x) {
        return 0.5 * (1 + erf(x / Math.sqrt(2)));
    }
    
    /**
     * Error function approximation
     * @param x Value
     * @return Error function value
     */
    private double erf(double x) {
        // Use approximation formula
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);
        
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        
        return sign * y;
    }
    
    @Override
    public String getMethodName() {
        return "Wilcoxon_Rank_Sum";
    }
}
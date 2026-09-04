package demo.dockable.sigtest;

/**
 * Implementation of the t-test.
 * A parametric test used to compare the mean differences between two independent samples.
 * This test assumes that the data follows a normal distribution.
 */
public class TTest implements StatisticalTest {
    
    /**
     * Calculate the p-value for two groups of data using the t-test
     * @param group1 First group of data
     * @param group2 Second group of data
     * @return p-value indicating statistical significance
     */
    @Override
    public double calculatePValue(double[] group1, double[] group2) {
        if (group1.length == 0 || group2.length == 0) {
            return 1.0;
        }
        
        double mean1 = calculateMean(group1);
        double mean2 = calculateMean(group2);
        double var1 = calculateVariance(group1, mean1);
        double var2 = calculateVariance(group2, mean2);
        
        int n1 = group1.length;
        int n2 = group2.length;
        
        // Welch's t-test (does not assume equal variances)
        double pooledSE = Math.sqrt(var1/n1 + var2/n2);
        double t = (mean1 - mean2) / pooledSE;
        
        // Calculate degrees of freedom (Welch-Satterthwaite equation)
        double df = Math.pow(var1/n1 + var2/n2, 2) / 
                   (Math.pow(var1/n1, 2)/(n1-1) + Math.pow(var2/n2, 2)/(n2-1));
        
        // Two-tailed test
        return 2 * (1 - studentTCDF(Math.abs(t), df));
    }
    
    /**
     * Calculate the mean of the data
     * @param data The data array
     * @return The mean value
     */
    private double calculateMean(double[] data) {
        double sum = 0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.length;
    }
    
    /**
     * Calculate the variance of the data
     * @param data The data array
     * @param mean The mean of the data
     * @return The variance value
     */
    private double calculateVariance(double[] data, double mean) {
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return sum / (data.length - 1);
    }
    
    /**
     * Student's t-distribution cumulative distribution function
     * @param t The t-statistic
     * @param df The degrees of freedom
     * @return The cumulative probability
     */
    private double studentTCDF(double t, double df) {
        if (df <= 0) return 0.5;
        
        // Relationship using incomplete Beta function
        double x = df / (df + t * t);
        return 0.5 + 0.5 * Math.signum(t) * (1 - incompleteBeta(0.5 * df, 0.5, x));
    }
    
    /**
     * Incomplete Beta function
     * @param a First parameter
     * @param b Second parameter
     * @param x The value to evaluate
     * @return The incomplete Beta function value
     */
    private double incompleteBeta(double a, double b, double x) {
        if (x <= 0) return 0;
        if (x >= 1) return 1;
        
        // Using continued fraction expansion approximation
        double bt = Math.exp(logGamma(a + b) - logGamma(a) - logGamma(b) + 
                           a * Math.log(x) + b * Math.log(1 - x));
        
        if (x < (a + 1) / (a + b + 2)) {
            return bt * betaContinuedFraction(a, b, x) / a;
        } else {
            return 1 - bt * betaContinuedFraction(b, a, 1 - x) / b;
        }
    }
    
    /**
     * Continued fraction expansion for the Beta function
     * @param a First parameter
     * @param b Second parameter
     * @param x The value to evaluate
     * @return The continued fraction value
     */
    private double betaContinuedFraction(double a, double b, double x) {
        final int maxIterations = 100;
        final double epsilon = 1e-15;
        
        double qab = a + b;
        double qap = a + 1;
        double qam = a - 1;
        double c = 1;
        double d = 1 - qab * x / qap;
        
        if (Math.abs(d) < 1e-30) d = 1e-30;
        d = 1 / d;
        double h = d;
        
        for (int m = 1; m <= maxIterations; m++) {
            int m2 = 2 * m;
            double aa = m * (b - m) * x / ((qam + m2) * (a + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1 / d;
            h *= d * c;
            
            aa = -(a + m) * (qab + m) * x / ((a + m2) * (qap + m2));
            d = 1 + aa * d;
            if (Math.abs(d) < 1e-30) d = 1e-30;
            c = 1 + aa / c;
            if (Math.abs(c) < 1e-30) c = 1e-30;
            d = 1 / d;
            double del = d * c;
            h *= del;
            
            if (Math.abs(del - 1) < epsilon) break;
        }
        
        return h;
    }
    
    /**
     * Log Gamma function
     * @param x The value to evaluate
     * @return The natural logarithm of the Gamma function
     */
    private double logGamma(double x) {
        if (x <= 0) return Double.POSITIVE_INFINITY;
        
        // Stirling approximation
        if (x > 12) {
            return (x - 0.5) * Math.log(x) - x + 0.5 * Math.log(2 * Math.PI) + 
                   1.0 / (12 * x) - 1.0 / (360 * x * x * x);
        }
        
        // Using recurrence relation
        double result = 0;
        while (x < 12) {
            result -= Math.log(x);
            x += 1;
        }
        
        return result + logGamma(x);
    }
    
    /**
     * Get the name of this statistical method
     * @return Method name string
     */
    @Override
    public String getMethodName() {
        return "t_test";
    }
}
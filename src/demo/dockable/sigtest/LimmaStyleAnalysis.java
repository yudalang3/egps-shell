package demo.dockable.sigtest;

import java.util.*;

/**
 * Limma-style Linear Model Differential Expression Analysis
 * Linear model method implementing empirical Bayes variance shrinkage
 */
public class LimmaStyleAnalysis implements AdvancedStatisticalTest {
    
    @Override
    public List<DifferentialExpressionResult> performAnalysis(
            double[][] expressionMatrix,
            String[] geneNames,
            String[] sampleNames,
            int[] group1Indices,
            int[] group2Indices,
            Map<String, Object> parameters) {
        
        // Get parameters
        boolean logTransform = (Boolean) parameters.getOrDefault("log_transform", true);
        boolean normalize = (Boolean) parameters.getOrDefault("normalize", true);
        double priorDf = (Double) parameters.getOrDefault("prior_df", 4.0);
        
        // Data preprocessing
        double[][] processedMatrix = preprocessData(expressionMatrix, logTransform, normalize);
        
        // Fit linear model
        List<DifferentialExpressionResult> results = new ArrayList<>();
        
        // Calculate global variance parameters
        double[] geneVariances = calculateGeneVariances(processedMatrix, group1Indices, group2Indices);
        double[] shrunkVariances = empiricalBayesShrinkage(geneVariances, priorDf);
        
        for (int i = 0; i < geneNames.length; i++) {
            // Calculate group means
            double mean1 = calculateGroupMean(processedMatrix[i], group1Indices);
            double mean2 = calculateGroupMean(processedMatrix[i], group2Indices);
            
            // Calculate logFC
            double logFC = mean2 - mean1;
            
            // Calculate t-statistic
            double pooledSE = Math.sqrt(shrunkVariances[i] * (1.0/group1Indices.length + 1.0/group2Indices.length));
            double tStat = logFC / pooledSE;
            
            // Calculate degrees of freedom
            double df = group1Indices.length + group2Indices.length - 2 + priorDf;
            
            // Calculate p-value
            double pValue = 2 * (1 - studentTCDF(Math.abs(tStat), df));
            
            // Calculate average expression
            double avgExpression = (mean1 + mean2) / 2;
            
            results.add(new DifferentialExpressionResult(geneNames[i], logFC, pValue, pValue, avgExpression));
        }
        
        // Multiple testing correction
        return adjustPValues(results);
    }
    
    /**
     * Data preprocessing
     * @param matrix Expression matrix
     * @param logTransform Whether to perform log transformation
     * @param normalize Whether to perform normalization
     * @return Preprocessed expression matrix
     */
    private double[][] preprocessData(double[][] matrix, boolean logTransform, boolean normalize) {
        double[][] processed = new double[matrix.length][];
        
        for (int i = 0; i < matrix.length; i++) {
            processed[i] = matrix[i].clone();
            
            // Log transformation
            if (logTransform) {
                for (int j = 0; j < processed[i].length; j++) {
                    double value = processed[i][j] + 1;
                    processed[i][j] = Math.log(value) / Math.log(2);

                }
            }
        }
        
        // Quantile normalization
        if (normalize) {
            processed = quantileNormalization(processed);
        }
        
        return processed;
    }
    
    /**
     * Quantile normalization
     * @param matrix Expression matrix
     * @return Normalized expression matrix
     */
    private double[][] quantileNormalization(double[][] matrix) {
        int nGenes = matrix.length;
        int nSamples = matrix[0].length;
        
        // Create sorted matrix
        double[][] sorted = new double[nGenes][nSamples];
        int[][] ranks = new int[nGenes][nSamples];
        
        for (int j = 0; j < nSamples; j++) {
            double[] column = new double[nGenes];
            Integer[] indices = new Integer[nGenes];
            
            for (int i = 0; i < nGenes; i++) {
                column[i] = matrix[i][j];
                indices[i] = i;
            }
            
            Arrays.sort(indices, (a, b) -> Double.compare(column[a], column[b]));
            
            for (int i = 0; i < nGenes; i++) {
                ranks[indices[i]][j] = i;
                sorted[i][j] = column[indices[i]];
            }
        }
        
        // Calculate mean for each rank
        double[] rankMeans = new double[nGenes];
        for (int i = 0; i < nGenes; i++) {
            double sum = 0;
            for (int j = 0; j < nSamples; j++) {
                sum += sorted[i][j];
            }
            rankMeans[i] = sum / nSamples;
        }
        
        // Reassign values
        double[][] normalized = new double[nGenes][nSamples];
        for (int i = 0; i < nGenes; i++) {
            for (int j = 0; j < nSamples; j++) {
                normalized[i][j] = rankMeans[ranks[i][j]];
            }
        }
        
        return normalized;
    }
    
    /**
     * Calculate gene variances
     * @param matrix Expression matrix
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @return Array of gene variances
     */
    private double[] calculateGeneVariances(double[][] matrix, int[] group1Indices, int[] group2Indices) {
        double[] variances = new double[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            double[] group1Data = extractGroupData(matrix[i], group1Indices);
            double[] group2Data = extractGroupData(matrix[i], group2Indices);
            
            double var1 = calculateVariance(group1Data);
            double var2 = calculateVariance(group2Data);
            
            // Pooled variance
            int n1 = group1Data.length;
            int n2 = group2Data.length;
            variances[i] = ((n1 - 1) * var1 + (n2 - 1) * var2) / (n1 + n2 - 2);
        }
        
        return variances;
    }
    
    /**
     * Empirical Bayes variance shrinkage
     * @param variances Array of gene variances
     * @param priorDf Prior degrees of freedom
     * @return Array of shrunk variances
     */
    private double[] empiricalBayesShrinkage(double[] variances, double priorDf) {
        // Calculate prior variance
        double priorVar = calculateMedian(variances);
        
        double[] shrunkVariances = new double[variances.length];
        for (int i = 0; i < variances.length; i++) {
            // Empirical Bayes shrinkage
            shrunkVariances[i] = (priorDf * priorVar + variances[i]) / (priorDf + 1);
        }
        
        return shrunkVariances;
    }
    
    /**
     * Calculate median
     * @param values Array of values
     * @return Median value
     */
    private double calculateMedian(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        int n = sorted.length;
        if (n % 2 == 0) {
            return (sorted[n/2 - 1] + sorted[n/2]) / 2.0;
        } else {
            return sorted[n/2];
        }
    }
    
    /**
     * Extract group data
     * @param data Data array
     * @param indices Indices to extract
     * @return Extracted data subset
     */
    private double[] extractGroupData(double[] data, int[] indices) {
        double[] groupData = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
            groupData[i] = data[indices[i]];
        }
        return groupData;
    }
    
    /**
     * Calculate group mean
     * @param data Data array
     * @param indices Indices of samples in the group
     * @return Mean of the group
     */
    private double calculateGroupMean(double[] data, int[] indices) {
        double sum = 0;
        for (int index : indices) {
            sum += data[index];
        }
        return sum / indices.length;
    }
    
    /**
     * Calculate variance
     * @param data Data array
     * @return Variance of the data
     */
    private double calculateVariance(double[] data) {
        double mean = Arrays.stream(data).average().orElse(0.0);
        double sum = 0;
        for (double value : data) {
            sum += Math.pow(value - mean, 2);
        }
        return sum / (data.length - 1);
    }
    
    /**
     * Student's t distribution CDF
     * @param t T-statistic
     * @param df Degrees of freedom
     * @return Cumulative distribution function value
     */
    private double studentTCDF(double t, double df) {
        if (df <= 0) return 0.5;
        
        double x = df / (df + t * t);
        return 0.5 + 0.5 * Math.signum(t) * (1 - incompleteBeta(0.5 * df, 0.5, x));
    }
    
    /**
     * Incomplete Beta function (simplified implementation)
     * @param a First parameter
     * @param b Second parameter
     * @param x Value
     * @return Incomplete Beta function value
     */
    private double incompleteBeta(double a, double b, double x) {
        if (x <= 0) return 0;
        if (x >= 1) return 1;
        
        // Simplified approximate implementation
        return Math.pow(x, a) * Math.pow(1 - x, b) / (a * beta(a, b));
    }
    
    /**
     * Beta function
     * @param a First parameter
     * @param b Second parameter
     * @return Beta function value
     */
    private double beta(double a, double b) {
        return Math.exp(logGamma(a) + logGamma(b) - logGamma(a + b));
    }
    
    /**
     * Log Gamma function
     * @param x Value
     * @return Log Gamma function value
     */
    private double logGamma(double x) {
        if (x <= 0) return Double.POSITIVE_INFINITY;
        
        // Stirling approximation
        return (x - 0.5) * Math.log(x) - x + 0.5 * Math.log(2 * Math.PI);
    }
    
    /**
     * Benjamini-Hochberg FDR multiple testing correction
     * @param originalResults List of differential expression results
     * @return List of results with adjusted p-values
     */
    private List<DifferentialExpressionResult> adjustPValues(List<DifferentialExpressionResult> originalResults) {
        int n = originalResults.size();
        if (n == 0) return new ArrayList<>();

        // Create a copy for sorting
        List<DifferentialExpressionResult> sortedResults = new ArrayList<>(originalResults);
        sortedResults.sort(Comparator.comparingDouble(DifferentialExpressionResult::getPValue));

        Map<String, Double> adjustedPValuesMap = new HashMap<>();
        double lastAdjustedP = sortedResults.get(n - 1).getPValue();
        adjustedPValuesMap.put(sortedResults.get(n - 1).getGeneName(), lastAdjustedP);

        for (int i = n - 2; i >= 0; i--) {
            double unadjustedP = sortedResults.get(i).getPValue();
            double factor = (double) n / (i + 1);
            double adjustedP = Math.min(lastAdjustedP, unadjustedP * factor);
            adjustedPValuesMap.put(sortedResults.get(i).getGeneName(), adjustedP);
            lastAdjustedP = adjustedP;
        }

        // Create new results list
        List<DifferentialExpressionResult> finalResults = new ArrayList<>();
        for (DifferentialExpressionResult result : originalResults) {
            double adjustedP = adjustedPValuesMap.get(result.getGeneName());
            finalResults.add(new DifferentialExpressionResult(result.getGeneName(), result.getLogFC(), 
                result.getPValue(), adjustedP, result.getAverageExpression()));
        }
        
        return finalResults;
    }
    
    @Override
    public String getMethodName() {
        return "Limma";
    }
    
    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("log_transform", true);
        params.put("normalize", true);
        params.put("prior_df", 4.0);
        return params;
    }
}
package demo.dockable.sigtest;

import java.util.*;

/**
 * DESeq2-style Negative Binomial Distribution Differential Expression Analysis
 * Statistical method specifically designed for RNA-seq count data
 */
public class DESeq2StyleAnalysis implements AdvancedStatisticalTest {
    
    @Override
    public List<DifferentialExpressionResult> performAnalysis(
            double[][] expressionMatrix,
            String[] geneNames,
            String[] sampleNames,
            int[] group1Indices,
            int[] group2Indices,
            Map<String, Object> parameters) {
        
        // Get parameters
        double alpha = (Double) parameters.getOrDefault("alpha", 0.1);
        String fitType = (String) parameters.getOrDefault("fit_type", "parametric");
        
        // Estimate size factors
        double[] sizeFactors = estimateSizeFactors(expressionMatrix);
        
        // Normalize data
        double[][] normalizedMatrix = normalizeBySize(expressionMatrix, sizeFactors);
        
        // Estimate dispersions
        double[] dispersions = estimateDispersions(normalizedMatrix, group1Indices, group2Indices, alpha);
        
        // Perform differential expression analysis
        List<DifferentialExpressionResult> results = new ArrayList<>();
        
        for (int i = 0; i < geneNames.length; i++) {
            // Calculate group means
            double mean1 = calculateGroupMean(normalizedMatrix[i], group1Indices);
            double mean2 = calculateGroupMean(normalizedMatrix[i], group2Indices);
            
            // Calculate logFC
            double logFC = Math.log((mean2 + 1) / (mean1 + 1)) / Math.log(2);
            
            // Wald test
            double pValue = waldTest(normalizedMatrix[i], group1Indices, group2Indices, dispersions[i]);
            
            // Calculate average expression
            double avgExpression = (mean1 + mean2) / 2;
            String geneName = geneNames[i];
            results.add(new DifferentialExpressionResult(geneName, logFC, pValue, pValue, avgExpression));
        }
        
        // Multiple testing correction
        adjustPValues(results);
        
        return results;
    }
    
    /**
     * Estimate size factors
     * @param matrix Expression matrix
     * @return Array of size factors for each sample
     */
    private double[] estimateSizeFactors(double[][] matrix) {
        int nSamples = matrix[0].length;
        int nGenes = matrix.length;
        
        // Calculate geometric means
        double[] geoMeans = new double[nGenes];
        for (int i = 0; i < nGenes; i++) {
            double product = 1.0;
            boolean hasZero = false;
            for (int j = 0; j < nSamples; j++) {
                if (matrix[i][j] == 0) {
                    hasZero = true;
                    break;
                }
                product *= matrix[i][j];
            }
            geoMeans[i] = hasZero ? 0 : Math.pow(product, 1.0 / nSamples);
        }
        
        // Calculate size factors
        double[] sizeFactors = new double[nSamples];
        for (int j = 0; j < nSamples; j++) {
            List<Double> ratios = new ArrayList<>();
            for (int i = 0; i < nGenes; i++) {
                if (geoMeans[i] > 0 && matrix[i][j] > 0) {
                    ratios.add(matrix[i][j] / geoMeans[i]);
                }
            }
            
            if (!ratios.isEmpty()) {
                Collections.sort(ratios);
                sizeFactors[j] = ratios.get(ratios.size() / 2); // Median
            } else {
                sizeFactors[j] = 1.0;
            }
        }
        
        return sizeFactors;
    }
    
    /**
     * Normalize by size factors
     * @param matrix Expression matrix
     * @param sizeFactors Size factors for each sample
     * @return Normalized expression matrix
     */
    private double[][] normalizeBySize(double[][] matrix, double[] sizeFactors) {
        int nGenes = matrix.length;
        int nSamples = matrix[0].length;
        
        double[][] normalized = new double[nGenes][nSamples];
        for (int i = 0; i < nGenes; i++) {
            for (int j = 0; j < nSamples; j++) {
                normalized[i][j] = matrix[i][j] / sizeFactors[j];
            }
        }
        
        return normalized;
    }
    
    /**
     * Estimate dispersion parameters
     * @param matrix Normalized expression matrix
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @param alpha Significance level
     * @return Array of dispersion values for each gene
     */
    private double[] estimateDispersions(double[][] matrix, int[] group1Indices, int[] group2Indices, double alpha) {
        int nGenes = matrix.length;
        double[] dispersions = new double[nGenes];
        
        for (int i = 0; i < nGenes; i++) {
            double[] group1Data = extractGroupData(matrix[i], group1Indices);
            double[] group2Data = extractGroupData(matrix[i], group2Indices);
            
            double mean1 = Arrays.stream(group1Data).average().orElse(0.0);
            double mean2 = Arrays.stream(group2Data).average().orElse(0.0);
            double var1 = calculateVariance(group1Data);
            double var2 = calculateVariance(group2Data);
            
            // Estimate dispersion
            double meanExpr = (mean1 + mean2) / 2;
            double pooledVar = (var1 + var2) / 2;
            
            if (meanExpr > 0) {
                dispersions[i] = Math.max(0, (pooledVar - meanExpr) / (meanExpr * meanExpr));
            } else {
                dispersions[i] = alpha;
            }
            
            // Shrink to prior value
            dispersions[i] = alpha * 0.5 + dispersions[i] * 0.5;
        }
        
        return dispersions;
    }
    
    /**
     * Wald test
     * @param geneData Expression data for a gene
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @param dispersion Dispersion parameter
     * @return P-value from Wald test
     */
    private double waldTest(double[] geneData, int[] group1Indices, int[] group2Indices, double dispersion) {
        double[] group1Data = extractGroupData(geneData, group1Indices);
        double[] group2Data = extractGroupData(geneData, group2Indices);
        
        double mean1 = Arrays.stream(group1Data).average().orElse(0.0);
        double mean2 = Arrays.stream(group2Data).average().orElse(0.0);
        
        // Calculate standard error
        double se1 = Math.sqrt(mean1 * (1 + dispersion * mean1) / group1Data.length);
        double se2 = Math.sqrt(mean2 * (1 + dispersion * mean2) / group2Data.length);
        double pooledSE = Math.sqrt(se1 * se1 + se2 * se2);
        
        if (pooledSE == 0) return 1.0;
        
        // Wald statistic
        double waldStat = Math.log((mean2 + 1) / (mean1 + 1)) / pooledSE;
        
        // Use standard normal distribution
        return 2 * (1 - standardNormalCDF(Math.abs(waldStat)));
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
     * Standard normal distribution CDF
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
    
    /**
     * Multiple testing correction (Benjamini-Hochberg FDR)
     * @param results List of differential expression results
     */
    private void adjustPValues(List<DifferentialExpressionResult> results) {
        int n = results.size();
        
        // Sort by p-value
        results.sort((a, b) -> Double.compare(a.getPValue(), b.getPValue()));
        
        // Calculate adjusted p-values
        for (int i = 0; i < n; i++) {
            DifferentialExpressionResult result = results.get(i);
            double adjustedP = result.getPValue() * n / (i + 1);
            adjustedP = Math.min(adjustedP, 1.0);
            
            // Ensure monotonicity
            if (i > 0) {
                DifferentialExpressionResult prevResult = results.get(i - 1);
                adjustedP = Math.max(adjustedP, prevResult.getAdjustedPValue());
            }
            
            // Create new result object
            DifferentialExpressionResult newResult = new DifferentialExpressionResult(
                result.getGeneName(), result.getLogFC(), result.getPValue(),
                adjustedP, result.getAverageExpression());
            
            results.set(i, newResult);
        }
    }
    
    @Override
    public String getMethodName() {
        return "DESeq2";
    }
    
    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("alpha", 0.1);
        params.put("fit_type", "parametric");
        return params;
    }
}
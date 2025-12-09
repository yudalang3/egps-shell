package demo.dockable.sigtest;

import java.util.*;

/**
 * edgeR-style Exact Test Differential Expression Analysis
 * Exact test method based on negative binomial distribution
 */
public class EdgeRStyleAnalysis implements AdvancedStatisticalTest {
    
    @Override
    public List<DifferentialExpressionResult> performAnalysis(
            double[][] expressionMatrix,
            String[] geneNames,
            String[] sampleNames,
            int[] group1Indices,
            int[] group2Indices,
            Map<String, Object> parameters) {
        
        // Get parameters
        double priorCount = (Double) parameters.getOrDefault("prior_count", 0.125);
        String testMethod = (String) parameters.getOrDefault("test_method", "exact");
        
        // TMM normalization
        double[] normFactors = calculateTMMNormFactors(expressionMatrix);
        double[][] normalizedMatrix = normalizeByTMM(expressionMatrix, normFactors);
        
        // Estimate dispersions
        double commonDispersion = estimateCommonDispersion(normalizedMatrix, group1Indices, group2Indices);
        double[] tagwiseDispersions = estimateTagwiseDispersions(normalizedMatrix, group1Indices, group2Indices, commonDispersion);
        
        // Perform differential expression analysis
        List<DifferentialExpressionResult> results = new ArrayList<>();
        
        for (int i = 0; i < geneNames.length; i++) {
            // Calculate group means
            double mean1 = calculateGroupMean(normalizedMatrix[i], group1Indices);
            double mean2 = calculateGroupMean(normalizedMatrix[i], group2Indices);
            
            // Calculate logFC
            double logFC = Math.log((mean2 + priorCount) / (mean1 + priorCount)) / Math.log(2);
            
            // Exact test or GLM test
            double pValue;
            if ("exact".equals(testMethod)) {
                pValue = exactTest(normalizedMatrix[i], group1Indices, group2Indices, tagwiseDispersions[i]);
            } else {
                pValue = glmTest(normalizedMatrix[i], group1Indices, group2Indices, tagwiseDispersions[i]);
            }
            
            // Calculate average expression
            double avgExpression = (mean1 + mean2) / 2;
            
            results.add(new DifferentialExpressionResult(geneNames[i], logFC, pValue, pValue, avgExpression));
        }
        
        // Multiple testing correction
        adjustPValues(results);
        
        return results;
    }
    
    /**
     * Calculate TMM normalization factors
     * @param matrix Expression matrix
     * @return Array of normalization factors for each sample
     */
    private double[] calculateTMMNormFactors(double[][] matrix) {
        int nSamples = matrix[0].length;
        double[] normFactors = new double[nSamples];
        
        // Select reference sample (the sample whose total count is closest to the geometric mean)
        double[] totalCounts = new double[nSamples];
        for (int j = 0; j < nSamples; j++) {
            for (int i = 0; i < matrix.length; i++) {
                totalCounts[j] += matrix[i][j];
            }
        }
        
        // Calculate geometric mean
        double geoMean = 1.0;
        for (double count : totalCounts) {
            geoMean *= Math.pow(count, 1.0 / nSamples);
        }
        
        // Find the sample closest to the geometric mean as reference
        int refSample = 0;
        double minDiff = Math.abs(totalCounts[0] - geoMean);
        for (int j = 1; j < nSamples; j++) {
            double diff = Math.abs(totalCounts[j] - geoMean);
            if (diff < minDiff) {
                minDiff = diff;
                refSample = j;
            }
        }
        
        // Calculate TMM factors
        for (int j = 0; j < nSamples; j++) {
            if (j == refSample) {
                normFactors[j] = 1.0;
            } else {
                normFactors[j] = calculateTMMFactor(matrix, refSample, j);
            }
        }
        
        return normFactors;
    }
    
    /**
     * Calculate a single TMM factor
     * @param matrix Expression matrix
     * @param refSample Reference sample index
     * @param targetSample Target sample index
     * @return TMM factor
     */
    private double calculateTMMFactor(double[][] matrix, int refSample, int targetSample) {
        List<Double> mValues = new ArrayList<>();
        List<Double> aValues = new ArrayList<>();
        
        for (int i = 0; i < matrix.length; i++) {
            double ref = matrix[i][refSample];
            double target = matrix[i][targetSample];
            
            if (ref > 0 && target > 0) {
                double m = Math.log(target / ref) / Math.log(2);
                double a = 0.5 * Math.log(target * ref) / Math.log(2);
                mValues.add(m);
                aValues.add(a);
            }
        }
        
        if (mValues.isEmpty()) return 1.0;
        
        // Remove extreme values (30% quantiles from both ends)
        Collections.sort(mValues);
        Collections.sort(aValues);
        
        int trimCount = (int) (mValues.size() * 0.3);
        List<Double> trimmedM = mValues.subList(trimCount, mValues.size() - trimCount);
        
        // Calculate weighted average
        double weightedSum = 0;
        double totalWeight = 0;
        
        for (double m : trimmedM) {
            double weight = 1.0; // Simplified weight
            weightedSum += m * weight;
            totalWeight += weight;
        }
        
        double tmmFactor = totalWeight > 0 ? Math.pow(2, weightedSum / totalWeight) : 1.0;
        return tmmFactor;
    }
    
    /**
     * TMM normalization
     * @param matrix Expression matrix
     * @param normFactors Normalization factors for each sample
     * @return Normalized expression matrix
     */
    private double[][] normalizeByTMM(double[][] matrix, double[] normFactors) {
        int nGenes = matrix.length;
        int nSamples = matrix[0].length;
        
        double[][] normalized = new double[nGenes][nSamples];
        for (int i = 0; i < nGenes; i++) {
            for (int j = 0; j < nSamples; j++) {
                normalized[i][j] = matrix[i][j] / normFactors[j];
            }
        }
        
        return normalized;
    }
    
    /**
     * Estimate common dispersion
     * @param matrix Normalized expression matrix
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @return Common dispersion value
     */
    private double estimateCommonDispersion(double[][] matrix, int[] group1Indices, int[] group2Indices) {
        List<Double> dispersions = new ArrayList<>();
        
        for (int i = 0; i < matrix.length; i++) {
            double[] group1Data = extractGroupData(matrix[i], group1Indices);
            double[] group2Data = extractGroupData(matrix[i], group2Indices);
            
            double mean1 = Arrays.stream(group1Data).average().orElse(0.0);
            double mean2 = Arrays.stream(group2Data).average().orElse(0.0);
            double var1 = calculateVariance(group1Data);
            double var2 = calculateVariance(group2Data);
            
            double meanExpr = (mean1 + mean2) / 2;
            double pooledVar = (var1 + var2) / 2;
            
            if (meanExpr > 0) {
                double dispersion = Math.max(0, (pooledVar - meanExpr) / (meanExpr * meanExpr));
                dispersions.add(dispersion);
            }
        }
        
        // Return median as common dispersion
        Collections.sort(dispersions);
        int n = dispersions.size();
        if (n == 0) return 0.1;
        
        if (n % 2 == 0) {
            return (dispersions.get(n/2 - 1) + dispersions.get(n/2)) / 2.0;
        } else {
            return dispersions.get(n/2);
        }
    }
    
    /**
     * Estimate tagwise dispersions
     * @param matrix Normalized expression matrix
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @param commonDispersion Common dispersion value
     * @return Array of tagwise dispersion values
     */
    private double[] estimateTagwiseDispersions(double[][] matrix, int[] group1Indices, int[] group2Indices, double commonDispersion) {
        double[] dispersions = new double[matrix.length];
        
        for (int i = 0; i < matrix.length; i++) {
            double[] group1Data = extractGroupData(matrix[i], group1Indices);
            double[] group2Data = extractGroupData(matrix[i], group2Indices);
            
            double mean1 = Arrays.stream(group1Data).average().orElse(0.0);
            double mean2 = Arrays.stream(group2Data).average().orElse(0.0);
            double var1 = calculateVariance(group1Data);
            double var2 = calculateVariance(group2Data);
            
            double meanExpr = (mean1 + mean2) / 2;
            double pooledVar = (var1 + var2) / 2;
            
            if (meanExpr > 0) {
                double rawDispersion = Math.max(0, (pooledVar - meanExpr) / (meanExpr * meanExpr));
                // Shrink toward common dispersion
                dispersions[i] = 0.7 * commonDispersion + 0.3 * rawDispersion;
            } else {
                dispersions[i] = commonDispersion;
            }
        }
        
        return dispersions;
    }
    
    /**
     * Exact test
     * @param geneData Expression data for a gene
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @param dispersion Dispersion parameter
     * @return P-value from exact test
     */
    private double exactTest(double[] geneData, int[] group1Indices, int[] group2Indices, double dispersion) {
        double[] group1Data = extractGroupData(geneData, group1Indices);
        double[] group2Data = extractGroupData(geneData, group2Indices);
        
        double mean1 = Arrays.stream(group1Data).average().orElse(0.0);
        double mean2 = Arrays.stream(group2Data).average().orElse(0.0);
        
        // Simplified exact test (using normal approximation)
        double logFC = Math.log((mean2 + 1) / (mean1 + 1));
        double se = Math.sqrt(dispersion * (1.0/group1Data.length + 1.0/group2Data.length) * (mean1 + mean2 + 2));
        
        if (se == 0) return 1.0;
        
        double z = logFC / se;
        return 2 * (1 - standardNormalCDF(Math.abs(z)));
    }
    
    /**
     * GLM test (simplified version)
     * @param geneData Expression data for a gene
     * @param group1Indices Indices of samples in group 1
     * @param group2Indices Indices of samples in group 2
     * @param dispersion Dispersion parameter
     * @return P-value from GLM test
     */
    private double glmTest(double[] geneData, int[] group1Indices, int[] group2Indices, double dispersion) {
        // Simplified GLM test, actually calls exact test
        return exactTest(geneData, group1Indices, group2Indices, dispersion);
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
        return "edgeR";
    }
    
    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("prior_count", 0.125);
        params.put("test_method", "exact");
        return params;
    }
}
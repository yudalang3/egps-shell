package demo.dockable.sigtest;

import java.util.*;

/**
 * Advanced Statistical Test Interface
 * Used for bioinformatics statistical methods that require global information
 */
public interface AdvancedStatisticalTest {
    
    /**
     * Perform global differential expression analysis
     * @param expressionMatrix Expression matrix, each row is a gene, each column is a sample
     * @param geneNames List of gene names
     * @param sampleNames List of sample names
     * @param group1Indices Indices of samples in the first group
     * @param group2Indices Indices of samples in the second group
     * @param parameters Method-specific parameters
     * @return List of differential expression analysis results
     */
    List<DifferentialExpressionResult> performAnalysis(
        double[][] expressionMatrix,
        String[] geneNames,
        String[] sampleNames,
        int[] group1Indices,
        int[] group2Indices,
        Map<String, Object> parameters
    );
    
    /**
     * Get method name
     * @return Method name
     */
    String getMethodName();
    
    /**
     * Get default parameters
     * @return Default parameter map
     */
    Map<String, Object> getDefaultParameters();
}
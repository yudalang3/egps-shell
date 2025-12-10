package demo.dockable.sigtest;

import java.io.*;
import java.util.*;

/**
 * Advanced Differential Expression Analyzer
 * Integrates bioinformatics methods such as Limma, DESeq2, and edgeR
 */
public class AdvancedDifferentialExpressionAnalyzer {
    
    private Map<String, AdvancedStatisticalTest> methods;
    
    /**
     * Constructor for AdvancedDifferentialExpressionAnalyzer
     * Initializes the available statistical methods
     */
    public AdvancedDifferentialExpressionAnalyzer() {
        initializeMethods();
    }
    
    /**
     * Initialize advanced statistical methods
     */
    private void initializeMethods() {
        methods = new HashMap<>();
        methods.put("Limma", new LimmaStyleAnalysis());
        methods.put("DESeq2", new DESeq2StyleAnalysis());
        methods.put("edgeR", new EdgeRStyleAnalysis());
    }
    
    /**
     * Perform advanced differential expression analysis
     * @param fileName Input file name
     * @param group1Labels Labels for the first group of samples
     * @param group2Labels Labels for the second group of samples
     * @param outputFileName Output file name
     * @param methodName Statistical method name
     */
    public void performAdvancedAnalysis(String fileName, String[] group1Labels, 
                                      String[] group2Labels, String outputFileName, 
                                      String methodName) {
        performAdvancedAnalysis(fileName, group1Labels, group2Labels, outputFileName, methodName, null);
    }
    
    /**
     * Perform advanced differential expression analysis with parameters
     * @param fileName Input file name
     * @param group1Labels Labels for the first group of samples
     * @param group2Labels Labels for the second group of samples
     * @param outputFileName Output file name
     * @param methodName Statistical method name
     * @param parameters Method parameters
     */
    public void performAdvancedAnalysis(String fileName, String[] group1Labels, 
                                      String[] group2Labels, String outputFileName, 
                                      String methodName, Map<String, Object> parameters) {
        
        AdvancedStatisticalTest method = methods.get(methodName);
        if (method == null) {
            System.err.println("Unknown advanced statistical method: " + methodName);
            return;
        }
        
        try {
            // Read expression data
            ExpressionData data = readExpressionData(fileName);
            if (data == null) {
                System.err.println("Failed to read data: " + fileName);
                return;
            }
            
            // Find sample indices
            int[] group1Indices = findSampleIndices(data.sampleNames, group1Labels);
            int[] group2Indices = findSampleIndices(data.sampleNames, group2Labels);
            
            // Use default parameters if not provided
            if (parameters == null) {
                parameters = method.getDefaultParameters();
            }
            
            // Perform analysis
            List<DifferentialExpressionResult> results = method.performAnalysis(
                data.expressionMatrix, data.geneNames, data.sampleNames,
                group1Indices, group2Indices, parameters);
            
            // Write results
            writeAdvancedResults(results, outputFileName, methodName);
            System.out.println("Advanced analysis completed: " + outputFileName);
            
        } catch (IOException e) {
            System.err.println("File operation error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Analysis error: " + e.getMessage());
        }
    }
    
    /**
     * Read expression data from file
     * @param fileName Path to the expression data file
     * @return ExpressionData object containing the data
     * @throws IOException If there's an error reading the file
     */
    public ExpressionData readExpressionData(String fileName) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.split("\t"));
            }
        }
        
        if (data.isEmpty()) return null;
        
        String[] header = data.get(0);
        String[] sampleNames = Arrays.copyOfRange(header, 1, header.length);
        
        int nGenes = data.size() - 1;
        int nSamples = sampleNames.length;
        
        String[] geneNames = new String[nGenes];
        double[][] expressionMatrix = new double[nGenes][nSamples];
        
        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            geneNames[i-1] = row[0];
            
            for (int j = 1; j < row.length; j++) {
                try {
                    expressionMatrix[i-1][j-1] = Double.parseDouble(row[j]);
                } catch (NumberFormatException e) {
                    expressionMatrix[i-1][j-1] = 0.0;
                }
            }
        }
        
        return new ExpressionData(expressionMatrix, geneNames, sampleNames);
    }
    
    /**
     * Find sample indices in the data
     * @param sampleNames Array of sample names in the data
     * @param targetLabels Array of target sample labels to find
     * @return Array of indices corresponding to the target labels
     */
    private int[] findSampleIndices(String[] sampleNames, String[] targetLabels) {
        int[] indices = new int[targetLabels.length];
        for (int i = 0; i < targetLabels.length; i++) {
            indices[i] = -1;
            for (int j = 0; j < sampleNames.length; j++) {
                if (sampleNames[j].equals(targetLabels[i])) {
                    indices[i] = j;
                    break;
                }
            }
            if (indices[i] == -1) {
                throw new IllegalArgumentException("Sample not found: " + targetLabels[i]);
            }
        }
        return indices;
    }
    
    /**
     * Write advanced analysis results to file
     * @param results List of differential expression results
     * @param fileName Output file name
     * @param methodName Name of the method used
     * @throws IOException If there's an error writing to the file
     */
    public void writeAdvancedResults(List<DifferentialExpressionResult> results, 
                                   String fileName, String methodName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Write header
            writer.println("GeneName\tlogFC\tPValue\tAdjustedPValue\tAverageExpression\tSignificant\tMethod");
            
            // Write results
            for (DifferentialExpressionResult result : results) {
                writer.printf("%s\t%.4f\t%.6f\t%.6f\t%.4f\t%s\t%s%n",
                    result.getGeneName(),
                    result.getLogFC(),
                    result.getPValue(),
                    result.getAdjustedPValue(),
                    result.getAverageExpression(),
                    result.isSignificant() ? "YES" : "NO",
                    methodName);
            }
        }
    }
    
    /**
     * Compare analysis results from multiple methods
     * @param fileName Input file name
     * @param group1Labels Labels for the first group of samples
     * @param group2Labels Labels for the second group of samples
     * @param outputPrefix Prefix for output files
     */
    public void compareMethodsAnalysis(String fileName, String[] group1Labels, 
                                     String[] group2Labels, String outputPrefix) {
        for (String methodName : methods.keySet()) {
            String outputFile = fileName + "_" + outputPrefix + "_" + methodName + ".tsv";
            performAdvancedAnalysis(fileName, group1Labels, group2Labels, outputFile, methodName);
        }
    }
    
    /**
     * Get available methods
     * @return Set of available method names
     */
    public Set<String> getAvailableMethods() {
        return methods.keySet();
    }
    
    /**
     * Main method for example usage
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        AdvancedDifferentialExpressionAnalyzer analyzer = new AdvancedDifferentialExpressionAnalyzer();
        
        String fileName = "large_sample_data.tsv";
        String[] group1 = {"Ctrl1", "Ctrl2", "Ctrl3"};
        String[] group2 = {"Treat1", "Treat2", "Treat3"};
        
        System.out.println("=== Advanced Differential Expression Analysis ===");
        System.out.println("Input file: " + fileName);
        System.out.println("Control group: " + String.join(", ", group1));
        System.out.println("Treatment group: " + String.join(", ", group2));
        System.out.println();
        
        // Show available methods
        System.out.println("Available advanced methods:");
        for (String method : analyzer.getAvailableMethods()) {
            System.out.println("- " + method);
        }
        System.out.println();
        
        // Run comparative analysis
        analyzer.compareMethodsAnalysis(fileName, group1, group2, "advanced_analysis");
        
        // Run each method individually
        System.out.println("\n=== Running Each Method Individually ===");
        for (String method : analyzer.getAvailableMethods()) {
            String outputFile = "single_" + method + "_result.txt";
            analyzer.performAdvancedAnalysis(fileName, group1, group2, outputFile, method);
        }
        
        System.out.println("\nAll analyses completed!");
    }
}
package demo.dockable.sigtest;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.*;
import java.util.*;

/**
 * Differential Expression Analyzer
 * Used to perform differential expression analysis, supporting multiple statistical methods
 */
public class DifferentialExpressionAnalyzer {
    
    private Map<String, StatisticalTest> methods;
    public static final String ALL_METHOD = "all";
    
    /**
     * Constructor for DifferentialExpressionAnalyzer
     * Initializes available statistical methods
     */
    public DifferentialExpressionAnalyzer() {
        initializeMethods();
    }
    
    /**
     * Initialize statistical methods
     */
    private void initializeMethods() {
        methods = new HashMap<>();
        methods.put("Mann-Whitney U", new MannWhitneyUTest());
        methods.put("t-test", new TTest());
        methods.put("Wilcoxon Rank Sum", new WilcoxonRankSumTest());
        methods.put("Kolmogorov-Smirnov", new KolmogorovSmirnovTest());
    }
    
    /**
     * Perform differential expression analysis
     * @param fileName Input file name
     * @param group1Columns Column names for the first group
     * @param group2Columns Column names for the second group
     * @param outputFileName Output file name
     * @param methodName Statistical method name
     */
    public void performAnalysis(String fileName, String[] group1Columns, 
                              String[] group2Columns, String outputFileName, 
                              String methodName) {

        List<StatisticalTest> currUseMethods = new ArrayList<>();
        {
            StatisticalTest method = methods.get(methodName);
            if (method == null && Objects.equals(ALL_METHOD, methodName)) {
                throw new IllegalArgumentException("Unknown method name: " + methodName);
            }

            if (method != null) {
                currUseMethods.add(method);
            } else {
                currUseMethods.addAll(methods.values());
            }
        }
        
        try {
            // Read data
            List<String[]> data = readTSVFile(fileName);
            if (data.isEmpty()) {
                throw new IllegalArgumentException("Empty input file, Please check your data");
            }
            
            String[] header = data.get(0);
            
            // Find column indices
            int[] group1Indices = findColumnIndices(header, group1Columns);
            int[] group2Indices = findColumnIndices(header, group2Columns);
            Joiner joiner = Joiner.on('\t');
            // Perform analysis
            List<String> results = new ArrayList<>();
            {
                StringJoiner stringJoiner = new StringJoiner("\t");
                for (StatisticalTest med : currUseMethods) {
                    stringJoiner.add(med.getMethodName() + "_PValue");
                }
                results.add(joiner.join(header) + "\t" + stringJoiner);
            }

            int geneSize = data.size();
            for (int i = 1; i < geneSize; i++) {
                String[] row = data.get(i);

                double[] group1Data = extractGroupData(row, group1Indices);
                double[] group2Data = extractGroupData(row, group2Indices);
                StringJoiner stringJoiner = new StringJoiner("\t");
                for (StatisticalTest med : currUseMethods){
                    double pValue = med.calculatePValue(group1Data, group2Data);
                    stringJoiner.add(String.valueOf(pValue));
                }

                results.add(joiner.join(header) + "\t" + stringJoiner);
            }
            
            // Write results
            writeResults(results, outputFileName);

        } catch (Exception e) {
            throw new RuntimeException("Error in computation: " + e.getMessage());
        }
    }


    /**
     * Read TSV file
     * @param fileName Path to the TSV file
     * @return List of string arrays representing the file data
     * @throws IOException If there's an error reading the file
     */
    private List<String[]> readTSVFile(String fileName) throws IOException {
        List<String[]> data = Lists.newArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.split("\t"));
            }
        }
        return data;
    }
    
    /**
     * Find column indices
     * @param header Header array
     * @param columnNames Names of columns to find
     * @return Array of indices corresponding to the column names
     */
    private int[] findColumnIndices(String[] header, String[] columnNames) {
        int[] indices = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            indices[i] = -1;
            for (int j = 0; j < header.length; j++) {
                if (header[j].equals(columnNames[i])) {
                    indices[i] = j;
                    break;
                }
            }
            if (indices[i] == -1) {
                throw new IllegalArgumentException("Could not find the column: " + columnNames[i]);
            }
        }
        return indices;
    }
    
    /**
     * Extract group data
     * @param row Data row
     * @param indices Indices to extract
     * @return Array of extracted data
     */
    private double[] extractGroupData(String[] row, int[] indices) {
        double[] data = new double[indices.length];
        for (int i = 0; i < indices.length; i++) {
            try {
                data[i] = Double.parseDouble(row[indices[i]]);
            } catch (NumberFormatException e) {
                data[i] = 0.0; // Default value
            }
        }
        return data;
    }
    
    /**
     * Write results to file
     * @param results List of result strings
     * @param fileName Output file name
     * @throws IOException If there's an error writing to the file
     */
    private void writeResults(List<String> results, String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String result : results) {
                writer.println(result);
            }
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
     * Main method example
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        DifferentialExpressionAnalyzer analyzer = new DifferentialExpressionAnalyzer();
        
        String fileName = "sample_data.tsv";
        String[] group1 = {"Ctrl1", "Ctrl2", "Ctrl3"};
        String[] group2 = {"Treat1", "Treat2", "Treat3"};
        
        // Run all methods
        for (String method : analyzer.getAvailableMethods()) {
            String outputFile = "output_" + method.replace("-", "_").replace(" ", "_") + ".txt";
            analyzer.performAnalysis(fileName, group1, group2, outputFile, method);
        }
    }
}
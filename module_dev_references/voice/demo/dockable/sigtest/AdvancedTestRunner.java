package demo.dockable.sigtest;

import java.util.Map;

/**
 * Advanced Differential Expression Analysis Test Runner
 * This class demonstrates how to use the AdvancedDifferentialExpressionAnalyzer
 * to perform various types of advanced differential expression analyses.
 */
public class AdvancedTestRunner {

    /**
     * Main method to run advanced differential expression analysis tests
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Create analyzer instance
        AdvancedDifferentialExpressionAnalyzer analyzer = new AdvancedDifferentialExpressionAnalyzer();

        // Define input file and sample groups
        String inputFile = "sample_data.tsv";
        String[] controlGroup = {"control1", "control2", "control3"};
        String[] treatmentGroup = {"treatment1", "treatment2", "treatment3"};

        System.out.println("Available advanced analysis methods: " + analyzer.getAvailableMethods());
        System.out.println("======================================================");

        // Run all advanced analysis methods
        System.out.println("Running all advanced analysis methods...");
        analyzer.compareMethodsAnalysis(inputFile, controlGroup, treatmentGroup, "advanced_output");
        System.out.println("======================================================");

        // Test Limma method separately with specified parameters
        System.out.println("Testing Limma method (without log transformation)...");
        Map<String, Object> limmaParams = analyzer.getAvailableMethods().contains("Limma") 
            ? new LimmaStyleAnalysis().getDefaultParameters() 
            : null;
        if (limmaParams != null) {
            limmaParams.put("log_transform", false);
            analyzer.performAdvancedAnalysis(inputFile, controlGroup, treatmentGroup, 
                "advanced_output_limma_no_log.txt", "Limma", limmaParams);
        }
        System.out.println("======================================================");

        // Test large dataset
        System.out.println("Testing large dataset (large_sample_data.tsv) using DESeq2...");
        String largeInputFile = "large_sample_data.tsv";
        String[] largeControlGroup = {"control_1", "control_2", "control_3", "control_4", "control_5"};
        String[] largeTreatmentGroup = {"treatment_1", "treatment_2", "treatment_3", "treatment_4", "treatment_5"};
        analyzer.performAdvancedAnalysis(largeInputFile, largeControlGroup, largeTreatmentGroup, 
            "advanced_output_large_deseq2.txt", "DESeq2");

        System.out.println("\nAll advanced analysis tests completed.");
    }
}
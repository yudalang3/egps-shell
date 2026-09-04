package demo.dockable.algo;

import java.io.IOException;
import java.util.List;

/**
 * Expression profile simulator usage example
 * Demonstrates how to use ExpressionProfileSimulator to generate different types of expression profile data
 */
public class ExpressionProfileRunner {

    static  String dir = "C:\\Users\\yudal\\Documents\\temp\\my_test_egps\\";
    
    /**
     * Main method to run all examples
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Example 1: 2 types - Case vs Control
            example2Types();
            
            // Example 2: 2 types - Wild-type vs Mutant
            exampleWildTypeMutant();

            // Example 3: 3 types - Wild-type, Mutant, and Rescue
            example3Types();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Example 1: 2 types - Case vs Control
     * @throws IOException If there's an error writing to the output file
     */
    public static void example2Types() throws IOException {
        System.out.println("=== Example 1: Case vs Control ===");
        
        String[] typeLabels = {"Case", "Control"};
        List<String> sampleLabels = ExpressionProfileSimulator.generateSampleLabels(typeLabels, 3);
        
        ExpressionProfileSimulator simulator = new ExpressionProfileSimulator(
            20000, // 20000 genes
            sampleLabels,
                dir+ "expression_case_control.txt"
        );
        
        simulator.generateExpressionProfile();
    }
    
    /**
     * Example 2: 2 types - Wild-type vs Mutant
     * @throws IOException If there's an error writing to the output file
     */
    public static void exampleWildTypeMutant() throws IOException {
        System.out.println("\n=== Example 2: Wild-type vs Mutant ===");
        
        String[] typeLabels = {"WT", "Mutant"};
        List<String> sampleLabels = ExpressionProfileSimulator.generateSampleLabels(typeLabels, 3);
        
        // Custom parameters: higher differential expression ratio and fold change
        ExpressionProfileSimulator simulator = new ExpressionProfileSimulator(
            20000,
            sampleLabels,
            dir + "expression_wt_mutant.txt",
            0.02,    // 2% of genes are differentially expressed
            150.0,   // Higher base expression level
            0.08,    // Lower dispersion
            2.0,     // Minimum 2-fold change
            6.0      // Maximum 6-fold change
        );
        
        simulator.generateExpressionProfile();
    }
    
    /**
     * Example 3: 3 types - Wild-type, Mutant, and Rescue
     * @throws IOException If there's an error writing to the output file
     */
    public static void example3Types() throws IOException {
        System.out.println("\n=== Example 3: Wild-type, Mutant, and Rescue ===");
        
        String[] typeLabels = {"WT", "Mutant", "Rescue"};
        List<String> sampleLabels = ExpressionProfileSimulator.generateSampleLabels(typeLabels, 3);
        
        ExpressionProfileSimulator simulator = new ExpressionProfileSimulator(
            20000,
            sampleLabels,
            dir +"expression_wt_mutant_rescue.txt",
            0.015,   // 1.5% of genes are differentially expressed
            120.0,   // Base expression level
            0.12,    // Dispersion
            1.8,     // Minimum 1.8-fold change
            5.0      // Maximum 5-fold change
        );
        
        simulator.generateExpressionProfile();
    }
    
    /**
     * Advanced example: Custom sample labels
     * @throws IOException If there's an error writing to the output file
     */
    public static void exampleCustomLabels() throws IOException {
        System.out.println("\n=== Advanced Example: Custom Sample Labels ===");
        
        List<String> customLabels = List.of(
            "Condition_A_1", "Condition_A_2", "Condition_A_3",
            "Condition_B_1", "Condition_B_2", "Condition_B_3",
            "Condition_C_1", "Condition_C_2", "Condition_C_3"
        );
        
        ExpressionProfileSimulator simulator = new ExpressionProfileSimulator(
            10000,  // Fewer genes for quick testing
            customLabels,
            "expression_custom.txt"
        );
        
        simulator.generateExpressionProfile();
    }
}
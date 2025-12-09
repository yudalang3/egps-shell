package demo.dockable.algo;

import com.google.common.collect.Lists;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Expression profile data simulator
 * Supports 2 types (case-ctrl, wild-type-mutant) or 3 types (wild-type-mutant-complementation) samples
 * Uses negative binomial distribution to simulate RNA-seq expression data
 */
public class ExpressionProfileSimulator {

    private final int numberOfGenes;
    private final int numberOfSamples;
    private final List<String> sampleLabels;
    private final String outputFilePath;
    private final double differentialRatio; // Proportion of differentially expressed genes
    private final RandomGenerator random;

    // Negative binomial distribution parameters
    private final double baseMean; // Base expression mean
    private final double dispersion; // Dispersion parameter
    private final double foldChangeMin; // Minimum fold change
    private final double foldChangeMax; // Maximum fold change

    /**
     * Constructor for ExpressionProfileSimulator with default parameters
     * @param numberOfGenes Number of genes to simulate
     * @param sampleLabels Labels for the samples
     * @param outputFilePath Path to output file
     */
    public ExpressionProfileSimulator(int numberOfGenes, List<String> sampleLabels,
                                      String outputFilePath) {
        this(numberOfGenes, sampleLabels, outputFilePath, 0.01, 50.0, 0.02, 3.0, 8.0);
    }

    /**
     * Constructor for ExpressionProfileSimulator with custom parameters
     * @param numberOfGenes Number of genes to simulate
     * @param sampleLabels Labels for the samples
     * @param outputFilePath Path to output file
     * @param differentialRatio Proportion of differentially expressed genes
     * @param baseMean Base expression mean
     * @param dispersion Dispersion parameter for negative binomial distribution
     * @param foldChangeMin Minimum fold change
     * @param foldChangeMax Maximum fold change
     */
    public ExpressionProfileSimulator(int numberOfGenes, List<String> sampleLabels,
                                      String outputFilePath, double differentialRatio,
                                      double baseMean, double dispersion,
                                      double foldChangeMin, double foldChangeMax) {
        this.numberOfGenes = numberOfGenes;
        this.numberOfSamples = sampleLabels.size();
        this.sampleLabels = new ArrayList<>(sampleLabels);
        this.outputFilePath = outputFilePath;
        this.differentialRatio = differentialRatio;
        this.baseMean = baseMean;
        this.dispersion = dispersion;
        this.foldChangeMin = foldChangeMin;
        this.foldChangeMax = foldChangeMax;
        this.random = ThreadLocalRandom.current();
    }

    /**
     * Generate expression profile data
     *
     * @return List of status messages
     * @throws IOException If there's an error writing to the output file
     */
    public List<String> generateExpressionProfile() throws IOException {
        // Determine number of sample types
        int typeNumber = determineTypeNumber();

        // Generate gene expression matrix
        double[][] expressionMatrix = generateExpressionMatrix(typeNumber);

        // Output to file
        writeToFile(expressionMatrix);

        int numDifferential = getDifferentialGeneCount();

        List<String> ret = Lists.newLinkedList();

        ret.add("Expression profile data generation completed: " + outputFilePath);
        ret.add("Number of genes: " + numberOfGenes);
        ret.add("Number of samples: " + numberOfSamples);
        ret.add("Number of sample types: " + typeNumber);
        ret.add("Differentially expressed genes ratio: " + (differentialRatio * 100) + "%");

        ret.add("Gene arrangement information:");
        ret.add("- Differentially expressed genes: Gene_1 to Gene_" + numDifferential + " (first " + numDifferential + ")");
        ret.add("- Non-differentially expressed genes: Gene_" + (numDifferential + 1) + " to Gene_" + numberOfGenes + " (last " + (numberOfGenes - numDifferential) + ")");

        return ret;
    }

    /**
     * Determine the number of sample types
     * @return Number of distinct sample types
     */
    private int determineTypeNumber() {
        Set<String> typeSet = new HashSet<>();
        for (String label : sampleLabels) {
            // Extract type label (remove _1, _2, _3 suffixes)
            String type = label.replaceAll("_\\d+$", "");
            typeSet.add(type);
        }
        return typeSet.size();
    }

    /**
     * Generate expression matrix
     * @param typeNumber Number of sample types
     * @return Matrix of gene expression values
     */
    private double[][] generateExpressionMatrix(int typeNumber) {
        double[][] matrix = new double[numberOfGenes][numberOfSamples];

        // Calculate number of differentially expressed genes
        int numDifferential = (int) (numberOfGenes * differentialRatio);

        // First genes are differentially expressed
        for (int geneIdx = 0; geneIdx < numDifferential; geneIdx++) {
            generateGeneExpression(matrix, geneIdx, typeNumber, true);
        }

        // Remaining genes are non-differentially expressed
        for (int geneIdx = numDifferential; geneIdx < numberOfGenes; geneIdx++) {
            generateGeneExpression(matrix, geneIdx, typeNumber, false);
        }

        return matrix;
    }

    /**
     * Get count of differentially expressed genes
     * @return Number of differentially expressed genes
     */
    private int getDifferentialGeneCount() {
        return (int) (numberOfGenes * differentialRatio);
    }

    /**
     * Generate expression values for a single gene
     * @param matrix Expression matrix to populate
     * @param geneIdx Index of the gene
     * @param typeNumber Number of sample types
     * @param isDifferential Whether the gene is differentially expressed
     */
    private void generateGeneExpression(double[][] matrix, int geneIdx, int typeNumber, boolean isDifferential) {
        // Base expression level
        double baseExpression = generateBaseExpression();

        if (!isDifferential) {
            // Non-differential genes: similar expression levels across all samples
            for (int sampleIdx = 0; sampleIdx < numberOfSamples; sampleIdx++) {
                matrix[geneIdx][sampleIdx] = sampleNegativeBinomial(baseExpression, dispersion);
            }
        } else {
            // Differential genes: generate different expression levels by sample type
            generateDifferentialExpression(matrix, geneIdx, typeNumber, baseExpression);
        }
    }

    /**
     * Generate differential expression
     * @param matrix Expression matrix to populate
     * @param geneIdx Index of the gene
     * @param typeNumber Number of sample types
     * @param baseExpression Base expression level
     */
    private void generateDifferentialExpression(double[][] matrix, int geneIdx, int typeNumber, double baseExpression) {
        // Generate fold changes
        double[] foldChanges = generateFoldChanges(typeNumber);

        // Group samples by type
        Map<String, List<Integer>> typeGroups = groupSamplesByType();

        int typeIdx = 0;
        for (Map.Entry<String, List<Integer>> entry : typeGroups.entrySet()) {
            double adjustedMean = baseExpression * foldChanges[typeIdx];

            for (int sampleIdx : entry.getValue()) {
                matrix[geneIdx][sampleIdx] = sampleNegativeBinomial(adjustedMean, dispersion);
            }
            typeIdx++;
        }
    }

    /**
     * Generate fold change array
     * @param typeNumber Number of sample types
     * @return Array of fold changes for each type
     */
    private double[] generateFoldChanges(int typeNumber) {
        double[] foldChanges = new double[typeNumber];

        if (typeNumber == 2) {
            // 2 types: control group and experimental group
            foldChanges[0] = 1.0; // control group
            foldChanges[1] = foldChangeMin + random.nextDouble() * (foldChangeMax - foldChangeMin);
            // Randomly decide up-regulation or down-regulation
            if (random.nextBoolean()) {
                foldChanges[1] = 1.0 / foldChanges[1];
            }
        } else if (typeNumber == 3) {
            // 3 types: wild-type, mutant, complementation
            foldChanges[0] = 1.0; // wild-type (control)
            foldChanges[1] = foldChangeMin + random.nextDouble() * (foldChangeMax - foldChangeMin); // mutant
            // Randomly decide up-regulation or down-regulation
            if (random.nextBoolean()) {
                foldChanges[1] = 1.0 / foldChanges[1];
            }
            // Complementation: partially recover to wild-type level
            double recoveryRatio = 0.4 + random.nextDouble() * 0.4; // 40%-80% recovery, more obvious complementation effect
            foldChanges[2] = 1.0 + (foldChanges[1] - 1.0) * (1.0 - recoveryRatio);
        }

        return foldChanges;
    }

    /**
     * Group samples by type
     * @return Map of type names to sample indices
     */
    private Map<String, List<Integer>> groupSamplesByType() {
        Map<String, List<Integer>> groups = new LinkedHashMap<>();

        for (int i = 0; i < sampleLabels.size(); i++) {
            String label = sampleLabels.get(i);
            String type = label.replaceAll("_\\d+$", "");

            groups.computeIfAbsent(type, k -> new ArrayList<>()).add(i);
        }

        return groups;
    }

    /**
     * Generate base expression level
     * @return Base expression level
     */
    private double generateBaseExpression() {
        // Use log-normal distribution to generate base expression levels
        double logMean = Math.log(baseMean);
        double logStd = 0.5; // Reduce variability to make base expression more stable
        double logValue = random.nextGaussian() * logStd + logMean;
        return Math.exp(logValue);
    }

    /**
     * Sample from negative binomial distribution
     * Using Gamma-Poisson mixture model
     * @param mean Mean of the distribution
     * @param dispersion Dispersion parameter
     * @return Sampled value
     */
    private double sampleNegativeBinomial(double mean, double dispersion) {
        if (mean <= 0) return 0;

        // Negative binomial distribution parameter conversion
        double alpha = 1.0 / dispersion; // shape parameter
        double beta = dispersion / mean;  // rate parameter

        // First sample lambda from Gamma distribution
        double lambda = sampleGamma(alpha, 1.0 / beta);

        // Then sample from Poisson distribution
        return samplePoisson(lambda);
    }

    /**
     * Sample from Gamma distribution (using Marsaglia and Tsang method)
     * @param alpha Shape parameter
     * @param scale Scale parameter
     * @return Sampled value
     */
    private double sampleGamma(double alpha, double scale) {
        if (alpha < 1.0) {
            return sampleGamma(alpha + 1.0, scale) * Math.pow(random.nextDouble(), 1.0 / alpha);
        }

        double d = alpha - 1.0 / 3.0;
        double c = 1.0 / Math.sqrt(9.0 * d);

        while (true) {
            double x, v;
            do {
                x = random.nextGaussian();
                v = 1.0 + c * x;
            } while (v <= 0);

            v = v * v * v;
            double u = random.nextDouble();

            if (u < 1.0 - 0.0331 * x * x * x * x) {
                return d * v * scale;
            }

            if (Math.log(u) < 0.5 * x * x + d * (1.0 - v + Math.log(v))) {
                return d * v * scale;
            }
        }
    }

    /**
     * Sample from Poisson distribution
     * @param lambda Lambda parameter
     * @return Sampled value
     */
    private double samplePoisson(double lambda) {
        if (lambda < 30) {
            // Use Knuth's algorithm
            double L = Math.exp(-lambda);
            double p = 1.0;
            int k = 0;

            do {
                k++;
                p *= random.nextDouble();
            } while (p > L);

            return k - 1;
        } else {
            // Use normal approximation
            return Math.max(0, random.nextGaussian() * Math.sqrt(lambda) + lambda);
        }
    }

    /**
     * Write data to file
     * @param matrix Expression matrix to write
     * @throws IOException If there's an error writing to the file
     */
    private void writeToFile(double[][] matrix) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            // Write header
            writer.write("GeneID");
            for (String label : sampleLabels) {
                writer.write("\t" + label);
            }
            writer.newLine();

            // Write data
            for (int geneIdx = 0; geneIdx < numberOfGenes; geneIdx++) {
                writer.write("Gene_" + (geneIdx + 1));
                for (int sampleIdx = 0; sampleIdx < numberOfSamples; sampleIdx++) {
                    writer.write(String.format("\t%.2f", matrix[geneIdx][sampleIdx]));
                }
                writer.newLine();
            }
        }
    }

    /**
     * Static method to generate sample labels
     * @param typeLabels Labels for each sample type
     * @param replicatesPerType Number of replicates per type
     * @return List of generated sample labels
     */
    public static List<String> generateSampleLabels(String[] typeLabels, int replicatesPerType) {
        List<String> labels = new ArrayList<>();
        for (String type : typeLabels) {
            for (int i = 1; i <= replicatesPerType; i++) {
                labels.add(type + "_" + i);
            }
        }
        return labels;
    }
}
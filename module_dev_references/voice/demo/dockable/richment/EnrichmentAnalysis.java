package demo.dockable.richment;

import unified.output.UnifiedPrinter;
import unified.output.UnifiedPrinterBuilder;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class for performing gene enrichment analysis including ORA (Over-Representation Analysis) 
 * and GSEA (Gene Set Enrichment Analysis).
 * 
 * This class provides methods to read gene sets from GMT files, read significant gene results,
 * perform enrichment analysis using statistical methods, and save results to output files.
 */
public class EnrichmentAnalysis {

    /**
     * Represents a gene set with name, description and a collection of genes
     */
    public static class GeneSet {
        public String name;
        public String description;
        public Set<String> genes;

        /**
         * Constructor for GeneSet
         * @param name Name of the gene set
         * @param description Description of the gene set
         * @param genes Set of genes in this gene set
         */
        public GeneSet(String name, String description, Set<String> genes) {
            this.name = name;
            this.description = description;
            this.genes = genes;
        }
    }

    /**
     * Represents the result of an ORA (Over-Representation Analysis)
     */
    public static class EnrichmentResult {
        public String pathwayName;
        public String description;
        public int genesInPathway;
        public int significantGenesInPathway;
        public int totalSignificantGenes;
        public int totalGenes;
        public double pValue;
        public double fdr;
        public String genesInSet;

        /**
         * Constructor for EnrichmentResult
         * @param pathwayName Name of the pathway
         * @param description Description of the pathway
         * @param genesInPathway Number of genes in the pathway
         * @param significantGenesInPathway Number of significant genes in the pathway
         * @param totalSignificantGenes Total number of significant genes
         * @param totalGenes Total number of genes
         * @param pValue P-value of the enrichment
         * @param genesInSet Genes in the set as a semicolon-separated string
         */
        public EnrichmentResult(String pathwayName, String description, int genesInPathway,
                                int significantGenesInPathway, int totalSignificantGenes,
                                int totalGenes, double pValue, String genesInSet) {
            this.pathwayName = pathwayName;
            this.description = description;
            this.genesInPathway = genesInPathway;
            this.significantGenesInPathway = significantGenesInPathway;
            this.totalSignificantGenes = totalSignificantGenes;
            this.totalGenes = totalGenes;
            this.pValue = pValue;
            this.genesInSet = genesInSet;
        }
    }

    /**
     * Represents the result of a GSEA (Gene Set Enrichment Analysis)
     */
    public static class GSEAResult {
        public String pathwayName;
        public String description;
        public double enrichmentScore;
        public double normalizedES;
        public double pValue;
        public double fdr;
        public int size;
        public String leadingEdge;

        /**
         * Constructor for GSEAResult
         * @param pathwayName Name of the pathway
         * @param description Description of the pathway
         * @param enrichmentScore Enrichment score
         * @param normalizedES Normalized enrichment score
         * @param pValue P-value of the enrichment
         * @param size Size of the gene set
         * @param leadingEdge Leading edge genes
         */
        public GSEAResult(String pathwayName, String description, double enrichmentScore,
                          double normalizedES, double pValue, int size, String leadingEdge) {
            this.pathwayName = pathwayName;
            this.description = description;
            this.enrichmentScore = enrichmentScore;
            this.normalizedES = normalizedES;
            this.pValue = pValue;
            this.size = size;
            this.leadingEdge = leadingEdge;
        }
    }

    private static UnifiedPrinter out;

    /**
     * Read gene sets from a GMT file
     * @param gmtFileName Path to the GMT file
     * @return List of GeneSet objects
     * @throws IOException If there's an error reading the file
     */
    public static List<GeneSet> readGMTFile(String gmtFileName) throws IOException {
        List<GeneSet> geneSets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(gmtFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length >= 3) {
                    String name = parts[0];
                    String description = parts[1];
                    Set<String> genes = new HashSet<>();

                    // From column 3 onwards are gene list
                    for (int i = 2; i < parts.length; i++) {
                        if (!parts[i].trim().isEmpty()) {
                            genes.add(parts[i].trim());
                        }
                    }

                    if (!genes.isEmpty()) {
                        geneSets.add(new GeneSet(name, description, genes));
                    }
                }
            }
        }

        return geneSets;
    }

    /**
     * Read significant gene results from a file
     * @param fileName Path to the results file
     * @param geneSymbolColName Name of the gene symbol column
     * @param significantColName Name of the significance column (e.g., p-value)
     * @param pValueThreshold Threshold for considering a gene significant
     * @return Map of gene names to their significance scores
     * @throws IOException If there's an error reading the file
     */
    public static Map<String, Double> readSignificantResults(String fileName, String geneSymbolColName,
                                                             String significantColName, double pValueThreshold) throws IOException {
        Map<String, Double> geneScores = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String headerLine = reader.readLine();
            if (headerLine == null) return geneScores;

            String[] headers = headerLine.split("\t");
            int geneColIndex = -1;
            int pValueColIndex = -1;

            // Find corresponding column indices
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(geneSymbolColName)) {
                    geneColIndex = i;
                }
                if (headers[i].equals(significantColName)) {
                    pValueColIndex = i;
                }
            }

            if (geneColIndex == -1 || pValueColIndex == -1) {
                throw new IllegalArgumentException("Could not find specified column names");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length > Math.max(geneColIndex, pValueColIndex)) {
                    String geneName = parts[geneColIndex].trim();
                    try {
                        double pValue = Double.parseDouble(parts[pValueColIndex]);
                        if (pValue <= pValueThreshold) {
                            geneScores.put(geneName, pValue);
                        }
                    } catch (NumberFormatException e) {
                        // Skip rows that cannot be parsed
                    }
                }
            }
        }

        return geneScores;
    }

    /**
     * Perform ORA (Over-Representation Analysis) using Fisher's exact test
     * @param geneSets List of gene sets to analyze
     * @param significantGenes Set of significant genes
     * @param backgroundGenes Set of background genes
     * @param pValueThreshold Threshold for considering a gene significant
     * @return List of enrichment results
     */
    public static List<EnrichmentResult> performORA(List<GeneSet> geneSets, Set<String> significantGenes,
                                                    Set<String> backgroundGenes, double pValueThreshold) {
        List<EnrichmentResult> results = new ArrayList<>();

        int totalGenes = backgroundGenes.size();
        int totalSignificantGenes = significantGenes.size();

        // Calculate results for each gene set, regardless of significance
        for (GeneSet geneSet : geneSets) {
            // Calculate intersection
            Set<String> genesInPathway = new HashSet<>(geneSet.genes);
            genesInPathway.retainAll(backgroundGenes); // Only consider genes in the background set

            Set<String> significantGenesInPathway = new HashSet<>(genesInPathway);
            significantGenesInPathway.retainAll(significantGenes);

            int k = significantGenesInPathway.size(); // Number of significant genes in the pathway
            int n = genesInPathway.size(); // Total number of genes in the pathway
            int K = totalSignificantGenes; // Total number of significant genes
            int N = totalGenes; // Total number of genes

            double pValue = 1.0; // Default p-value is 1

            if (k > 0 && n >= 1) { // Calculate as long as there are genes
                // Calculate p-value using hypergeometric distribution
                pValue = calculateHypergeometricPValue(k, n, K, N);
            }

            String genesInSetStr = String.join(";", significantGenesInPathway);
            results.add(new EnrichmentResult(geneSet.name, geneSet.description, n, k,
                    K, N, pValue, genesInSetStr));
        }

        // Sort by p-value
        results.sort(Comparator.comparingDouble(r -> r.pValue));

        // Calculate FDR (Benjamini-Hochberg)
        calculateFDR(results);

        return results;
    }

    /**
     * Perform GSEA (Gene Set Enrichment Analysis)
     * @param geneSets List of gene sets to analyze
     * @param geneScores Map of gene names to their scores
     * @param permutations Number of permutations for significance testing
     * @return List of GSEA results
     */
    public static List<GSEAResult> performGSEA(List<GeneSet> geneSets, Map<String, Double> geneScores,
                                               int permutations) {
        List<GSEAResult> results = new ArrayList<>();

        // Create sorted gene list (sorted by scores, using -log10(p-value) as score)
        List<Map.Entry<String, Double>> sortedGenes = geneScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(-Math.log10(e2.getValue()), -Math.log10(e1.getValue())))
                .collect(Collectors.toList());

        List<String> rankedGeneList = sortedGenes.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Calculate results for each gene set
        for (GeneSet geneSet : geneSets) {
            GSEAResult result = calculateEnrichmentScore(geneSet, rankedGeneList, geneScores, permutations);
            if (result != null) {
                results.add(result);
            }
        }

        // Sort by absolute value of enrichment score
        results.sort((r1, r2) -> Double.compare(Math.abs(r2.enrichmentScore), Math.abs(r1.enrichmentScore)));

        // Calculate FDR
        calculateGSEAFDR(results);

        return results;
    }

    /**
     * Calculate enrichment score
     * @param geneSet Gene set to analyze
     * @param rankedGeneList Ranked list of genes
     * @param geneScores Map of gene names to their scores
     * @param permutations Number of permutations for significance testing
     * @return GSEA result
     */
    private static GSEAResult calculateEnrichmentScore(GeneSet geneSet, List<String> rankedGeneList,
                                                       Map<String, Double> geneScores, int permutations) {
        int N = rankedGeneList.size();
        Set<String> geneSetGenes = geneSet.genes;

        // Calculate actual enrichment score
        double actualES = computeES(rankedGeneList, geneSetGenes, geneScores);

        // Calculate p-value through permutation test
        int moreThanActual = 0;
        List<Double> permutationESs = new ArrayList<>();

        Random random = new Random(42); // Fixed seed for reproducibility

        for (int i = 0; i < permutations; i++) {
            // Randomly shuffle gene list
            List<String> shuffledGenes = new ArrayList<>(rankedGeneList);
            Collections.shuffle(shuffledGenes, random);

            double permES = computeES(shuffledGenes, geneSetGenes, geneScores);
            permutationESs.add(permES);

            if (Math.abs(permES) >= Math.abs(actualES)) {
                moreThanActual++;
            }
        }

        double pValue = (double) moreThanActual / permutations;

        // Calculate normalized enrichment score
        double meanPermES = permutationESs.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double normalizedES = actualES / (meanPermES != 0 ? Math.abs(meanPermES) : 1.0);

        // Find leading edge genes
        String leadingEdge = findLeadingEdge(rankedGeneList, geneSetGenes, actualES);

        return new GSEAResult(geneSet.name, geneSet.description, actualES, normalizedES,
                pValue, geneSetGenes.size(), leadingEdge);
    }

    /**
     * Core algorithm for computing enrichment score
     * @param rankedGenes Ranked list of genes
     * @param geneSet Gene set to analyze
     * @param geneScores Map of gene names to their scores
     * @return Enrichment score
     */
    private static double computeES(List<String> rankedGenes, Set<String> geneSet, Map<String, Double> geneScores) {
        double runningSum = 0.0;
        double maxES = 0.0;
        double minES = 0.0;

        // Calculate total weight
        double totalWeight = 0.0;
        for (String gene : rankedGenes) {
            if (geneSet.contains(gene) && geneScores.containsKey(gene)) {
                totalWeight += Math.abs(-Math.log10(geneScores.get(gene)));
            }
        }

        int genesInSet = 0;
        for (String gene : geneSet) {
            if (rankedGenes.contains(gene)) {
                genesInSet++;
            }
        }

        for (int i = 0; i < rankedGenes.size(); i++) {
            String gene = rankedGenes.get(i);

            if (geneSet.contains(gene)) {
                // Gene is in the gene set
                double weight = geneScores.containsKey(gene) ?
                        Math.abs(-Math.log10(geneScores.get(gene))) : 1.0;
                runningSum += weight / totalWeight;
            } else {
                // Gene is not in the gene set
                runningSum -= 1.0 / (rankedGenes.size() - genesInSet);
            }

            if (runningSum > maxES) maxES = runningSum;
            if (runningSum < minES) minES = runningSum;
        }

        return Math.abs(maxES) > Math.abs(minES) ? maxES : minES;
    }

    /**
     * Find leading edge genes - improved version
     * @param rankedGenes Ranked list of genes
     * @param geneSet Gene set to analyze
     * @param enrichmentScore Enrichment score
     * @return Leading edge genes as a semicolon-separated string
     */
    private static String findLeadingEdge(List<String> rankedGenes, Set<String> geneSet, double enrichmentScore) {
        List<String> leadingEdgeGenes = new ArrayList<>();
        double runningSum = 0.0;
        double maxRunningSum = 0.0;
        double minRunningSum = 0.0;
        int peakPosition = 0;

        // Calculate total weight
        double totalWeight = 0.0;
        for (String gene : rankedGenes) {
            if (geneSet.contains(gene)) {
                totalWeight += 1.0; // Simplified weight calculation
            }
        }

        int genesInSet = 0;
        for (String gene : geneSet) {
            if (rankedGenes.contains(gene)) {
                genesInSet++;
            }
        }

        // Recalculate running sum to find peak position
        for (int i = 0; i < rankedGenes.size(); i++) {
            String gene = rankedGenes.get(i);

            if (geneSet.contains(gene)) {
                runningSum += 1.0 / totalWeight;
            } else {
                runningSum -= 1.0 / (rankedGenes.size() - genesInSet);
            }

            if (runningSum > maxRunningSum) {
                maxRunningSum = runningSum;
                peakPosition = i;
            }
            if (runningSum < minRunningSum) {
                minRunningSum = runningSum;
                if (Math.abs(minRunningSum) > Math.abs(maxRunningSum)) {
                    peakPosition = i;
                }
            }
        }

        // Collect leading edge genes: genes in the gene set from start to peak position
        for (int i = 0; i <= peakPosition; i++) {
            String gene = rankedGenes.get(i);
            if (geneSet.contains(gene)) {
                leadingEdgeGenes.add(gene);
            }
        }

        return String.join(";", leadingEdgeGenes);
    }

    /**
     * Calculate hypergeometric distribution p-value
     * @param k Observed number of successes
     * @param n Number of successes in the population
     * @param K Sample size
     * @param N Population size
     * @return P-value
     */
    private static double calculateHypergeometricPValue(int k, int n, int K, int N) {
        // k: Observed number of successes
        // n: Number of successes in the population
        // K: Sample size
        // N: Population size

        double pValue = 0.0;

        // Calculate P(X >= k) where X follows hypergeometric distribution
        for (int i = k; i <= Math.min(K, n); i++) {
            pValue += hypergeometricPMF(i, n, K, N);
        }

        return Math.min(1.0, pValue);
    }

    /**
     * Hypergeometric distribution probability mass function
     * @param k Number of successes
     * @param n Number of successes in the population
     * @param K Sample size
     * @param N Population size
     * @return Probability mass
     */
    private static double hypergeometricPMF(int k, int n, int K, int N) {
        // P(X = k) = C(n,k) * C(N-n,K-k) / C(N,K)

        if (k < 0 || k > K || k > n || (K - k) > (N - n)) {
            return 0.0;
        }

        try {
            double numerator = logCombination(n, k) + logCombination(N - n, K - k);
            double denominator = logCombination(N, K);
            return Math.exp(numerator - denominator);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Calculate logarithm of combination C(n,k)
     * @param n Total number of items
     * @param k Number of items to choose
     * @return Logarithm of combination
     */
    private static double logCombination(int n, int k) {
        if (k < 0 || k > n) return Double.NEGATIVE_INFINITY;
        if (k == 0 || k == n) return 0.0;

        // Use symmetry for optimization
        if (k > n - k) k = n - k;

        double result = 0.0;
        for (int i = 0; i < k; i++) {
            result += Math.log(n - i) - Math.log(i + 1);
        }

        return result;
    }

    /**
     * Calculate FDR using Benjamini-Hochberg method
     * @param results List of enrichment results
     */
    private static void calculateFDR(List<EnrichmentResult> results) {
        int n = results.size();
        for (int i = 0; i < n; i++) {
            double fdr = results.get(i).pValue * n / (i + 1);
            results.get(i).fdr = Math.min(1.0, fdr);
        }

        // Ensure FDR is monotonic
        for (int i = n - 2; i >= 0; i--) {
            if (results.get(i).fdr > results.get(i + 1).fdr) {
                results.get(i).fdr = results.get(i + 1).fdr;
            }
        }
    }

    /**
     * Calculate FDR for GSEA results
     * @param results List of GSEA results
     */
    private static void calculateGSEAFDR(List<GSEAResult> results) {
        int n = results.size();
        for (int i = 0; i < n; i++) {
            double fdr = results.get(i).pValue * n / (i + 1);
            results.get(i).fdr = Math.min(1.0, fdr);
        }

        // Ensure FDR is monotonic
        for (int i = n - 2; i >= 0; i--) {
            if (results.get(i).fdr > results.get(i + 1).fdr) {
                results.get(i).fdr = results.get(i + 1).fdr;
            }
        }
    }

    /**
     * Save ORA results to a file
     * @param results List of enrichment results
     * @param outputFileName Output file path
     * @throws IOException If there's an error writing to the file
     */
    public static void saveORAResults(List<EnrichmentResult> results, String outputFileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            writer.println("Pathway\tDescription\tGenes_in_pathway\tSignificant_genes_in_pathway\t" +
                    "Total_significant_genes\tTotal_genes\tP_value\tFDR\tGenes_in_set");

            for (EnrichmentResult result : results) {
                writer.printf("%s\t%s\t%d\t%d\t%d\t%d\t%.6e\t%.6e\t%s%n",
                        result.pathwayName,
                        result.description,
                        result.genesInPathway,
                        result.significantGenesInPathway,
                        result.totalSignificantGenes,
                        result.totalGenes,
                        result.pValue,
                        result.fdr,
                        result.genesInSet);
            }
        }
    }

    /**
     * Save GSEA results to a file
     * @param results List of GSEA results
     * @param outputFileName Output file path
     * @throws IOException If there's an error writing to the file
     */
    public static void saveGSEAResults(List<GSEAResult> results, String outputFileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFileName))) {
            writer.println("Pathway\tDescription\tEnrichment_Score\tNormalized_ES\t" +
                    "P_value\tFDR\tSize\tLeading_Edge");

            for (GSEAResult result : results) {
                writer.printf("%s\t%s\t%.6f\t%.6f\t%.6e\t%.6e\t%d\t%s%n",
                        result.pathwayName,
                        result.description,
                        result.enrichmentScore,
                        result.normalizedES,
                        result.pValue,
                        result.fdr,
                        result.size,
                        result.leadingEdge);
            }
        }
    }

    /**
     * Set output printer
     * @param out UnifiedPrinter instance
     */
    public static void setOut(UnifiedPrinter out) {
        EnrichmentAnalysis.out = out;
    }

    /**
     * Main enrichment analysis method
     * @param gmtFileName Path to GMT file
     * @param significantResultFileName Path to significant results file
     * @param geneSymbolColName Name of gene symbol column
     * @param significantColName Name of significance column
     * @param outputFileName Output file path
     * @param pValueThreshold P-value threshold for significance
     * @throws IOException If there's an error reading or writing files
     */
    public static void performEnrichmentAnalysis(String gmtFileName, String significantResultFileName,
                                                 String geneSymbolColName, String significantColName,
                                                 String outputFileName, double pValueThreshold) throws IOException {

        if (out == null) {
            out = UnifiedPrinterBuilder.getDefaultPrinter();
        }
        System.out.println("Starting enrichment analysis: " + gmtFileName);
        List<GeneSet> geneSets = readGMTFile(gmtFileName);
        System.out.println("Read " + geneSets.size() + " gene sets");

        System.out.println("Starting to read significant results file: " + significantResultFileName);
        Map<String, Double> geneScores = readSignificantResults(significantResultFileName,
                geneSymbolColName, significantColName, 1.0);
        System.out.println("Read " + geneScores.size() + " gene scores");

        // Get significant genes (p < pValueThreshold)
        Set<String> significantGenes = geneScores.entrySet().stream()
                .filter(entry -> entry.getValue() <= pValueThreshold)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        System.out.println("Number of significant genes (p <= " + pValueThreshold + "): " + significantGenes.size());

        // Create background gene set (all genes appearing in GMT file)
        Set<String> backgroundGenes = new HashSet<>();
        for (GeneSet geneSet : geneSets) {
            backgroundGenes.addAll(geneSet.genes);
        }
        // Only keep genes that appear in our data
        backgroundGenes.retainAll(geneScores.keySet());
        System.out.println("Background gene count: " + backgroundGenes.size());

        // Perform ORA analysis
        System.out.println("Starting ORA analysis...");
        List<EnrichmentResult> oraResults = performORA(geneSets, significantGenes, backgroundGenes, 1.0); // Output all results
        System.out.println("ORA analysis completed, analyzed " + oraResults.size() + " gene sets");

        // Save ORA results
        String oraOutputFile = outputFileName.replace(".tsv", "_ORA.tsv");
        saveORAResults(oraResults, oraOutputFile);
        System.out.println("ORA results saved to: " + oraOutputFile);

        // Perform GSEA analysis
        System.out.println("Starting GSEA analysis...");
        List<GSEAResult> gseaResults = performGSEA(geneSets, geneScores, 1000);
        System.out.println("GSEA analysis completed, found " + gseaResults.size() + " pathway results");

        // Save GSEA results
        String gseaOutputFile = outputFileName.replace(".tsv", "_GSEA.tsv");
        saveGSEAResults(gseaResults, gseaOutputFile);
        System.out.println("GSEA results saved to: " + gseaOutputFile);
    }

    /**
     * Append text to console
     * @param text Text to append
     */
    public static void appendText2Console(String text) {
        System.out.println(text);
    }

    /**
     * Main method example
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        String outputFileName = "output.tsv";
        String inputGMTFileName = "h.all.v2025.1.Hs.entrez.gmt";
        String inputSignificantResultFileName = "output_Mann_Whitney_U.txt";
        String geneSymbolColName = "GeneName";
        String significantColName = "p.value";

        try {
            performEnrichmentAnalysis(inputGMTFileName, inputSignificantResultFileName,
                    geneSymbolColName, significantColName, outputFileName, 0.05);
            appendText2Console("Finished Computation: " + outputFileName);
        } catch (IOException e) {
            System.err.println("An error occurred during analysis: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
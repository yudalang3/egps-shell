package demo.dockable.algo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.random.RandomGenerator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A class for simulating sequence alignments with mutations.
 * This simulator generates multiple sequences based on a reference sequence,
 * introducing substitutions, insertions, and deletions according to specified probabilities.
 * It then performs multiple sequence alignment using a simplified global alignment algorithm.
 */
public class AlignmentSimulator {
    private final String refSequence;
    private final int numSequences;
    private final boolean includeIndel;
    private final String outputFile;
    private final String outputLogPath;
    private final double subProb;
    private final double insProb;
    private final double delProb;

    /**
     * Constructor for AlignmentSimulator
     * @param refSequence The reference sequence to base simulations on
     * @param numSequences Number of sequences to generate
     * @param includeIndel Whether to include insertions and deletions
     * @param outputFile Path to output FASTA file
     * @param outputLogPath Path to output mutation log file
     * @param subProb Probability of substitution
     * @param insProb Probability of insertion
     * @param delProb Probability of deletion
     */
    public AlignmentSimulator(String refSequence, int numSequences, boolean includeIndel,
                              String outputFile, String outputLogPath,
                              double subProb, double insProb, double delProb) {
        this.refSequence = refSequence;
        this.numSequences = numSequences;
        this.includeIndel = includeIndel;
        this.outputFile = outputFile;
        this.outputLogPath = outputLogPath;
        this.subProb = subProb;
        this.insProb = includeIndel ? insProb : 0.0;
        this.delProb = includeIndel ? delProb : 0.0;
    }

    /**
     * Run the sequence simulation and alignment process
     * @throws IOException If there's an error writing to files
     */
    public void runSimulation() throws IOException {
        List<List<Character>> sequences = new ArrayList<>();
        List<List<String>> mutationLogs = new ArrayList<>();

        // Initialize sequence lists
        for (int i = 0; i < numSequences; i++) {
            sequences.add(new ArrayList<>());
            mutationLogs.add(new ArrayList<>());
        }

        // Reference sequence as the first sequence
        for (char c : refSequence.toCharArray()) {
            sequences.get(0).add(c);
        }

        RandomGenerator rand = ThreadLocalRandom.current();

        // Generate mutations for each non-reference sequence
        for (int seqIdx = 1; seqIdx < numSequences; seqIdx++) {
            List<Character> currentSeq = new ArrayList<>();
            List<String> logs = mutationLogs.get(seqIdx);
            String seqName = "seq" + seqIdx;

            int refPos = 0;
            while (refPos < refSequence.length()) {
                char refBase = refSequence.charAt(refPos);
                double r = rand.nextDouble();

                if (includeIndel && r < insProb) {
                    // Insertion operation: insert a random base at current position
                    char insBase = randomBase(rand);
                    currentSeq.add(insBase);
                    logs.add(seqName + "\tINS\t" + refPos + "\t" + insBase);
                    // Note: After insertion, do not move reference position as insertion is relative to reference
                } else if (includeIndel && r < insProb + delProb) {
                    // Deletion operation: skip the current reference base
                    logs.add(seqName + "\tDEL\t" + refPos + "\t" + refBase);
                    refPos++; // Move to next reference position
                } else if (r < insProb + delProb + subProb) {
                    // Substitution operation
                    char subBase = randomDifferentBase(rand, refBase);
                    currentSeq.add(subBase);
                    logs.add(seqName + "\tSUB\t" + refPos + "\t" + refBase + "->" + subBase);
                    refPos++;
                } else {
                    // Match: keep consistent with reference sequence
                    currentSeq.add(refBase);
                    refPos++;
                }
            }
            sequences.set(seqIdx, currentSeq);
        }

        // Perform multiple sequence alignment
        alignSequences(sequences);

        // Write to FASTA format
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            writer.write(">seq.ref\n");
            writer.write(sequenceToString(sequences.get(0)));
            writer.newLine();

            for (int i = 1; i < numSequences; i++) {
                writer.write(">seq" + i + "\n");
                writer.write(sequenceToString(sequences.get(i)));
                writer.newLine();
            }
        }

        // Write mutation log
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputLogPath))) {
            for (List<String> logs : mutationLogs) {
                for (String log : logs) {
                    writer.write(log);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * Perform multiple sequence alignment using a simplified global alignment algorithm
     * @param sequences List of sequences to align
     */
    private void alignSequences(List<List<Character>> sequences) {
        if (sequences.size() < 2) return;

        List<Character> reference = sequences.get(0);

        for (int seqIdx = 1; seqIdx < sequences.size(); seqIdx++) {
            List<Character> currentSeq = sequences.get(seqIdx);

            // Use dynamic programming for sequence alignment
            AlignmentResult result = performAlignment(reference, currentSeq);

            // Update reference and current sequences
            sequences.set(0, result.alignedRef);
            sequences.set(seqIdx, result.alignedSeq);

            // Update other already aligned sequences
            for (int i = 1; i < seqIdx; i++) {
                sequences.set(i, insertGapsInSequence(sequences.get(i), result.refGapPositions));
            }

            reference = sequences.get(0); // Update reference sequence
        }
    }

    /**
     * Perform alignment of two sequences
     * @param seq1 First sequence
     * @param seq2 Second sequence
     * @return AlignmentResult containing the aligned sequences
     */
    private AlignmentResult performAlignment(List<Character> seq1, List<Character> seq2) {
        int m = seq1.size();
        int n = seq2.size();

        // Dynamic programming matrix
        int[][] dp = new int[m + 1][n + 1];

        // Initialization
        for (int i = 0; i <= m; i++) dp[i][0] = i * (-1); // gap penalty
        for (int j = 0; j <= n; j++) dp[0][j] = j * (-1);

        // Fill the matrix
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int match = dp[i-1][j-1] + (seq1.get(i-1).equals(seq2.get(j-1)) ? 2 : -1);
                int delete = dp[i-1][j] - 1;
                int insert = dp[i][j-1] - 1;
                dp[i][j] = Math.max(Math.max(match, delete), insert);
            }
        }

        // Traceback to build alignment
        List<Character> alignedSeq1 = new ArrayList<>();
        List<Character> alignedSeq2 = new ArrayList<>();
        List<Integer> refGapPositions = new ArrayList<>();

        int i = m, j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 && dp[i][j] == dp[i-1][j-1] + (seq1.get(i-1).equals(seq2.get(j-1)) ? 2 : -1)) {
                alignedSeq1.add(0, seq1.get(i-1));
                alignedSeq2.add(0, seq2.get(j-1));
                i--; j--;
            } else if (i > 0 && dp[i][j] == dp[i-1][j] - 1) {
                alignedSeq1.add(0, seq1.get(i-1));
                alignedSeq2.add(0, '-');
                i--;
            } else {
                alignedSeq1.add(0, '-');
                alignedSeq2.add(0, seq2.get(j-1));
                refGapPositions.add(0, alignedSeq1.size() - 1);
                j--;
            }
        }

        return new AlignmentResult(alignedSeq1, alignedSeq2, refGapPositions);
    }

    /**
     * Insert gaps at specified positions in a sequence
     * @param sequence The sequence to insert gaps into
     * @param gapPositions Positions where gaps should be inserted
     * @return Sequence with gaps inserted
     */
    private List<Character> insertGapsInSequence(List<Character> sequence, List<Integer> gapPositions) {
        List<Character> result = new ArrayList<>(sequence);
        for (int pos : gapPositions) {
            if (pos < result.size()) {
                result.add(pos, '-');
            }
        }
        return result;
    }

    /**
     * Convert a list of characters to a string
     * @param sequence List of characters representing a sequence
     * @return String representation of the sequence
     */
    private String sequenceToString(List<Character> sequence) {
        StringBuilder sb = new StringBuilder();
        for (Character c : sequence) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Generate a random nucleotide base
     * @param rand Random number generator
     * @return Randomly selected base (A, T, C, or G)
     */
    private char randomBase(RandomGenerator rand) {
        char[] bases = {'A', 'T', 'C', 'G'};
        return bases[rand.nextInt(bases.length)];
    }

    /**
     * Generate a random nucleotide base different from the original
     * @param rand Random number generator
     * @param original Original base to avoid
     * @return Randomly selected base different from original
     */
    private char randomDifferentBase(RandomGenerator rand, char original) {
        char[] bases = {'A', 'T', 'C', 'G'};
        char base;
        do {
            base = bases[rand.nextInt(bases.length)];
        } while (base == original);
        return base;
    }

    /**
     * Class to hold alignment results
     */
    private static class AlignmentResult {
        final List<Character> alignedRef;
        final List<Character> alignedSeq;
        final List<Integer> refGapPositions;

        /**
         * Constructor for AlignmentResult
         * @param alignedRef Aligned reference sequence
         * @param alignedSeq Aligned sequence
         * @param refGapPositions Positions of gaps in reference sequence
         */
        AlignmentResult(List<Character> alignedRef, List<Character> alignedSeq, List<Integer> refGapPositions) {
            this.alignedRef = alignedRef;
            this.alignedSeq = alignedSeq;
            this.refGapPositions = refGapPositions;
        }
    }
}
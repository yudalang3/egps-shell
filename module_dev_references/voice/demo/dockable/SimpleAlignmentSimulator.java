package demo.dockable;

import demo.dockable.algo.AlignmentSimulator;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * A dockable tab module for simulating multiple sequence alignments.
 * This module generates a set of sequences based on a reference sequence with simulated mutations.
 */
public class SimpleAlignmentSimulator extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(SimpleAlignmentSimulator.class);

    /**
     * Constructor for the SimpleAlignmentSimulator module
     * @param cmf The computational module face
     */
    public SimpleAlignmentSimulator(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the alignment simulation
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("ref.sequence","ATGCGCATCGATCAGTCAGCTAGCATCGATCAGCTAGCAT", "The referenced DNA sequence to simulate.");
        designer.addKeyValueEntryBean("num.sequence","5", "Total number of sequence to output, including the reference.");
        designer.addKeyValueEntryBean("include.indel","F", "Whether include the INDEL (insertion and deletion).");
        designer.addKeyValueEntryBean("output.file.path","", "Output file path");
    }

    /**
     * Execute the alignment simulation process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If simulation fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("output.file.path");
        String refSequence = o.getSimplifiedString("ref.sequence");
        int numSequence = o.getSimplifiedInt("num.sequence");
        boolean includeIndel = o.getSimplifiedBool("include.indel");

        // Set default mutation probabilities
        double subProb = 0.02;  // Substitution probability
        double insProb = 0.02;  // Insertion probability
        double delProb = 0.02;  // Deletion probability

        // Adjust probabilities if INDELs are disabled
        if (!includeIndel){
            insProb = 0;
            delProb = 0;
        }

        // Define log file path for mutation records
        String logPath = fileName + ".mutations.log";

        // Create and configure the alignment simulator
        AlignmentSimulator simulator = new AlignmentSimulator(
                refSequence, numSequence, includeIndel,
                fileName, logPath, subProb, insProb, delProb
        );

        // Run the simulation process
        simulator.runSimulation();

        // Report completion to console
        setText4Console(Arrays.asList("Finished writing file: " + fileName,
                "Mutation log written to: " + logPath));
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "Simulate the short length multiple sequence alignment for demo";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "2.Simple alignment Simulator";
    }
}
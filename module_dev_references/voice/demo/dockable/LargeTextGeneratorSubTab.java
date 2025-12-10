package demo.dockable;

import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A dockable tab module for generating large text files in FASTQ format.
 * This module is useful for testing file I/O operations and memory management with large files.
 */
public class LargeTextGeneratorSubTab extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(LargeTextGeneratorSubTab.class);

    /**
     * Constructor for the LargeTextGeneratorSubTab module
     * @param cmf The computational module face
     */
    public LargeTextGeneratorSubTab(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the module
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("output.file.path","", "Output file location. E.g. 10GB_fastq.txt");
        designer.addKeyValueEntryBean("file.GiB.size","10", "The binary giga byte size, with GiB, note this is not GB");
    }

    /**
     * Execute the file generation process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If file I/O operations fail
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("output.file.path");
        int size = o.getSimplifiedInt("file.GiB.size");

        long targetSizeBytes = 1024L * 1024 * 1024 * size; // Convert GiB to bytes
        String line = "@SEQ_ID_001\n" +
                "ATGCGGCCGCCTACGTAGCATGCGGCCGCCTACGTAGCATGCGGCCGCCTACGTAGC\n" +
                "+\n" +
                "!''*((((***+))%%%++,!''*((((***+))%%%++,!''*((((***+))%%%++,\n";
        long length = targetSizeBytes / line.length();

        log.trace("Generating file: {} with line {}", fileName, length);

        int bufferSize = 1024 * 1024; // 1MB buffer for efficient writing
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName), bufferSize)) {
            // Write the same line repeatedly to reach the target file size
            for (int i = 0; i < length; i++) {
                writer.write(line);
            }
        } catch (IOException e) {
            throw e;
        }

        appendText2Console("Finished writing file: " + fileName);
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "Generate the fastq file with text, volume greater than 10 GB.";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "1.Large fastq file generator";
    }
}
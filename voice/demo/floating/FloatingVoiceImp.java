package demo.floating;

import egps2.EGPSProperties;
import demo.floating.work.BioinformaticsApp;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.builtin.modules.voice.template.AbstractGuiBaseVoiceFeaturedPanel;
import utils.string.EGPSStringUtil;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the floating voice panel for bioinformatics visualization.
 * This class handles the parameter configuration and execution logic for the floating
 * bioinformatics visualization modules, including multiple sequence alignment,
 * expression data visualization, and molecular mechanism diagrams.
 */
public class FloatingVoiceImp extends AbstractGuiBaseVoiceFeaturedPanel {
    private final GuiMain guiMain;

    /**
     * Constructor for FloatingVoiceImp
     * @param guiMain The main GUI component to which this implementation belongs
     */
    public FloatingVoiceImp(GuiMain guiMain) {
        this.guiMain = guiMain;
    }

    /**
     * Define parameters for the floating voice panel
     * @param mapProducer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        mapProducer.addKeyValueEntryBean("%1","Alignment file for evolution","");
        mapProducer.addKeyValueEntryBean("input.msa.fasta",
                EGPSProperties.PROPERTIES_DIR + "/path/to/test.fa",
                "The multiple sequence alignment file\n#Actually, it is simulated by self");

        mapProducer.addKeyValueEntryBean("%2","Expression files","");
        mapProducer.addKeyValueEntryBean("input.expression.tpm",
                EGPSProperties.PROPERTIES_DIR + "/path/to/floatingVoice/exp.tpm",
                "The normalized tmp fasta file\n#Actually, it is simulated by self");

        mapProducer.addKeyValueEntryBean("%3","Molecular interaction files","");
        mapProducer.addKeyValueEntryBean("input.mol.picture",
                EGPSProperties.PROPERTIES_DIR + "/path/to/mol.mechanism.pptx",
                "The power point file of the molecular mechanism\n#Please download the test data, first.\n#While this is demo, just click button.");
    }

    /**
     * Execute the floating voice panel functionality
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If execution fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {

//        String fasta_path = o.getSimplifiedString("input.msa.fasta");
//        Optional<List<String>> lists = o.getComplicatedValues("input.expression.tpm");
//        boolean has_fast = o.getSimplifiedBool("has.msa.fasta");
//        double dispersion = o.getSimplifiedDouble("model.dispersion");
//        int number = o.getSimplifiedInt("model.gene.number");
//        String simplifiedStringWithDefault = o.getSimplifiedStringWithDefault("model.differentialRatio");

        BioinformaticsApp bioinformaticsApp = new BioinformaticsApp();
        guiMain.addContent(bioinformaticsApp);

        bioinformaticsApp.displayData();
    }

}
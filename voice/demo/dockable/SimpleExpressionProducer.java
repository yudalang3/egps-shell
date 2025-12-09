package demo.dockable;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import demo.dockable.algo.ExpressionProfileSimulator;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.string.EGPSStringUtil;

import java.util.List;

/**
 * A dockable tab module for simulating gene expression profiles.
 * This module generates synthetic gene expression data with specified parameters
 * for testing and demonstration purposes.
 */
public class SimpleExpressionProducer extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(SimpleExpressionProducer.class);

    /**
     * Constructor for the SimpleExpressionProducer module
     * @param cmf The computational module face
     */
    public SimpleExpressionProducer(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the expression profile simulation
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("meta.info.type.number","2", "Conditions, 2 for control/case, 3 for ctrl/depletion/rescue.");
        designer.addKeyValueEntryBean("meta.info.label","Ctrl;Treat", "$meta.info.type.number 2 for two name, separate with char ;. Example like WT/KO");
        designer.addKeyValueEntryBean("output.file.path","", "The output file path.");

        designer.addKeyValueEntryBean("^", "Advanced simulation parameters", "");
        designer.addKeyValueEntryBean("%1","Meta information", "");
        designer.addKeyValueEntryBean("model.gene.number","20000", "The gene number");
        designer.addKeyValueEntryBean("model.differentialRatio","0.01", "The gene number of differential ratio");
        designer.addKeyValueEntryBean("%2","Negative binomial distribution parameters", "");
        designer.addKeyValueEntryBean("model.baseMean","50.0", "Base expression mean");
        designer.addKeyValueEntryBean("model.dispersion","0.02", "Dispersion parameter");
        designer.addKeyValueEntryBean("model.foldChangeMin","3.0", "Minimum fold change");
        designer.addKeyValueEntryBean("model.foldChangeMax","8.0", "Maximum fold change");
    }

    /**
     * Execute the expression profile simulation process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If simulation fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("output.file.path");
        int typeNumber = o.getSimplifiedInt("meta.info.type.number");
        String str = o.getSimplifiedString("meta.info.label");
        String[] splits = EGPSStringUtil.split(str, ';');
        if (typeNumber != splits.length){
            throw new IllegalArgumentException("$meta.info.type.number is not match with $meta.info.label, check yourself.");
        }

        // Generate sample labels for each condition
        List<String> metaInfoLabels = Lists.newArrayList();
        for (String s : splits){
            for (int i = 1; i <= 3; i++){
                String concat = s.concat("_" + i);
                metaInfoLabels.add(concat);
            }
        }

        // Extract simulation parameters from user input
        int numberOfGenes = o.getSimplifiedInt("model.gene.number");
        double differentialRatio = o.getSimplifiedDouble("model.differentialRatio");
        double baseMean = o.getSimplifiedDouble("model.baseMean");
        double dispersion = o.getSimplifiedDouble("model.dispersion");
        double foldChangeMin = o.getSimplifiedDouble("model.foldChangeMin");
        double foldChangeMax = o.getSimplifiedDouble("model.foldChangeMax");
        
        // Create and configure the expression profile simulator
        ExpressionProfileSimulator simulator = new ExpressionProfileSimulator(
                numberOfGenes,
                metaInfoLabels,
                fileName, differentialRatio, baseMean , dispersion , foldChangeMin, foldChangeMax
        );

        // Start timing the simulation process
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Generate the expression profile
        List<String> ret = simulator.generateExpressionProfile();

        // Stop timing and add elapsed time to results
        stopwatch.stop();
        ret.add("Take time of  " + stopwatch.toString());

        // Display results in console
        setText4Console(ret);
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "Simulate the expression profile for demo";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "3. Simple expression producer";
    }
}
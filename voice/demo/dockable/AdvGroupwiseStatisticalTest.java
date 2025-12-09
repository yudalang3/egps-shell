package demo.dockable;

import com.google.common.collect.Lists;
import demo.dockable.sigtest.AdvancedDifferentialExpressionAnalyzer;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * A dockable tab module for performing advanced group-wise statistical tests on expression data.
 * This module allows users to perform advanced differential expression analysis using methods
 * such as limma, DESeq2, and EdgeR, which are commonly used in bioinformatics for RNA-seq data analysis.
 * 
 * The results can be used for downstream visualization such as MA plots.
 */
public class AdvGroupwiseStatisticalTest extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(AdvGroupwiseStatisticalTest.class);

    /**
     * Constructor for the AdvGroupwiseStatisticalTest module
     * @param cmf The computational module face
     */
    public AdvGroupwiseStatisticalTest(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the advanced statistical test
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("input.expression.tsv","", "Input the tsv file of the expression profile.\n# first column is the gene symbol, must have header line.");
        designer.addKeyValueEntryBean("condition.or.group.1","Ctrl_1;Ctrl_2;Ctrl_3", "The samples name of first condition or group.");
        designer.addKeyValueEntryBean("condition.or.group.2","Treat_1;Treat_2;Treat_3", "The samples name of second condition or group.");
        designer.addKeyValueEntryBean("%","Three methods: limma, DESeq2 and EdgeR all will run", "===About methodology:");
    }

    /**
     * Execute the advanced statistical test process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If the analysis fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("input.expression.tsv");
        String tmp1 = o.getSimplifiedString("condition.or.group.1");
        String tmp2 = o.getSimplifiedString("condition.or.group.2");
        String[] group1 = tmp1.split(";");
        String[] group2 = tmp2.split(";");

        AdvancedDifferentialExpressionAnalyzer analyzer = new AdvancedDifferentialExpressionAnalyzer();

        List<String> ret = Lists.newArrayList();

        ret.add("=== Advanced Differential Expression Analysis ===");
        ret.add("Input file: " + fileName);
        ret.add("Control group: " + String.join(", ", group1));
        ret.add("Treatment group: " + String.join(", ", group2));
        ret.add("");

        // Display available methods
        ret.add("Available advanced methods:");
        for (String method : analyzer.getAvailableMethods()) {
            ret.add("- " + method);
        }
        ret.add("");

        // Run comparison analysis
        analyzer.compareMethodsAnalysis(fileName, group1, group2, "advanced_sig_analysis");
        ret.add("\nAll analyses completed!");
        setText4Console(ret);
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "Statistical test to prepare for the MA plot visualization";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "4.2 Advanced Group-wise Test";
    }
}
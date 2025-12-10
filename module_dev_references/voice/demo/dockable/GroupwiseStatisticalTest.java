package demo.dockable;

import demo.dockable.sigtest.DifferentialExpressionAnalyzer;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Set;

/**
 * A dockable tab module for performing group-wise statistical tests on expression data.
 * This module allows users to perform various statistical tests to identify differentially
 * expressed genes between two conditions or groups, preparing data for downstream visualization
 * such as MA plots.
 */
public class GroupwiseStatisticalTest extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(GroupwiseStatisticalTest.class);

    DifferentialExpressionAnalyzer analyzer = new DifferentialExpressionAnalyzer();

    /**
     * Constructor for the GroupwiseStatisticalTest module
     * @param cmf The computational module face
     */
    public GroupwiseStatisticalTest(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the statistical test
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("input.expression.tsv","", "Input the tsv file of the expression profile.\n# first column is the gene symbol, must have header line.");
        designer.addKeyValueEntryBean("condition.or.group.1","Ctrl_1;Ctrl_2;Ctrl_3", "The samples name of first condition or group.");
        designer.addKeyValueEntryBean("condition.or.group.2","Treat_1;Treat_2;Treat_3", "The samples name of second condition or group.");
        {
            Set<String> availableMethods = analyzer.getAvailableMethods();
            StringBuilder builder = new StringBuilder("Available methods are: ");
            Iterator<String> iterator = availableMethods.iterator();
            String firstMethodName = iterator.next();
            builder.append(firstMethodName);
            while (iterator.hasNext()) {
                builder.append(",").append(iterator.next());
            }
            builder.append(",").append(DifferentialExpressionAnalyzer.ALL_METHOD);
            designer.addKeyValueEntryBean("analysis.method.name", firstMethodName, builder.toString());
        }

        designer.addKeyValueEntryBean("output.result.tsv","", "The output file path");
    }

    /**
     * Execute the statistical test process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If the analysis fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String fileName = o.getSimplifiedString("input.expression.tsv");
        String group1 = o.getSimplifiedString("condition.or.group.1");
        String group2 = o.getSimplifiedString("condition.or.group.2");
        String outputFileName = o.getSimplifiedString("output.result.tsv");

        String[] group1Labels = group1.split(";");
        String[] group2Labels = group2.split(";");
        String methodName = o.getSimplifiedString("analysis.method.name");
        DifferentialExpressionAnalyzer differentialExpressionAnalyzer = new DifferentialExpressionAnalyzer();
        differentialExpressionAnalyzer.performAnalysis(fileName, group1Labels, group2Labels, outputFileName, methodName);

        appendText2Console("Finished writing file: " + fileName);
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
        return "4. Group-wise statistical test";
    }
}
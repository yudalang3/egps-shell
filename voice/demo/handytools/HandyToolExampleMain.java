package demo.handytools;

import java.io.InputStream;

import egps2.EGPSProperties;
import egps2.UnifiedAccessPoint;
import egps2.builtin.modules.voice.VersatileOpenInputClickAbstractGuiBase;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.builtin.modules.voice.fastmodvoice.TabModuleFaceOfVoice;
import egps2.frame.HintManager;
import egps2.frame.MyFrame;
import egps2.modulei.IconBean;
import egps2.modulei.ModuleClassification;
import egps2.modulei.ModuleVersion;
import utils.EGPSFormatUtil;
import utils.datetime.DateTimeOperator;
import utils.string.EGPSStringUtil;

import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Main class for the handy tools demonstration module.
 * This module provides biologist-friendly utilities including linear regression analysis.
 * It demonstrates how to create a VOICE module with examples and GUI integration.
 */
public class HandyToolExampleMain extends TabModuleFaceOfVoice {

    private int exampleIndex = 0;
    private final String EXAMPLE_PATH = EGPSProperties.PROPERTIES_DIR + "/bioData/example/regression/regression.data.tsv";

    /**
     * Get the number of examples provided by this module
     * @return The number of examples (4)
     */
    @Override
    protected int getNumberOfExamples() {
        return 4;
    }

    /**
     * Get the example text for the current example index
     * @return The example text as a string
     */
    @Override
    protected String getExampleText() {
        String str = null;
        String firstExample = """
                ### For module: Handy tool for biologist # See the right label for help.
                # The tsv file include header line.
                $input.tsv.path=%s
                
                # The x variable values
                $x.variable=X
                
                # The y variable values
                $y.variable=Y
                
                """.formatted(EXAMPLE_PATH);
        if (exampleIndex == 0) {
            str = firstExample;
        } else if (exampleIndex == 1) {
            str = firstExample + """
                    # x values to predict, split with ','. Leave blank if none or delete
                    $values.to.predict=1,2,3
                    """;
        } else if (exampleIndex == 2) {
            str = firstExample + """
                    # Optional file, default is to the console
                    $output.tsv.path=
                    
                    # x values to predict, split with ','. Leave blank if none or delete
                    $values.to.predict=1,2,3
                    """;
        } else if (exampleIndex == 3) {
            String outputPath = EGPSProperties.PROPERTIES_DIR + "/bioData/example/regression/regression.results.tsv";
            str = firstExample + """
                    # Optional file, default is to the console
                    $output.tsv.path=%s
                    
                    # x values to predict, split with ','. Leave blank if none or delete
                    $values.to.predict=1,2,3
                    """.formatted(outputPath);
            return str;
        }
        exampleIndex++;

        if (exampleIndex > 3) {
            exampleIndex = 0;
        }
        return str;
    }

    /**
     * Define parameters for the handy tool module
     * @param mapProducer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE mapProducer) {
        mapProducer.addKeyValueEntryBean("input.tsv.path", EXAMPLE_PATH, "The tsv file include header line.");
        mapProducer.addKeyValueEntryBean("x.variable", "X", "The x variable values");
        mapProducer.addKeyValueEntryBean("y.variable", "Y", "The y variable values");
        mapProducer.addKeyValueEntryBean("output.tsv.path", "", "Optional file, default is to the console");
        mapProducer.addKeyValueEntryBean("values.to.predict", "1,2,3", "x values to predict, split with ','. Leave blank if none or delete");

    }

    /**
     * Execute the handy tool functionality
     * Performs linear regression analysis on the provided data
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If there is an error during execution
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String inputFile = o.getSimplifiedString("input.tsv.path");
        String xVariable = o.getSimplifiedString("x.variable");
        String yVariable = o.getSimplifiedString("y.variable");

        String outputPath = o.getSimplifiedStringWithDefault("output.tsv.path");

        // Check if example data file exists before processing
        Path inputPath = Path.of(inputFile);
        if (!Files.exists(inputPath)) {
            String errorMessage = String.format(
                "Example data file not found: %s\n\n" +
                "Please ensure the example data directory exists:\n%s\n\n" +
                "Note: On first launch, eGPS creates the configuration directory automatically,\n" +
                "but example data files need to be prepared manually.\n\n" +
                "You can create a simple TSV file with 'X' and 'Y' columns for testing,\n" +
                "or modify the input path to point to your own data file.",
                inputFile,
                EGPSProperties.PROPERTIES_DIR + "/bioData/example/regression/"
            );

            // Display error in console
            setText4Console(Arrays.asList(errorMessage.split("\n")));

            // Show user-friendly dialog
            SwingUtilities.invokeLater(() -> {
                egps2.panels.dialog.SwingDialog.showWarningMSGDialog(
                    "Example Data Not Found",
                    errorMessage
                );
            });

            return; // Exit early without processing
        }

        // Read data from TSV file
        Map<String, double[]> data = LinearRegressionTool.readTSV(inputFile, xVariable, yVariable);

        // Create and fit the linear regression model
        LinearRegressionTool lr = new LinearRegressionTool();
        lr.fit(data.get("x"), data.get("y"));

        // Collect results
        List<String> results = new ArrayList<>();
        String currentTime = new DateTimeOperator().getCurrentTime();
        results.add("Running on the time:    " + currentTime);
        results.add("Slope:    " + lr.getSlope());
        results.add("Intercept:    " + lr.getIntercept());
        results.add("R²:    " + lr.getR2());
        results.add("Correlation:    " + lr.getCorrelation());
        results.add("P Value:    " + lr.getPValue());
        results.add("Slope Standard Error:  " + lr.getSlopeStdErr());
        results.add("Intercept Standard Error:  " + lr.getInterceptStdErr());

        // Calculate and add confidence intervals
        double[] slopeCI = lr.getSlopeConfidenceInterval();
        double[] interceptCI = lr.getInterceptConfidenceInterval();

        results.add(String.format("Slope Confidence Interval (95%%): [%.4f, %.4f]    ", slopeCI[0], slopeCI[1]));
        results.add(String.format("Intercept Confidence Interval (95%%): [%.4f, %.4f]    ", interceptCI[0], interceptCI[1]));

        // Perform predictions if requested
        String valuesToPredict = o.getSimplifiedStringWithDefault("values.to.predict");
        if (!valuesToPredict.isBlank()) {
            String[] split = EGPSStringUtil.split(valuesToPredict, ',');
            double[] xToPredict = new double[split.length];
            for (int i = 0; i < split.length; i++) {
                xToPredict[i] = Double.parseDouble(split[i].trim());
            }
            double[] predicted = lr.predict(xToPredict);
            results.add("Predicted Values:    " + Arrays.toString(predicted));
        }
        results.add("-------------------------------------------------------------");
        results.add("If you prefer to use R:   ");
        results.add("your_data_path <- \"a/b/c\"");
        results.add("data <- read.table(path, header = TRUE, sep = \"\\t\")");
        results.add("model <- lm(Y ~ X, data = data)");
        results.add("summary(model)");

        // Write results to file or console
        if (!outputPath.isBlank()) {
            Files.write(Path.of(outputPath), results);
        }
        setText4Console(results);
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "VOICE demo: Handy tools could also named useful utilities; practical tools; small helper tools / helper apps;lightweight tools INTENDS for biologist";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "Handy tool for biologist";
    }

    /**
     * Get the icon for this module
     * @return IconBean containing the module icon
     */
    @Override
    public IconBean getIcon() {
        InputStream resourceAsStream = getClass().getResourceAsStream("/images/maincore/Handy tool for biologist.svg");
        IconBean iconBean = new IconBean();
        iconBean.setSVG(true);
        iconBean.setInputStream(resourceAsStream);

        return iconBean;
    }

    /**
     * Get the category classification for this module
     * @return Array of category indices
     */
    @Override
    public int[] getCategory() {
        int[] ret = ModuleClassification.getOneModuleClassification(
                ModuleClassification.BYFUNCTIONALITY_SIMPLE_TOOLS_INDEX,
                ModuleClassification.BYAPPLICATION_GENOMICS_INDEX,
                ModuleClassification.BYCOMPLEXITY_LEVEL_1_INDEX,
                ModuleClassification.BYDEPENDENCY_ONLY_EMPLOY_CONTAINER
        );
        return ret;
    }

    /**
     * Initialize the graphical user interface for this module
     */
    @Override
    public void initializeGraphics() {
        super.initializeGraphics();

        MyFrame instanceFrame = UnifiedAccessPoint.getInstanceFrame();

        List<VersatileOpenInputClickAbstractGuiBase.Widget> importantWidgets = this.voiceTools.getImportantWidgets();

        String htmlStr = EGPSFormatUtil.html32Concat(5, UnifiedAccessPoint.getResourceString("voice.demo.comp.console"));
        VersatileOpenInputClickAbstractGuiBase.Widget widget = new VersatileOpenInputClickAbstractGuiBase.Widget(jTextArea4buttomConsole, htmlStr, SwingConstants.TOP);
        importantWidgets.add(widget);

        importantWidgets.reversed().forEach(triple -> {
            HintManager.Hint firstHint = new HintManager.Hint(
                    triple.name(),
                    triple.component(),
                    triple.width());
            instanceFrame.appendOneHint(firstHint);
        });

        boolean debug = false;

        if ( debug){
            return;
        }

        SwingUtilities.invokeLater(() -> {
            instanceFrame.showHints();
        });
    }

    /**
     * Get the version of this module
     * @return ModuleVersion object representing this module's version
     */
    @Override
    public ModuleVersion getVersion() {
        return EGPSProperties.MAINFRAME_CORE_VERSION;
    }
}
package demo.dockable;

import demo.dockable.richment.EnrichmentAnalysis;
import egps2.builtin.modules.voice.bean.AbstractParamsAssignerAndParser4VOICE;
import egps2.builtin.modules.voice.fastmodvoice.DockableTabModuleFaceOfVoice;
import egps2.builtin.modules.voice.fastmodvoice.OrganizedParameterGetter;
import egps2.frame.ComputationalModuleFace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A dockable tab module for performing biological pathway enrichment analysis.
 * This module allows users to perform pathway enrichment analysis on gene lists
 * using methods such as Over-Representation Analysis (ORA) with Fisher's exact test
 * and Gene Set Enrichment Analysis (GSEA).
 * 
 * The module takes a GMT file containing gene sets and a TSV file with significant genes
 * as input, then calculates enrichment results for biological pathways such as 
 * metabolic pathways, regulatory pathways, and signaling transduction pathways.
 */
public class BiologicalPathwayEnrichment extends DockableTabModuleFaceOfVoice {

    private static final Logger log = LoggerFactory.getLogger(BiologicalPathwayEnrichment.class);

    /**
     * Constructor for the BiologicalPathwayEnrichment module
     * @param cmf The computational module face
     */
    public BiologicalPathwayEnrichment(ComputationalModuleFace cmf) {
        super(cmf);
    }

    /**
     * Define parameters for the pathway enrichment analysis
     * @param designer The parameter designer used to define input parameters
     */
    @Override
    protected void setParameter(AbstractParamsAssignerAndParser4VOICE designer) {
        designer.addKeyValueEntryBean("input.geneset.gmt", "", "Input gmt format genesets from GSEAdb");
        designer.addKeyValueEntryBean("input.significant.result.tsv", "", "Input tsv data, must have header line");
        designer.addKeyValueEntryBean("symbol.col.name", "gene", "Gene symbol name");
        designer.addKeyValueEntryBean("significant.col.name", "pValue", "Column name with p values");
        designer.addKeyValueEntryBean("output.file.path", "", "Output file path, eg. out.result.tsv");
        designer.addKeyValueEntryBean("^", "", "");
        designer.addKeyValueEntryBean("p.value.cutoff", "0.05", "");
    }

    /**
     * Execute the pathway enrichment analysis process
     * @param o The organized parameter getter containing user inputs
     * @throws Exception If the analysis fails
     */
    @Override
    protected void execute(OrganizedParameterGetter o) throws Exception {
        String outputFileName = o.getSimplifiedString("output.file.path");
        String inputGMTFileName = o.getSimplifiedString("input.geneset.gmt");
        String inputSignificantResultFileName = o.getSimplifiedString("input.significant.result.tsv");
        String geneSymbolColName = o.getSimplifiedString("symbol.col.name");
        String significantColName = o.getSimplifiedString("significant.col.name");
        double pValueCutoff = o.getSimplifiedDouble("p.value.cutoff");
        
        // Perform pathway enrichment analysis using the provided inputs
        EnrichmentAnalysis.performEnrichmentAnalysis(inputGMTFileName, inputSignificantResultFileName,
                geneSymbolColName, significantColName, outputFileName, pValueCutoff);
        appendText2Console("Finished Computation: " + outputFileName);
    }

    /**
     * Get a short description of the module's functionality
     * @return Description string
     */
    @Override
    public String getShortDescription() {
        return "Statistical enrichment for the biological pathway, like metabolism pathway, regulatory pathway and signaling transduction pathway";
    }

    /**
     * Get the tab name for this module
     * @return Tab name string
     */
    @Override
    public String getTabName() {
        return "5. Biological pathway enrichment";
    }
}

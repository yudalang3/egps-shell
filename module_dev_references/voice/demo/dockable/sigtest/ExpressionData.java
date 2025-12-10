package demo.dockable.sigtest;

/**
 * Expression Data Class
 * Container class for storing expression data including the expression matrix,
 * gene names, and sample names.
 */
public class ExpressionData {
    public final double[][] expressionMatrix;
    public final String[] geneNames;
    public final String[] sampleNames;

    /**
     * Constructor for ExpressionData
     * @param expressionMatrix Expression matrix where rows are genes and columns are samples
     * @param geneNames Array of gene names
     * @param sampleNames Array of sample names
     */
    public ExpressionData(double[][] expressionMatrix, String[] geneNames, String[] sampleNames) {
        this.expressionMatrix = expressionMatrix;
        this.geneNames = geneNames;
        this.sampleNames = sampleNames;
    }
}
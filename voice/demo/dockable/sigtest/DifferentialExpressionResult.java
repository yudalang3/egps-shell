package demo.dockable.sigtest;

/**
 * Differential Expression Analysis Result Class
 * Represents the results of a differential expression analysis for a single gene
 */
public class DifferentialExpressionResult {
    private String geneName;
    private double logFC;           // log fold change
    private double pValue;          // Raw p-value
    private double adjustedPValue;  // Adjusted p-value
    private double averageExpression; // Average expression level
    private boolean significant;    // Whether the result is significant
    
    /**
     * Constructor for DifferentialExpressionResult
     * @param geneName Gene name
     * @param logFC Log fold change
     * @param pValue Raw p-value
     * @param adjustedPValue Adjusted p-value
     * @param averageExpression Average expression level
     */
    public DifferentialExpressionResult(String geneName, double logFC, double pValue, 
                                      double adjustedPValue, double averageExpression) {
        this.geneName = geneName;
        this.logFC = logFC;
        this.pValue = pValue;
        this.adjustedPValue = adjustedPValue;
        this.averageExpression = averageExpression;
        this.significant = adjustedPValue < 0.05;
    }
    
    // Getters
    /**
     * Get gene name
     * @return Gene name
     */
    public String getGeneName() { return geneName; }
    
    /**
     * Get log fold change
     * @return Log fold change
     */
    public double getLogFC() { return logFC; }
    
    /**
     * Get raw p-value
     * @return Raw p-value
     */
    public double getPValue() { return pValue; }
    
    /**
     * Get adjusted p-value
     * @return Adjusted p-value
     */
    public double getAdjustedPValue() { return adjustedPValue; }
    
    /**
     * Get average expression level
     * @return Average expression level
     */
    public double getAverageExpression() { return averageExpression; }
    
    /**
     * Check if the result is significant
     * @return Whether the result is significant
     */
    public boolean isSignificant() { return significant; }
    
    // Setters
    /**
     * Set significance status
     * @param significant Whether the result is significant
     */
    public void setSignificant(boolean significant) { this.significant = significant; }
    
    @Override
    public String toString() {
        return String.format("%s\t%.4f\t%.6f\t%.6f\t%.4f\t%s",
            geneName, logFC, pValue, adjustedPValue, averageExpression,
            significant ? "YES" : "NO");
    }
}
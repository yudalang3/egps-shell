package demo.floating.work;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Multiple Sequence Alignment visualization panel.
 * Displays DNA/protein sequence alignments with color coding for identical columns
 * and conservation indicators.
 */
public class MultipleSequenceAlignmentPanel extends JPanel {
    private String[] sequences;
    private String[] labels;
    private JPanel sequenceDisplayPanel;
    private JScrollPane scrollPane;
    
    // Nucleotide color mapping
    private static final Map<Character, Color> NUCLEOTIDE_COLORS = new HashMap<>();
    static {
        NUCLEOTIDE_COLORS.put('A', new Color(255, 102, 102)); // Red
        NUCLEOTIDE_COLORS.put('T', new Color(102, 255, 102)); // Green
        NUCLEOTIDE_COLORS.put('C', new Color(102, 102, 255)); // Blue
        NUCLEOTIDE_COLORS.put('G', new Color(255, 255, 102)); // Yellow
        NUCLEOTIDE_COLORS.put('-', Color.LIGHT_GRAY);         // Gap
    }
    
    /**
     * Constructor for MultipleSequenceAlignmentPanel
     * Initializes the UI components
     */
    public MultipleSequenceAlignmentPanel() {
        initializeUI();
    }
    
    /**
     * Initialize the user interface components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(400, 300));
        
        // Create sequence display panel
        sequenceDisplayPanel = new JPanel();
        sequenceDisplayPanel.setLayout(new BoxLayout(sequenceDisplayPanel, BoxLayout.Y_AXIS));
        sequenceDisplayPanel.setBackground(Color.WHITE);
        
        // Create scroll panel
        scrollPane = new JScrollPane(sequenceDisplayPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Set sequences to display
     * @param sequences sequence array
     * @param labels sequence labels
     */
    public void setSequences(String[] sequences, String[] labels) {
        this.sequences = sequences;
        this.labels = labels;
        displaySequences();
    }
    
    /**
     * Display sequence alignment results
     */
    private void displaySequences() {
        sequenceDisplayPanel.removeAll();
        
        if (sequences == null || sequences.length == 0) {
            JLabel noDataLabel = new JLabel("No sequence data available");
            noDataLabel.setFont(MyFontConfig.getDefaultFont());
            noDataLabel.setHorizontalAlignment(SwingConstants.CENTER);
            sequenceDisplayPanel.add(noDataLabel);
            return;
        }
        
        int maxLength = getMaxSequenceLength();
        boolean[] conservedPositions = findConservedPositions(maxLength);
        
        // Add sequence names and sequence display
        for (int i = 0; i < sequences.length; i++) {
            String label = (labels != null && i < labels.length) ? labels[i] : "Seq" + (i + 1);
            JPanel sequencePanel = createSequencePanel(label, sequences[i], conservedPositions);
            sequenceDisplayPanel.add(sequencePanel);
            sequenceDisplayPanel.add(Box.createVerticalStrut(2));
        }
        
        // Add conservation indicator
        JPanel conservationPanel = createConservationPanel(conservedPositions, maxLength);
        sequenceDisplayPanel.add(conservationPanel);
        
        // Add legend panel
        JPanel legendPanel = createLegendPanel();
        sequenceDisplayPanel.add(Box.createVerticalStrut(10));
        sequenceDisplayPanel.add(legendPanel);
        
        sequenceDisplayPanel.revalidate();
        sequenceDisplayPanel.repaint();
    }
    
    /**
     * Create display panel for a single sequence
     * @param seqName Sequence name
     * @param sequence Sequence data
     * @param conservedPositions Array indicating conserved positions
     * @return JPanel containing the formatted sequence display
     */
    private JPanel createSequencePanel(String seqName, String sequence, boolean[] conservedPositions) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        
        // Sequence name label
        JLabel nameLabel = new JLabel(seqName + ": ");
        nameLabel.setPreferredSize(new Dimension(60, 20));
        nameLabel.setFont(MyFontConfig.getTitleFont());
        panel.add(nameLabel);
        
        // Sequence characters
        for (int i = 0; i < sequence.length(); i++) {
            char nucleotide = sequence.charAt(i);
            JLabel charLabel = new JLabel(String.valueOf(nucleotide));
            charLabel.setPreferredSize(new Dimension(15, 20));
            charLabel.setHorizontalAlignment(SwingConstants.CENTER);
            charLabel.setFont(MyFontConfig.getDefaultFont());
            charLabel.setOpaque(true);
            
            // Set background color
            if (conservedPositions[i]) {
                // Conserved positions use nucleotide-specific colors
                Color bgColor = NUCLEOTIDE_COLORS.getOrDefault(nucleotide, Color.WHITE);
                charLabel.setBackground(bgColor);
                charLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            } else {
                // Non-conserved positions use light colors
                charLabel.setBackground(Color.WHITE);
                charLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            }
            
            panel.add(charLabel);
        }
        
        return panel;
    }
    
    /**
     * Create conservation indicator panel
     * @param conservedPositions Array indicating conserved positions
     * @param length Length of the sequences
     * @return JPanel containing conservation indicators
     */
    private JPanel createConservationPanel(boolean[] conservedPositions, int length) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        
        // Spacer label for alignment
        JLabel spacer = new JLabel("");
        spacer.setPreferredSize(new Dimension(60, 20));
        panel.add(spacer);
        
        // Conservation indicators
        for (int i = 0; i < length; i++) {
            JLabel indicator = new JLabel(conservedPositions[i] ? "*" : " ");
            indicator.setPreferredSize(new Dimension(15, 20));
            indicator.setHorizontalAlignment(SwingConstants.CENTER);
            indicator.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            indicator.setForeground(Color.RED);
            panel.add(indicator);
        }
        
        return panel;
    }
    
    /**
     * Get the maximum sequence length
     * @return Maximum length among all sequences
     */
    private int getMaxSequenceLength() {
        int maxLength = 0;
        for (String seq : sequences) {
            maxLength = Math.max(maxLength, seq.length());
        }
        return maxLength;
    }
    
    /**
     * Find conserved positions (positions where all sequences have the same character)
     * @param maxLength Maximum sequence length
     * @return Boolean array indicating conserved positions
     */
    private boolean[] findConservedPositions(int maxLength) {
        boolean[] conserved = new boolean[maxLength];
        
        for (int pos = 0; pos < maxLength; pos++) {
            char firstChar = '\0';
            boolean isConserved = true;
            
            for (String seq : sequences) {
                if (pos < seq.length()) {
                    char currentChar = seq.charAt(pos);
                    if (firstChar == '\0') {
                        firstChar = currentChar;
                    } else if (firstChar != currentChar) {
                        isConserved = false;
                        break;
                    }
                } else {
                    isConserved = false;
                    break;
                }
            }
            
            conserved[pos] = isConserved;
        }
        
        return conserved;
    }
    
    /**
     * Create legend panel
     * @return JPanel containing the color legend
     */
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createTitledBorder("Legend"));
        
        // Create color legend for nucleotides
        for (Map.Entry<Character, Color> entry : NUCLEOTIDE_COLORS.entrySet()) {
            if (entry.getKey() != '-') { // Skip gap character
                JLabel colorLabel = new JLabel(String.valueOf(entry.getKey()));
                colorLabel.setPreferredSize(new Dimension(20, 20));
                colorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                colorLabel.setFont(MyFontConfig.getDefaultFont());
                colorLabel.setOpaque(true);
                colorLabel.setBackground(entry.getValue());
                colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                legendPanel.add(colorLabel);
            }
        }
        
        // Add gap legend
        JLabel gapLabel = new JLabel("-");
        gapLabel.setPreferredSize(new Dimension(20, 20));
        gapLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gapLabel.setFont(MyFontConfig.getDefaultFont());
        gapLabel.setOpaque(true);
        gapLabel.setBackground(Color.LIGHT_GRAY);
        gapLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        legendPanel.add(gapLabel);
        
        JLabel gapText = new JLabel("Gap");
        gapText.setFont(MyFontConfig.getDefaultFont());
        legendPanel.add(gapText);
        
        return legendPanel;
    }
}
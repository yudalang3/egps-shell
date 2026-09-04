package egps2.panels;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;  
  
/**
 * GUIPrintStream is a reusable Swing panel or dialog within eGPS.
 */
public class GUIPrintStream extends PrintStream{  
      
    private JTextComponent component;  
      
    public GUIPrintStream(OutputStream out, JTextComponent component){  
        super(out);  
        this.component = component;  
    }  
      
    /** *//** 
     * 重写write()方法，将输出信息填充到GUI组件。 
     * @param buf 
     * @param off 
     * @param len 
     */  
    @Override  
    public void write(byte[] buf, int off, int len) {  
        final String message = new String(buf, off, len);   
  
        SwingUtilities.invokeLater(new Runnable(){  
            public void run(){  
            	String text = component.getText();
            	String concat = text.concat(message);
                component.setText(concat);  
            }  
        });  
    }  
}

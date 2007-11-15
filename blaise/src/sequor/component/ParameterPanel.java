/*
 * ParameterPanel.java
 * Created on Nov 13, 2007, 8:56:27 AM
 */

package sequor.component;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import sequor.model.DoubleRangeModel;
import sequor.model.SpinnerDoubleEditor;

/**
 * Represents a panel with a collection of parameters. These may be displayed
 * as a collection of spinner boxes, or as a collection of sliders.
 * <br><br>
 * @author ae3263
 */
public class ParameterPanel extends JPanel {
    
    // COSNTRUCTORS
    
    public ParameterPanel(){
        super();
        setLayout(new FlowLayout());
    }
    
    
    // PARAMETER CHANGES
    
    /** Adds another parameter. */
    public void addParameter(String pm,DoubleRangeModel drm){
        add(new RowPanel(pm,drm));
    }
    
    
    // DISPLAY SETTINGS
    
    private int style=SPINNER_STYLE;
    public static final int SPINNER_STYLE=0;
    public static final int SLIDER_STYLE=1;            
    
    public void setStyle(int newStyle){
        if(newStyle<0||newStyle>1||newStyle==style){return;}
        style=newStyle;
    }
    
    
    // SUBPANELS
    
    class RowPanel extends JPanel{
        RowPanel(String pm,DoubleRangeModel drm){
            switch(style){
            case SPINNER_STYLE:
                add(new JLabel(pm+"="));
                add(new JSpinner(new SpinnerDoubleEditor(drm)));
                break;
            //case SLIDER_STYLE:
            //    add(new JLabel(pm+"="+drm.getValue()));
            //    add(new JSlider(new SpinnerDoubleEditor(drm)));
            //    break;
            }
        }
    }
}

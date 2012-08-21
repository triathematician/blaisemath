/*
 * PlotInputPanel.java
 * Created on Nov 12, 2007, 1:19:22 PM
 */

package specto;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import specto.euclidean2.Plot2D;

/**
 * This class is a wrapper for the typical PlotPanel contents. It uses a BorderLayout
 * to place four panels on the boundaries of the window... a toolbar a top, a status
 * bar at bottom, and input/settings panes on the right and left respectively.
 * <br><br>
 * The program here, as all my code, is dedicated to my Lord and friend Jesus Christ.
 * I believe not because of what He does for me but for what He does in me. For the
 * words that He speaks. For the grace He gives freely. For choosing me despite my
 * abundant failings.
 * <br><br>
 * @author ae3263
 */
public class PlotInputPanel extends JPanel {
    
    
    // COMPONENTS
    
    /** Contains the central panel... usually a plot. */
    PlotPanel contentPanel;
    /** Side panel for inputting new elements. */
    JPanel inputPanel;
    /** Status line at the bottom of the screen. */
    JPanel statusPanel;
    /** Panel for changing properties/settings for display. */
    JPanel propertyPanel;
    /** Toolbar at the top of the screen. */
    JToolBar toolBar;
    /** Context menu. */
    JMenu contextMenu;
    
    
    // CONSTRUCTORS
    
    /** Default constructor contains a default PlotPanel */
    public PlotInputPanel(){
        this(new Plot2D());
    }
    
    /** Construct with a particular PlotPanel */
    public PlotInputPanel(PlotPanel content){
        contentPanel=content;
        inputPanel=new JPanel();
        statusPanel=new JPanel();
        propertyPanel=new JPanel();
        toolBar=new JToolBar();
        
        setOpaque(false);
        setLayout(new BorderLayout());
        add(contentPanel);
        add(inputPanel,BorderLayout.EAST);
        add(statusPanel,BorderLayout.SOUTH);
        add(propertyPanel,BorderLayout.WEST);
        add(toolBar,BorderLayout.NORTH);
    }
    
    
    // BEAN PATTERNS
    
    boolean inputPanelVisible;
    boolean propertyPanelVisible;
    boolean toolBarVisible;
    boolean statusBarVisible;
    
    public void setInputPanelVisible(boolean newValue){inputPanelVisible=newValue;}
    public void setPropertyPanelVisible(boolean newValue){propertyPanelVisible=newValue;}
    public void setToolBarVisible(boolean newValue){toolBarVisible=newValue;}
    public void setStatusBarVisible(boolean newValue){statusBarVisible=newValue;}
}

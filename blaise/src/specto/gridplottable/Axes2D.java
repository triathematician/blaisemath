/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.gridplottable;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Vector;
import specto.DynamicPlottable;
import specto.style.LineStyle;
import specto.visometry.Euclidean2;

/**
 * <p>
 * NewGrid2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class Axes2D extends DynamicPlottable<Euclidean2> implements ActionListener {

    // CONSTANTS
    

    
    // CONSTRUCTOR
    public Axes2D(){}
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        NiceRangeGenerator spacing=NiceRangeGenerator.PI;
        Vector<Double> xGrid=spacing.niceRange(
                v.getActualMin().x,v.getActualMax().x,
                (double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        Vector<Double> yGrid=spacing.niceRange(
                v.getActualMin().y,v.getActualMax().y,
                (double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        
        g.setColor(getColor());
        g.setStroke(LineStyle.VERY_DOTTED_STROKE);
        g.draw(v.vLines(xGrid));
        g.draw(v.hLines(yGrid));
    }

    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // STYLE SETTINGS
    
    private String xLabel="x";
    private String yLabel="y";
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_GRID_SPACE=40;
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    public static final int AXES_CROSS=0;
    public static final int AXES_BOX=1;
    public static final int AXES_L=2;
    public static final int AXES_T=3;
    final static String[] styleStrings={"Cross-Style Axes","Box-Style Axes","L-Style Axes","Inverted T-Style Axes"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
}

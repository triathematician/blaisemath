/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.gridplottable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.VisualStyle;
import specto.DynamicPlottable;
import specto.style.LineStyle;
import specto.visometry.Euclidean2;

/**
 * <p>
 * NewGrid2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class NewGrid2D extends DynamicPlottable<Euclidean2> implements ActionListener {

    // CONSTANTS
    
    private static final int IDEAL_GRID_SPACE=40;

    
    // CONSTRUCTOR
    public NewGrid2D(){
        setColor(Color.getHSBColor(.4f,.5f,.7f));
    }
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        NiceRangeGenerator spacing=NiceRangeGenerator.PI;
        Vector<Double> xGrid=spacing.niceRange(
                v.getActualMin().x,v.getActualMax().x,
                (double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        Vector<Double> yGrid=spacing.niceRange(
                v.getActualMin().y,v.getActualMax().y,
                (double)IDEAL_GRID_SPACE*v.getDrawHeight()/v.getWindowHeight());        
        g.setColor(getColor());
        g.setStroke(VisualStyle.VERY_DOTTED_STROKE);
        g.draw(Ruler.getHorizontalLines(0,v.toWindowY(yGrid),v.getWindowWidth()));
        g.draw(Ruler.getVerticalLines(v.toWindowX(xGrid),0,v.getWindowHeight()));
    }

    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // STYLE SETTINGS

    @Override
    public String[] getStyleStrings() {return null;}
    @Override
    public String toString(){return "Grid";}

}

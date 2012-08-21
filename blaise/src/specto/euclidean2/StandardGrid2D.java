/*
 * StandardGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.euclidean2;

import specto.NiceRangeGenerator;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.VisualStyle;
import specto.DynamicPlottable;
import sequor.style.LineStyle;
import specto.euclidean2.Euclidean2;

/**
 * <p>
 * StandardGrid2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class StandardGrid2D extends DynamicPlottable<Euclidean2> implements ActionListener {

    // CONSTANTS
    
    private static final int IDEAL_GRID_SPACE=40;

    
    // CONSTRUCTOR
    public StandardGrid2D(){
        setColor(Color.getHSBColor(.4f,.3f,0.9f));
    }
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
        Vector<Double> xGrid=spacing.niceRange(
                v.getActualMin().x,v.getActualMax().x,
                (double)IDEAL_GRID_SPACE*v.getDrawWidth()/v.getWindowWidth());
        Vector<Double> yGrid=spacing.niceRange(
                v.getActualMin().y, v.getActualMax().y,
                (double)IDEAL_GRID_SPACE*v.getDrawHeight()/v.getWindowHeight());        
        g.setColor(getColor());
        g.setStroke(LineStyle.STROKES[LineStyle.VERY_THIN]);
        Line2D.Double[] lines=Ruler.getHorizontalLines(0,v.toWindowY(yGrid),v.getWindowWidth());
        for(int i=0;i<lines.length;i++){g.draw(lines[i]);}
        lines=Ruler.getVerticalLines(v.toWindowX(xGrid),0,v.getWindowHeight());
        for(int i=0;i<lines.length;i++){g.draw(lines[i]);}
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

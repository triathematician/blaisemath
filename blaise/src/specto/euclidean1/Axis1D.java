/**
 * Axis1D.java
 * Created on Jun 3, 2008
 */

package specto.euclidean1;

import specto.NiceRangeGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.LineStyle;
import specto.DynamicPlottable;
import specto.euclidean1.Euclidean1;

/**
 * Paints a simple axis on a plot window.
 * @author Elisha Peterson
 */
public class Axis1D extends DynamicPlottable<Euclidean1> implements ActionListener {
    
    // CONSTRUCTOR
    public Axis1D(){
        setColor(Color.GRAY);
    }    
    
    // PAINT METHODS
    
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_TICK_SPACE=40;    
    static Point2D XOFFSET=new Point2D.Double(5,-3);
    static Point2D YOFFSET=new Point2D.Double(10,-5);
    
    private String xLabel="x";
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean1 v) {
        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
        Vector<Double> xGrid=spacing.niceRange(
                v.getActualMin(),v.getActualMax(),
                (double)IDEAL_TICK_SPACE*v.getActualWidth()/v.getWindowWidth());            
        Vector<String> xLabels=new Vector<String>();
        for(Double d:xGrid){xLabels.add(nf.format(d));}
        xGrid=v.toWindow(xGrid);
        g.setColor(getColor());
        g.setStroke(LineStyle.STROKES[LineStyle.THICK]);
        
        Point2D.Double origin=v.toWindow(0.0);
        origin.x=Math.min(Math.max(origin.x,0),v.getWindowWidth());
        
        g.draw(new Line2D.Double(0,origin.y,v.getWindowWidth(),origin.y));
        Ruler.drawLabeledVerticalLines(g,xGrid,origin.y-TICK_HEIGHT,2*TICK_HEIGHT,xLabels,XOFFSET);
        Point2D.Float xLabelPos=new Point2D.Float(v.getWindowWidth()-10,(float)origin.y+12);
        g.drawString(xLabel,xLabelPos.x,xLabelPos.y);     
    }

    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // STYLE SETTINGS    
    
    @Override
    public String[] getStyleStrings() {return null;}
    
    @Override
    public String toString(){return "Axis";}
}

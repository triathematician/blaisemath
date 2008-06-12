/*
 * NewGrid2D.java
 * Created on Mar 22, 2008
 */

package specto.euclidean2;

import specto.NiceRangeGenerator;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.VisualStyle;
import specto.DynamicPlottable;
import specto.euclidean2.Euclidean2;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class Axes2D extends DynamicPlottable<Euclidean2> implements ActionListener {
    
    // CONSTRUCTOR
    public Axes2D(){
        setColor(Color.GRAY);
    }
    
    
    // PAINT METHODS
    
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_TICK_SPACE=40;    
    static Point2D XOFFSET=new Point2D.Double(5,-3);
    static Point2D YOFFSET=new Point2D.Double(10,-5);
    
    private String xLabel="x";
    private String yLabel="y";
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
        Vector<Double> xGrid=spacing.niceRange(
                v.getActualMin().x,v.getActualMax().x,
                (double)IDEAL_TICK_SPACE*v.getDrawWidth()/v.getWindowWidth());
        Vector<Double> yGrid=spacing.niceRange(
                v.getActualMin().y,v.getActualMax().y,
                (double)IDEAL_TICK_SPACE*v.getDrawHeight()/v.getWindowHeight());             
        Vector<String> xLabels=new Vector<String>();
        for(Double d:xGrid){xLabels.add(nf.format(d));}
        Vector<String> yLabels=new Vector<String>();
        for(Double d:yGrid){yLabels.add(nf.format(d));}
        xGrid=v.toWindowX(xGrid);
        yGrid=v.toWindowY(yGrid);
        g.setColor(getColor());
        g.setStroke(VisualStyle.MEDIUM_STROKE);  
        
        Point2D.Double origin=v.toWindow(0,0);
        origin.x=Math.min(Math.max(origin.x,0),v.getWindowWidth());
        origin.y=Math.min(Math.max(origin.y,0),v.getWindowHeight());
        
        switch(style.getValue()){
            case AXES_CROSS:
                g.draw(new Line2D.Double(0,origin.y,v.getWindowWidth(),origin.y));
                g.draw(new Line2D.Double(origin.x,0,origin.x,v.getWindowHeight()));
                Ruler.drawLabeledHorizontalLines(g,origin.x-TICK_HEIGHT,yGrid,2*TICK_HEIGHT,yLabels,YOFFSET);
                Ruler.drawLabeledVerticalLines(g,xGrid,origin.y-TICK_HEIGHT,2*TICK_HEIGHT,xLabels,XOFFSET);
                break;
            case AXES_L:
                g.draw(new Line2D.Double(origin.x,origin.y,v.getWindowWidth(),origin.y));
                g.draw(new Line2D.Double(origin.x,origin.y,origin.x,0));
                Ruler.drawLabeledVerticalLines(g,xGrid,origin.y,TICK_HEIGHT,xLabels,XOFFSET);
                Ruler.drawLabeledHorizontalLines(g,origin.x,yGrid,TICK_HEIGHT,yLabels,YOFFSET);
                break;
            case AXES_T:
                g.draw(new Line2D.Double(0,origin.y,v.getWindowWidth(),origin.y));
                g.draw(new Line2D.Double(origin.x,origin.y,origin.x,0));
                Ruler.drawLabeledVerticalLines(g,xGrid,origin.y,TICK_HEIGHT,xLabels,XOFFSET);
                Ruler.drawLabeledHorizontalLines(g,origin.x-TICK_HEIGHT,yGrid,2*TICK_HEIGHT,yLabels,YOFFSET);
                break;
            case AXES_BOX:
                g.draw(new Rectangle2D.Double(0,0,v.getWindowWidth(),v.getWindowHeight()));
                Ruler.drawLabeledVerticalLines(g,xGrid,0,TICK_HEIGHT,xLabels,XOFFSET);
                Ruler.drawLabeledVerticalLines(g,xGrid,v.getWindowHeight(),-TICK_HEIGHT,xLabels,XOFFSET);
                Ruler.drawLabeledHorizontalLines(g,0,yGrid,TICK_HEIGHT,yLabels,YOFFSET);
                Ruler.drawLabeledHorizontalLines(g,v.getWindowWidth(),yGrid,-TICK_HEIGHT,yLabels,YOFFSET);
                break;
        }
        
        Point2D.Float xLabelPos=new Point2D.Float(v.getWindowWidth()-10,(float)origin.y+12);
        Point2D.Float yLabelPos=new Point2D.Float((float)origin.x-12,15);
        g.drawString(xLabel,xLabelPos.x,xLabelPos.y);
        g.drawString(yLabel,yLabelPos.x,yLabelPos.y);        
    }

    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // STYLE SETTINGS    
    
    public static final int AXES_CROSS=0;
    public static final int AXES_BOX=1;
    public static final int AXES_L=2;
    public static final int AXES_T=3;
    final static String[] styleStrings={"Cross-Style Axes","Box-Style Axes","L-Style Axes","Inverted T-Style Axes"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    @Override
    public String toString(){return "Axes";}
}

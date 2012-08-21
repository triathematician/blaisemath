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
import java.text.DecimalFormat;
import java.util.Vector;
import sequor.control.Ruler;
import sequor.style.LineStyle;
import specto.DynamicPlottable;
import specto.euclidean2.Euclidean2;

/**
 * Axes2D represents the second generation of axes drawing, with support for multiple drawing modes, labels, etc. The Grid2D class separately
 * maintains the grid. The spacing of the two classes should be adjusted separately.
 * 
 * @author Elisha Peterson
 */
public class PolarAxes2D extends DynamicPlottable<Euclidean2> implements ActionListener {
    
    // CONSTRUCTOR
    public PolarAxes2D(){
        setColor(new Color(128,128,128,128));
    }
    
    
    // PAINT METHODS
    
    private static final int TICK_HEIGHT=5;
    private static final int IDEAL_TICK_SPACE=40;    
    static Point2D XOFFSET=new Point2D.Double(5,-3);
    static Point2D YOFFSET=new Point2D.Double(10,-5);
    
    private String rLabel="r";
   // private String yLabel="y";
    private static final DecimalFormat nf=new DecimalFormat("##0.#");   
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        double maxRad=PolarGrid2D.maxRadius(v);
        double minRad=maxRad/40;

        Point2D.Double origin=v.toWindow(0,0);
        origin.x=Math.min(Math.max(origin.x,0),v.getWindowWidth());
        origin.y=Math.min(Math.max(origin.y,0),v.getWindowHeight());

        NiceRangeGenerator spacing=new NiceRangeGenerator.StandardRange();
        Vector<Double> rGrid=spacing.niceRange(minRad, maxRad,
                (double)IDEAL_TICK_SPACE*v.getDrawWidth()/v.getWindowWidth());
        g.setColor(getColor());
        g.setStroke(LineStyle.STROKES[LineStyle.THICK]);

        Vector<String> rLabels=new Vector<String>();
        for(Double d:rGrid){rLabels.add(nf.format(d));}
        rGrid=v.toWindowX(rGrid);         
        
        switch(style.getValue()){
            case AXES_STD:
                g.draw(new Line2D.Double(origin.x,origin.y,v.getWindowWidth(),origin.y));
                Ruler.drawLabeledVerticalLines(g,rGrid,origin.y-TICK_HEIGHT,2*TICK_HEIGHT,rLabels,XOFFSET);
                break;
        }
        
        Point2D.Float xLabelPos=new Point2D.Float(v.getWindowWidth()-10,(float)origin.y+12);
        g.drawString(rLabel,xLabelPos.x,xLabelPos.y);
    }

    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    // STYLE SETTINGS    
    
    public static final int AXES_STD=0;
    final static String[] styleStrings={"Standard Axes"};
    @Override
    public String[] getStyleStrings() {return styleStrings;}
    
    @Override
    public String toString(){return "PolarAxes";}
    public String getRLabel() { return rLabel; }
    public void setRLabel(String rLabel) { this.rLabel = rLabel; }
  //  public String getYLabel() { return yLabel; }
  //  public void setYLabel(String yLabel) { this.yLabel = yLabel; }
}

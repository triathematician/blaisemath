package Planar;

import Interface.BPlottable;
import PlanarAlgebra.FiniteGridGeometry;
import PlanarAlgebra.TLElement;
import PlanarAlgebra.TLGroup;
import PlanarAlgebra.TLModel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.math.BigInteger;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

/**
 * <b>VisualTL.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 9, 2007, 10:12 AM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class VisualTL extends JComponent implements BPlottable{
    // basic parameters
    private Point click1,click2;
    private FiniteGridGeometry geo;
    private TLModel t;
    private TLElement tOld;
    private int tOldLength;
    
    // visual style parameters
    private final Color[] cycleColors={
        Color.DARK_GRAY,
        Color.RED,
        Color.BLUE,
        Color.GREEN,
        Color.ORANGE,
        Color.PINK,
        Color.CYAN,
        Color.MAGENTA,
        Color.GRAY};
    private boolean colorCycles=true;
    private boolean directed=false;
    private boolean thick=false;
    
    /** Constructor: creates a new instance of VisualTL */
    public VisualTL(){super();initializeModels();}
    public VisualTL(JPanel panel){super();initializeModels(panel);}
    
    public void initializeModels(JPanel panel){t=new TLModel();geo=new FiniteGridGeometry(panel,1,1);}
    public void initializeModels(){t=new TLModel();geo=new FiniteGridGeometry(new JPanel(),1,1);}
    
    public void setX(int wx){}
    public void setY(int wy){}
    
    public void setGeometry(FiniteGridGeometry geo){this.geo=geo;}
    public void setModel(TLModel t){this.t=t;}
    public TLModel getModel(){return t;}
    
    public void stateChanged(ChangeEvent e){}
    public void paintComponent(Graphics grb){
        Graphics2D gr=(Graphics2D)grb;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        if(!colorCycles){
            gr.setColor(cycleColors[1]);
            Point[][] tt=t.getValue().getPairs();
            for(int i=0;i<t.getLength();i++){gr.draw(getArc(tt[i]));}
        } else {
            Vector<Vector<Point[]>> ttt=t.getValue().getCyclePairs();
            for(int i=0;i<ttt.size();i++){
                gr.setColor(getCycleColor(i));
                for(Point[] t:ttt.get(i)){gr.draw(getArc(t));}
            }
        }
    }
    
    private Color getCycleColor(int i){
        return cycleColors[i%(cycleColors.length)];
    }
    
    /**
     * Draws a Temperley-Lieb arc between two points. Really draws a curve
     *   through the two points as well as a third point midway between them
     *   (in x values) and at 1/3,1/2, or 2/3 y distance depending on whether
     *   the points are both on bottom, both in the middle, or both on top,
     *   respectively.
     */
    private Path2D.Double getArc(Point[] point){
        if(point.length!=2){return null;}
        Point2D.Double p1=geo.toWindow(point[0]); // startpoint
        Point2D.Double bc1=geo.toWindow(point[0].x,.5); // first Bezier control point
        Point2D.Double bc2=geo.toWindow(point[1].x,.5); // second Bezier control point
        Point2D.Double p2=geo.toWindow(point[1]); // endpoint
        Path2D.Double path=new Path2D.Double();
        path.moveTo(p1.x,p1.y);
        path.curveTo(bc1.x,bc1.y,bc2.x,bc2.y,p2.x,p2.y);
        return path;
    }
    
    // mouse events
    public boolean clicked(MouseEvent e){return false;}
    public void mouseClicked(MouseEvent e){
        click1=geo.closest(e);
        int n=tOld.getN();
        if(click1!=null){
            if(click1.x==n-1){click1.x=n-2;}
            TLElement tNew=TLGroup.getBasic(click1.x,n);
            if(click1.y==0){tNew.composeOnRightWith(t.getValue());}
            else{tNew.composeOnLeftWith(t.getValue());}
            t.setValue(tNew);
        }
    }
    public void mousePressed(MouseEvent e){
        click1=geo.closest(e);
        if(click1!=null){tOld=t.getValue();tOldLength=tOld.getN();}
    }
    public void mouseReleased(MouseEvent e){click1=null;click2=null;}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mouseDragged(MouseEvent e){
        if(click1!=null){
            click2=geo.closest(e);
            if(click2!=null&&!click2.equals(click1)){                
                if((click1.y==0)&&(click2.y==0)){ // both bottom clicks... compose with swap on the left!
                    TLElement tNew=TLGroup.getSwap(click1.x,click2.x,tOldLength);
                    tNew.composeOnRightWith(tOld);
                    t.setValue(tNew);
                } else if((click1.y==1)&&(click2.y==1)){ // both top clicks... compose with swap on the right!
                    TLElement tNew=TLGroup.getSwap(click1.x,click2.x,tOldLength);
                    tNew.composeOnLeftWith(tOld);
                    t.setValue(tNew);
                }
            }
        }
    }
    public void mouseMoved(MouseEvent e){}

    public void setX(double wx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setY(double wy) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void paintComponent(Graphics2D g, AffineTransform at) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getGeoX() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getGeoY() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

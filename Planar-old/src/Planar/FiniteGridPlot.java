package Planar;

import Interface.BPlotPanel;
import Interface.BPlottable;
import PlanarAlgebra.FiniteGridGeometry;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;

/**
 * <b>FiniteGridPlot.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 9, 2007, 11:20 AM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class FiniteGridPlot extends JPanel implements BPlotPanel {
    private FiniteGridGeometry geo;
    
    public FiniteGridGeometry getGeometry(){return geo;}
    public void setGeometry(FiniteGridGeometry geo){this.geo=geo;}
    
    /** Constructor: creates a new instance of FiniteGridPlot */
    public FiniteGridPlot(){
        geo=new FiniteGridGeometry(this,1,1);
        setBackground(Color.WHITE);
        setOpaque(true);
    }
    
    protected void paintComponent(Graphics gb){
        super.paintComponent(gb);
        if(isOpaque()){gb.setColor(getBackground());gb.fillRect(0,0,getWidth(),getHeight());}
        Graphics2D g=(Graphics2D)gb;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        g.setColor(Color.GRAY);
        Point pt=geo.getGeoMax();
        for(int i=0;i<=pt.x;i++){
            for(int j=0;j<=pt.y;j++){
                Point2D.Double p=geo.toWindow(i,j);
                g.fill(new Ellipse2D.Double(p.x-2,p.y-2,4,4));
            }
        }
    }
    
    public void actionPerformed(ActionEvent e){}
    public void stateChanged(ChangeEvent e){repaint();}
    
    /**
     * Appends the specified component to the end of this container.
     * This is a convenience method for {@link #addImpl}.
     */
    public Component add(Component comp){
        Component x=super.add(comp);
        if(comp instanceof BPlottable){
            addMouseListener((BPlottable)comp);
            addMouseMotionListener((BPlottable)comp);
        }
        return x;
    }

    public void mouseClicked(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseReleased(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseEntered(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseExited(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseDragged(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseMoved(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}

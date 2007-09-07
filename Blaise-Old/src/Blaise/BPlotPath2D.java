package Blaise;

import Euclidean.PPath;
import Interface.BPlottable;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <b>BPlotPath2D.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 3, 2007, 1:39 PM</i><br><br>
 *
 * Stores a path with support for painting on a Graphics object.
 *
 * The underlying model stores Cartesian coordinates, whereas this class must convert
 *   to integer coordinates. For these purposes, two FollowerRangeModels are
 *   implemented, which store the integer values in terms of the GUI window. That's
 *   where all the translation occurs.
 *
 * Will be updating this to allow for animation of part of the path based on a "t" parameter control...
 *   initially I will just have an animation display a specified subset of the path rather than the entire
 *   thing. Future plans will be to allow for several different output settings.<br><br>
 *
 * <b>DESIRED FEATURES LIST:</b><br>
 *    1. Export options to BPlotPathStyle
 *    2. Expand number of display options
 *    3. Write display options panel
 *    4. Write "clicked" method
 *    5. Right click menu
 *    6. Cycle styles when clicked?
 *    7. Method to generate corresponding list of points
 *    8. Quicken animation & rethink it... include several options
 *    9. Option to create a "ghost" point along the path
 */
public class BPlotPath2D extends PPath implements BPlottable {
    
    /** Color of the path for drawing. */
    private Color color;
    /** Stroke setting. */
    private Stroke stroke;
    
    /** Subset of the path to be drawn, if animation setting is on. */
    private boolean animate;
    private int start;
    private int end;
    
    // Default constructor
    public BPlotPath2D() {
        super();
        color=Color.BLACK;
        stroke=new BasicStroke(1.5f);
        animate=false;
        start=0;
        end=0;
    }
    // Constructs given a ppath and color
    public BPlotPath2D(PPath path,Color color){
        addAll(path);
        this.color=color;
        stroke=new BasicStroke(1.5f);
        animate=false;
        start=0;
        end=0;
    }
        
// Bean Patterns
    
    public Color getColor(){return color;}
    public Stroke getStroke(){return stroke;}
    public boolean isAnimate(){return animate;}
    
    public void setColor(Color c){color=c;}
    public void setStroke(Stroke s){stroke=s;}
    public void animate(int start,int end){
        this.start=start;
        this.end=end;
        animate=true;
    }
    public void animateOff(){animate=false;}
    
    /**
     * Draws the path using a Path2D object on a Graphics2D object
     *   using the supplied transform.
     */
    public void paintComponent(Graphics2D g,AffineTransform at){
        Path2D.Double path=animate?getPath2D(start,end):getPath2D();
        path.transform(at);
        g.setColor(color);
        g.setStroke(stroke);
        g.draw(path);
        g.setStroke(new BasicStroke());
    }
    
    /** Sets the drawing color and draws at the same time. */
    public void paintComponent(Graphics2D g,AffineTransform at,Color c){
        setColor(c);
        paintComponent(g,at);
    }

    /** Responds to change events... perhaps from the containing BPlot2D?? */
    public void stateChanged(ChangeEvent e){}

    public double getGeoX(){return get(0).x;}
    public double getGeoY(){return get(0).y;}
    public void setX(int wx){}
    public void setY(int wy){}
    public void paintComponent(Graphics gb) {
    }

    public boolean clicked(MouseEvent e) {return false;
    }

    public void actionPerformed(ActionEvent e) {
    }

    public void initializeModels() {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent e) {
    }

    public void mouseMoved(MouseEvent e) {
    }

    public void setX(double wx) {
    }

    public void setY(double wy) {
    }
}

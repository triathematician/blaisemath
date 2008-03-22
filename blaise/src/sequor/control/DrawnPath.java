/**
 * DrawnPath.java
 * Created on Mar 21, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import sequor.VisualControl;

/**
 * Draws and stores a mouse path.
 * @author Elisha Peterson
 */
public class DrawnPath extends VisualControl {
    Path2D.Double path;

    // CONSTRUCTOR
    
    public DrawnPath(){super();setVisible(false);}
    public DrawnPath(Color c,int style){setForeground(c);this.style=style;setVisible(false);}
    
    // PAINT METHODS
    
    int style=HIGHLIGHTER;
    public static final int PEN=0;
    public static final int MARKER=1;
    public static final int HIGHLIGHTER=2;
    
    @Override
    public void paintComponent(Graphics2D g, float opacity) {
        if(!isVisible()){return;}
        g.setColor(getForeground());
        switch(style){
            case(MARKER):
                g.setStroke(new BasicStroke(5.0f));
                break;
            case(HIGHLIGHTER):
                g.setStroke(new BasicStroke(10.0f));
                break;
            case(PEN):
                g.setStroke(new BasicStroke(2.0f));
                break;
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
        if(path!=null){g.draw(path);}
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    
    // ACTIONS
    
    public void clear(){if(path==null){path=new Path2D.Double();}}
    
    
    // MOUSE METHODS

    @Override
    public boolean clicked(MouseEvent e) {return isVisible();}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        path.lineTo(e.getX(),e.getY());
        fireStateChanged();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        clear();
        path.moveTo(e.getX(),e.getY());
        fireStateChanged();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}

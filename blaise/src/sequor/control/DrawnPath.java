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
import java.awt.geom.Path2D;
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
    public static final int PENCIL=0;
    public static final int PEN=1;
    public static final int MARKER=2;
    public static final int HIGHLIGHTER=3;
    
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
            case(PENCIL):
            default:
                g.setStroke(new BasicStroke(1.0f));
                break;
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
        if(path!=null){g.draw(path);}
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }
    
    
    /** Sets to another marker's style. */
    void setStyle(DrawnPath copee) {
        setForeground(copee.getForeground());
        this.style=copee.style;
        fireStateChanged();
    }
    
    
    // ACTIONS
    
    public void clear(){path=new Path2D.Double();}
    
    
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
        if(path==null){clear();}
        path.moveTo(e.getX(),e.getY());
        fireStateChanged();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }
}

package sequor.control;

/**
 * DrawnPath.java
 * Created on Mar 21, 2008
 */



import sequor.control.*;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.util.Vector;
import scio.coordinate.R2;
import sequor.VisualControl;

/**
 * Draws and stores a mouse path.
 * @author Elisha Peterson
 */
public class DrawnPath extends VisualControl {
    protected GeneralPath path;

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
        if(tempShape!=null){g.draw(tempShape);}
    }
    
    
    /** Sets to another marker's style. */
    void setStyle(DrawnPath copee) {
        setForeground(copee.getForeground());
        this.style=copee.style;
        fireStateChanged();
    }
    
    
    // ACTIONS
    
    public void clear(){path=new GeneralPath();}
    
    
    // MOUSE METHODS

    @Override
    public boolean clicked(MouseEvent e) {return isVisible();}
    
    @Override
    public void mouseDragged(MouseEvent e) {
        path.lineTo(e.getX(),e.getY());
        observed.add(new R2(e.getX(),e.getY()));
        fireStateChanged();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(path==null){clear();}
        path.moveTo(e.getX(),e.getY());
        observed=new Vector<R2>();
        fireStateChanged();
    }

    @Override
    public void mouseReleased(MouseEvent e) {}
    

    public Vector<R2> observed=new Vector<R2>();
    public GeneralPath tempShape;
}

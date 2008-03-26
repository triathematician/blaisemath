/**
 * DrawnPath.java
 * Created on Mar 21, 2008
 */

package sequor.control;

import sequor.control.gestures.Gesture;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Vector;
import scio.coordinate.R2;
import scio.random.Markov;
import scio.random.Markov.CurrentState;
import sequor.VisualControl;
import sequor.control.gestures.AngleGesture;

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
    
    Shape tempShape;
    
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
    
    public void clear(){path=new Path2D.Double();}
    
    
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
    public void mouseReleased(MouseEvent e) {
        markovOutput();
    }
    

    Vector<R2> observed=new Vector<R2>();
    
    /**  Outputs left/right sequence of a path */
    public void markovOutput(){
        try {
            R2[] observations = new R2[observed.size()-1];
            for(int i=1;i<observed.size();i++){
                observations[i-1]=observed.get(i).minus(observed.get(i-1));
            }
            
            System.out.println(observations.toString());            
            System.out.println(Gesture.clipOutput(new AngleGesture.UpDown().computePath(observations),"0"));
            System.out.println(Gesture.clipOutput(new AngleGesture.LeftRight().computePath(observations),"0"));
            System.out.println(Gesture.clipOutput(new AngleGesture.FourDir().computePath(observations),"0"));
            System.out.println(Gesture.clipOutput(new AngleGesture.EightDir().computePath(observations),"0"));            
            
            Vector<String> result = new AngleGesture.EightDir().computePath(observations);
            Vector<String> gesture=Gesture.clipOutput(result,"0");
            tempShape=Gesture.checkGesture(gesture);
            if(tempShape!=null){fireStateChanged();}
        } catch (Exception e) {
        }
    }
}

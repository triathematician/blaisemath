/**
 * ToggleButton.java
 * Created on Mar 13, 2008
 */

package sequor.control;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

/**
 *
 * @author Elisha Peterson
 */
public class ToggleButton extends VisualButton {
    
    public ToggleButton(){super();pressed=false;}
    public ToggleButton(String actionCommand,ActionListener al,ShapeLibrary.BoundedElement shape){super(actionCommand,al,shape);pressed=false;}
    public ToggleButton(double x,double y,double sz,boolean pressed){this(x,y,sz,sz,null,pressed);}
    public ToggleButton(double x,double y,double sz,String s,boolean pressed){this(x,y,sz,sz,s,pressed);}
    public ToggleButton(double x,double y,double wid,double ht,String s,boolean pressed){
        super(x,y,wid,ht,s);
        this.pressed=pressed;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    
    @Override
    public void mousePressed(MouseEvent e){}
    
    @Override
    public void mouseReleased(MouseEvent e){
        pressed=!pressed;
        fireStateChanged();
    }
}

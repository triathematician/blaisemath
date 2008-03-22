/**
 * ToggleButton.java
 * Created on Mar 13, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.*;

/**
 *
 * @author Elisha Peterson
 */
public class ToggleButton extends VisualButton {
    String releaseCommand;
    ActionListener pressListener;
    ActionListener releaseListener;
    
    public ToggleButton(){super();pressed=false;}
    public ToggleButton(String actionCommand,ActionListener al,BoundedShape shape){
        super(shape);
        this.actionCommand=actionCommand;
        pressListener=al;
        releaseListener=al;
        releaseCommand=actionCommand;
        pressed=false;
    }
    public ToggleButton(int x,int y,int sz,boolean pressed){this(x,y,sz,sz,null,pressed);}
    public ToggleButton(ActionListener al1,ActionListener al2, BoundedShape shape,Color bgColor) {
        setBackgroundShape(shape);
        actionCommand="pressed";
        pressListener=al1;
        releaseCommand="released";
        releaseListener=al2;
        setForeground(Color.BLACK);
        setBackground(bgColor);
        pressed=false;
    }
    public ToggleButton(int x,int y,int sz,String s,boolean pressed){this(x,y,sz,sz,s,pressed);}
    public ToggleButton(int x,int y,int wid,int ht,String s,boolean pressed){
        super(x,y,wid,ht,s);
        this.pressed=pressed;
    }

    @Override
    protected void fireActionPerformed(String s) {
        if(pressed){
            pressListener.actionPerformed(new ActionEvent(this,0,actionCommand));
        }else{
            releaseListener.actionPerformed(new ActionEvent(this,1,releaseCommand));            
        }
        super.fireActionPerformed(s);
    }
    
    // MOUSE HANDLING

    @Override
    public void mouseClicked(MouseEvent e){}    
    @Override
    public void mousePressed(MouseEvent e){
        if(clicked(e)){
            fireStateChanged();
        }
    }
    @Override
    public void mouseReleased(MouseEvent e){
        pressed=!pressed;
        fireActionPerformed(actionCommand);
        fireStateChanged();
    }
}

/**
 * ToggleButton.java
 * Created on Mar 13, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.BooleanModel;

/**     Changes to this status should force changes to "pressed"
 *      Changes to this status should lead to firing an ActionCommand
 *      Write in beans for the BooleanModel
 * Also, perhaps write a similar button for IntegerRangeModel's allowing them to be "cycled"
 */


/**
 * A ToggleButton adds in the capability for a button to be selected and de-selected when pressed.
 * That is, it has two states which cycle given a mouse click.
 *
 * @author Elisha Peterson
 */
public class ToggleButton extends VisualButton {
    
    /** Stores the underlying status of the button. "True" corresponds to selected, "False" to de-selected. */
    BooleanModel state;
    
    
    // CONSTRUCTORS
    
    /** Default to false state */
    public ToggleButton(){this(false);}
    public ToggleButton(boolean status){this(status,BoundedShape.ELLIPSE);}
    public ToggleButton(boolean status,BoundedShape shape){this(status,shape,Color.BLACK);}
    public ToggleButton(boolean status,BoundedShape shape,Color c){
        super(shape);
        setForeground(c);
        state=new BooleanModel(status);
        pressed=status;
        actionCommand="toggle";  
        initEventListening();      
    }
    public ToggleButton(BooleanModel state, BoundedShape shape) {
        super(shape);
        this.state = state;
        pressed = state.getValue();
        initEventListening();
    }
    public ToggleButton(String actionCommand,ActionListener al,BoundedShape shape){
        super(actionCommand,al,shape);
        state=new BooleanModel(false);
        pressed=false;
        initEventListening();     
    }
    public ToggleButton(boolean status,String actionCommand,ActionListener al,BoundedShape shape){
        super(actionCommand,al,shape);
        state=new BooleanModel(status);
        pressed=status;
        initEventListening();     
    }
    public ToggleButton(BooleanModel stereo, String actionCommand,ActionListener al,BoundedShape shape) {
        super(actionCommand,al,shape);
        state=stereo;
        pressed=stereo.getValue();
        initEventListening();     
    }
    
    
    // INITIALIZERS
    
    /** Initializes event listeneing. */    
    public void initEventListening(){
        state.addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                pressed = state.getValue();
            }
        });
        addActionListener(state.getToggleListener());
    }

    
    // QUERY METHODS
    
    public BooleanModel getModel(){return state;}
    
    
    // ADJUST MOUSE PRESS HIGHLIGHTING BEHAVIOR
        
    @Override
    public void mousePressed(MouseEvent e){
        if(clicked(e)){ fireStateChanged(); }
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        fireActionPerformed(actionCommand);
        fireStateChanged();
    }
}

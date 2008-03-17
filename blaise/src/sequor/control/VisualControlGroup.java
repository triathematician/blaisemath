/*
 * VisualControlGroup.java
 * Created on Mar 16, 2008
 */

package sequor.control;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;
import sequor.VisualControl;

/**
 * VisualControlGroup groups a collection of VisualControl's together.
 * 
 * @author Elisha Peterson
 */
public class VisualControlGroup extends VisualControl implements ActionListener {
    /** Elements in the group. */
    Vector<VisualControl> elements;
    
    /** Initialize with bounding box. */
    public VisualControlGroup(double x,double y,double wid,double ht){
        super(x,y,wid,ht);
        elements=new Vector<VisualControl>();
    }

    
    // ADD/REMOVE METHODS
    
    public void add(VisualControl vc){
        if(vc!=null){
            elements.add(vc);
            vc.addChangeListener(this);
            if(vc instanceof VisualButton){
                ((VisualButton)vc).addActionListener(this);
            }
        } 
    }
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g) {
        for(VisualControl vc:elements){vc.paintComponent(g);}
    }
    
    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        fireStateChanged();
    }

    
    // MOUSE EVENTS
    
    @Override
    public boolean clicked(MouseEvent e) {
        for(VisualControl vc:elements){if(vc.clicked(e)){return true;}}
        return false;
    }
        
    VisualControl active;
    
    @Override
    public void mouseClicked(MouseEvent e){
        for(VisualControl vc:elements){
            if(vc.clicked(e)){
                vc.mouseClicked(e);
                active=null;
                return;
            }
        }        
        active=null;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(VisualControl vc:elements){
            if(vc.clicked(e)){
                active=vc;
                vc.mousePressed(e);
                return;
            }
        }
        active=null;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(active!=null){active.mouseDragged(e);}
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(active!=null){active.mouseReleased(e);}
    }
}

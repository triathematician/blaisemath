/*
 * VisualControlGroup.java
 * Created on Mar 16, 2008
 */

package sequor;

import java.awt.Color;
import sequor.control.*;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * VisualControlGroup groups a collection of VisualControl's together.
 * 
 * @author Elisha Peterson
 */
public class VisualControlGroup extends VisualControl implements ActionListener {
    
    protected int spacing=4;
    protected int padding=4;
    
    /** Elements in the group. */
    protected Vector<VisualControl> elements;
    
    /** Initialize with bounding box. */
    public VisualControlGroup(int x,int y,int wid,int ht){
        super(x,y,wid,ht);
        elements=new Vector<VisualControl>();
        setForeground(new Color(0,0,50));
        setBackground(new Color(100,100,200));
        draggable=true;
    }
    
    
    // BEAN PATTERNS
    
    /** Returns spacing between buttons */
    public int getSpacing(){return spacing;}
    /** Returns padding */
    public int getPadding(){return padding;}
    /** Sets spacing between buttons */
    public void setSpacing(int spacing){this.spacing=spacing;}
    /** Sets padding on the outside */
    public void setPadding(int padding){this.padding=padding;}
    
    /** Override to translate all elements contained in the group. */
    @Override
    public void setLocation(int x,int y){
        int curX=getX();
        int curY=getY();
        super.setLocation(x,y);
        int offsetX=getX()-curX;
        int offsetY=getY()-curY;
        for(VisualControl vc:elements){
            vc.translate(offsetX,offsetY);
        }
    }

    
    // ADD/REMOVE METHODS
    
    public void add(VisualControl vc){
        if(vc!=null){
            elements.add(vc);
            vc.setDraggable(false);
            vc.translate(getX(),getY());
            vc.addChangeListener(this);
            if(vc instanceof VisualButton){
                ((VisualButton)vc).addActionListener(this);
            }
        } 
    }
    
    
    // PAINT METHODS
        
    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g,0.2f);
        for(VisualControl vc:elements){vc.paintComponent(g);}
    }    
    @Override
    public void paintComponent(Graphics2D g,float opacity) {
        super.paintComponent(g,opacity);
        for(VisualControl vc:elements){vc.paintComponent(g,opacity);}
    }
    
    
    // EVENT HANDLING

    public void actionPerformed(ActionEvent e) {
        fireStateChanged();
    }
    
    
    // MOUSE EVENTS
        
    protected VisualControl active;
    
    /** Used when an element of the control group is not clicked, but the group itself is. */
    public void clickAction(MouseEvent e){}
    
    @Override
    public void mouseClicked(MouseEvent e){
        // priority to control elements
        for(VisualControl vc:elements){
            if(vc.clicked(e)){
                vc.mouseClicked(e);
                active=null;
                return;
            }
        }        
        if(clicked(e)){
            clickAction(e);
            active=null;
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // if an element is clicked
        for(VisualControl vc:elements){
            if(vc.clicked(e)){
                active=vc;
                vc.mousePressed(e);
                return;
            }
        }
        // if the box is clicked, enable "drag" behavior
        if(draggable && clicked(e)){
            active=this;
            initialPoint=new Point(getX()-e.getX(),getY()-e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(active==this && initialPoint!=null){
            Point current=e.getPoint();
            setLocation(current.x+initialPoint.x,current.y+initialPoint.y);
            fireStateChanged();
        }else if(active!=null){
            active.mouseDragged(e);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(active==this && initialPoint!=null){
            Point current=e.getPoint();
            setLocation(current.x+initialPoint.x,current.y+initialPoint.y);
            initialPoint=null;
            active=null;
        }else if(active!=null){
            active.mouseReleased(e);
            active=null;
        }
    }
}

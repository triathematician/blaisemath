/*
 * VisualButton.java
 * Created on Mar 15, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import sequor.VisualControl;
import sequor.control.ShapeLibrary.BoundedElement;
import sequor.model.ComboBoxRangeModel;
import specto.style.LineStyle;

/**
 * VisualButton has 
 * @author Elisha Peterson
 */
public class VisualButton extends VisualControl {
    boolean pressed;
    String label;
    String actionCommand;
    
    ShapeLibrary.BoundedElement normalShape;
    ShapeLibrary.BoundedElement pressedShape;
    
    public VisualButton(){this(0,0,15);}
    public VisualButton(String actionCommand,ActionListener al,ShapeLibrary.BoundedElement shape){
        this(shape);
        this.actionCommand=actionCommand;
        addActionListener(al);
    }
    public VisualButton(ShapeLibrary.BoundedElement shape){this();setButtons(shape);}
    public VisualButton(double x,double y,double sz){this(x,y,sz,sz,null);}
    public VisualButton(double x,double y,double sz,String label){this(x,y,sz,sz,label);}
    public VisualButton(double x,double y,double wid,double ht,String label){      
        super(x,y,wid,ht);
        this.label=label;
        pressed=false;
        actionCommand="button";
        initStyle();        
    }
    
    // BEAN PATTERNS
    
    public void setSize(double size){
        boundingBox.setRect(getX(),getY(),size,size);
    }
    
    public void setButtons(ShapeLibrary.BoundedElement normalShape){
        this.normalShape=normalShape;
        this.pressedShape=normalShape;
    }
    
    public void setButtons(ShapeLibrary.BoundedElement normalShape,ShapeLibrary.BoundedElement pressedShape){
        this.normalShape=normalShape;
        this.pressedShape=pressedShape;
    }

    public boolean isPressed(){return pressed;}
    public void setPressed(boolean pressed) {this.pressed=pressed;}
    
    
    // PAINT METHODS
    
    @Override
    public void paintComponent(Graphics2D g){
        if(pressed){
            g.setColor(color.getValue());
            paintNormal(g,0.9f);
            g.setColor(Color.WHITE);
            if(pressedShape==null){
                g.fill(new Ellipse2D.Double(getX()+3,getY()+3,getWidth()-6,getHeight()-6));
            }else{
                g.fill(pressedShape.getShape());
            }
        }else{
            g.setColor(color.getValue());
            if(normalShape==null){
                paintNormal(g,0.3f);
            }else{
                paintNormal(g,0.3f);
                g.fill(normalShape.getShape());
            }
        }
    }
    
    public void paintNormal(Graphics2D g,float opacity) {
        Shape outer;
        switch(buttonStyle.getValue()){
            case STYLE_BOX:
                outer=new Rectangle2D.Double(getX(),getY(),getWidth()-1,getHeight()-1);
                break;
            case STYLE_RBOX:
                outer=new RoundRectangle2D.Double(getX(),getY(),getWidth()-1,getHeight()-1,5,5);
                break;
            case STYLE_CIRCLE:
            default:
                outer=new Ellipse2D.Double(getX(),getY(),getWidth()-1,getHeight()-1);
                break;
        }
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));
        g.fill(outer);        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.setStroke(LineStyle.THIN_STROKE);
        g.draw(outer);
        if(label!=null){
            g.drawString(label,(float)(getX()+getWidth()+5),(float)(getY()+4.0));
        }
    }
    
    // MOUSE EVENTS
    
    @Override
    public void mouseClicked(MouseEvent e) {
        pressed=false;
        fireActionPerformed(actionCommand);
    }    
    
    @Override
    public void mousePressed(MouseEvent e){
        if(clicked(e)){
            pressed=true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e){
        pressed=false;
        fireActionPerformed(actionCommand);
    }
    
    
    // ACTION EVENT HANDLING
     
    protected ActionEvent actionEvent=null;
    protected EventListenerList actionListenerList=new EventListenerList();    
    public void addActionListener(ActionListener l){listenerList.add(ActionListener.class,l);}
    public void removeActionListener(ActionListener l){listenerList.remove(ActionListener.class,l);}
    protected void fireActionPerformed(String s){
        Object[] listeners=listenerList.getListenerList();
        for(int i=listeners.length-2; i>=0; i-=2){
            if(listeners[i]==ActionListener.class){
                if(actionEvent==null){actionEvent=new ActionEvent(this,0,s);}
                ((ActionListener)listeners[i+1]).actionPerformed(actionEvent);
            }
        }
    }    
    
    
    // STYLE SETTINGS
    
    
    // STYLE OPTIONS
    
    ComboBoxRangeModel buttonStyle;
    public static final int STYLE_CIRCLE=0;
    public static final int STYLE_BOX=1;
    public static final int STYLE_RBOX=2;
    public static String[] styleStrings={"Circle","Square","Rounded"};
    public ComboBoxRangeModel getButtonStyle(){return buttonStyle;}

    private void initStyle() {
        buttonStyle=new ComboBoxRangeModel(styleStrings,0,0,2);
    }
}

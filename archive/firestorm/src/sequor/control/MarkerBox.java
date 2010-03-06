/*
 * MarkerBox.java
 * Created on Mar 22, 2008
 */

package sequor.control;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import javax.swing.event.ChangeEvent;
import sequor.model.BooleanModel;
import sequor.model.BooleanModelGroup;

/**
 * MarkerBox is a collection of buttons which when pressed will add the ability to "white board" draw on the plot. Only one marker should
 * be selected at a time, and none may be selected. So we use a ButtonGroup class to control the selection process.
 * 
 * @author Elisha Peterson
 */
public class MarkerBox extends ButtonBox {
    
    /** Group used so that only one thing is selected at a time. */
    BooleanModelGroup group;
    /** Mapping from BooleanModel to marker style. */
    HashMap<BooleanModel,DrawnPath> markers;
    
    // CONSTRUCTORS
    
    public MarkerBox(){this(Color.YELLOW,DrawnPath.HIGHLIGHTER);}
    public MarkerBox(Color c,int style){
        super(0,0,70,70,ButtonBox.LAYOUT_HLINE);
        group=new BooleanModelGroup(true,false);
        group.addChangeListener(this);
        markers=new HashMap<BooleanModel,DrawnPath>();
        // shows/hides all markers
        add(new ToggleButton(true,new BoundedWidthShape.Ring(.5)));
        // erases all paths
        add(new VisualButton(
                "erase",
                new ActionListener(){public void actionPerformed(ActionEvent e){eraseAllMarkers();fireStateChanged();}},
                BoundedShape.RECTANGLE
                ));
        addMarker(c,style);
    }
    
    /** Adds a marker to the plot. */
    public void addMarker(Color c,int style){
        ToggleButton tb=new ToggleButton(false,BoundedShape.PEN_SHAPE,c);
        add(tb);
        group.add(tb.getModel());
        markers.put(tb.getModel(),new DrawnPath(c,style));
        adjustBounds();
        performLayout();
    }
    
    /** Shows or hides all markers. */
    public void toggleVisible(){
        boolean anyOn=false;
        for(BooleanModel bm:markers.keySet()){
            anyOn=anyOn || markers.get(bm).isVisible();
        }
        for(BooleanModel bm:markers.keySet()){
            markers.get(bm).setVisible(!anyOn);
        }
        if(!anyOn){group.clearSelection();}
    }
    
    /** Erases all markers. */
    public void eraseAllMarkers(){
        for(BooleanModel bm:markers.keySet()){markers.get(bm).clear();}        
    }
    
    

    /** Called when the selected marker changes. */
    @Override
    public void stateChanged(ChangeEvent e) {
        // Marker selection has changed        
        if(e.getSource().equals(group)){
            BooleanModel bm=group.getActiveModel();
            if(bm==null){
            }else{
                getElement(0).setPressed(true);
                markers.get(bm).setVisible(true);
            }
        }
        super.stateChanged(e);
    }
    
    
    // PAINTING

    @Override
    public void paintComponent(Graphics2D g) {
        super.paintComponent(g);
        if(!getElement(0).pressed){return;}
        for(BooleanModel bm:markers.keySet()){
            if(markers.get(bm).isVisible()){
                markers.get(bm).paintComponent(g);
            }
        }
    }

    @Override
    public void paintComponent(Graphics2D g, float opacity) {
        super.paintComponent(g, opacity);
        if(!getElement(0).pressed){return;}
        for(BooleanModel bm:markers.keySet()){
            if(markers.get(bm).isVisible()){
                markers.get(bm).paintComponent(g,opacity);
            }
        }
    }
    
    
    // EVENT HANDLING

    @Override
    public boolean clicked(MouseEvent e) {return super.clicked(e) || (getElement(0).pressed && group.getActiveModel()!=null);}

    @Override
    public void clickAction(MouseEvent e) {
        if(getElement(0).pressed && group.getActiveModel()!=null){return;}
        super.clickAction(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e){
        super.mousePressed(e);
        // active marker if possible
        if(active==this && group.getActiveModel()!=null){
            active=markers.get(group.getActiveModel());
            active.mousePressed(e);
        }
    }  
    
    @Override
    public void mouseDragged(MouseEvent e){
        super.mouseDragged(e);
        fireStateChanged();
    }
}

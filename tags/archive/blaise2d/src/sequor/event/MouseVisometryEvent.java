/*
 * MouseVisometryEvent.java
 * Created on Mar 2, 2008
 */

package sequor.event;

import java.awt.Component;
import java.awt.event.MouseEvent;
import scio.coordinate.Coordinate;
import specto.Visometry;

/**
 * MouseVisometryEvent is a class for handling mouse events that occur in a particular visometry. If the proper constructor is used,
 * the event will contain a coordinate specifying the visometry coordinate value of the function.
 * @author Elisha Peterson
 */
public class MouseVisometryEvent<V extends Visometry> extends MouseEvent {
    Coordinate coordinate;

    public MouseVisometryEvent(Component source, int id, long when, int modifiers, int x, int y, int xAbs, int yAbs, int clickCount, boolean popupTrigger, int button) {
        super(source, id, when, modifiers, x, y, xAbs, yAbs, clickCount, popupTrigger, button);
    }

    public MouseVisometryEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger);
    }

    public MouseVisometryEvent(Component source, int id, long when, int modifiers, int x, int y, int clickCount, boolean popupTrigger, int button) {
        super(source, id, when, modifiers, x, y, clickCount, popupTrigger, button);
    }
    
    public MouseVisometryEvent(MouseEvent e,V v){
        this((Component)e.getSource(),e.getID(),e.getWhen(),e.getModifiersEx(),e.getX(),e.getY(),e.getXOnScreen(),e.getYOnScreen(),e.getClickCount(),true,e.getButton());
        setSource(v);
        coordinate=v.toGeometry(getPoint());
    }
    
    public Coordinate getCoordinate(){return coordinate;}    
    public V getSourceVisometry(){return (V)source;}
}

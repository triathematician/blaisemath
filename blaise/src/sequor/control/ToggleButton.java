/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * ToggleButton.java
 * Created on Mar 13, 2008
 */

package sequor.control;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import sequor.VisualControl;
import java.awt.geom.*;
import specto.style.LineStyle;

/**
 *
 * @author Elisha Peterson
 */
public class ToggleButton extends VisualControl {
    String s;
    boolean state=false;
    double radius=6.0;
    
    public ToggleButton(double x,double y,String s,boolean state){
        position=new Point2D.Double(x,y);
        this.s=s;
        this.state=state;
    }

    @Override
    public void paintComponent(Graphics2D g) {
        Shape dot=new Ellipse2D.Double(position.x-radius,position.y-radius,2*radius,2*radius);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
        g.fill(dot);        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        g.setStroke(LineStyle.THIN_STROKE);
        g.draw(dot);
        if(state){
            g.fill(new Ellipse2D.Double(position.x-0.6*radius,position.y-0.6*radius,1.2*radius+1,1.2*radius+1));
        }
        g.drawString(s,(float)(position.x+radius+5),(float)(position.y+4.0));
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        state=!state;
        fireStateChanged();
    }    
}

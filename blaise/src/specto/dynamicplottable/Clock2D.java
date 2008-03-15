/*
 * Clock2D.java
 * Created on Mar 2, 2008
 */

package specto.dynamicplottable;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import scio.coordinate.R2;
import sequor.component.IntegerRangeTimer;
import specto.Animatable;
import specto.visometry.Euclidean2;

/**
 * Clock2D retrieves the current time and displays on the current plot window.
 *
 * @author Elisha Peterson
 */
public class Clock2D extends Point2D implements Animatable<Euclidean2> {
    double radius=30.0;

    public Clock2D(Point2D center){super(center.prm);}
    public Clock2D(Point2D center,double radius){super(center.prm);this.radius=radius;}
    public Clock2D(double x,double y,double radius){super(x,y);this.radius=radius;}

    @Override
    public void paintComponent(Graphics2D g,Euclidean2 v) {
        R2 center=getPoint();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.3f));
        g.setColor(new Color(255-(255-color.getValue().getRed())/3,255-(255-color.getValue().getGreen())/3,255-(255-color.getValue().getBlue())/3));
        g.fill(v.dot(center, radius));
        g.setColor(color.getValue());
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        // g.setStroke(new BasicStroke(4.0f));
       // g.draw(visometry.dot(center,radius));
        Calendar calendar=new GregorianCalendar();
        double hrAngle=calendar.get(Calendar.HOUR)*2*Math.PI/12-Math.PI/2;
        double minAngle=calendar.get(Calendar.MINUTE)*2*Math.PI/60.0-Math.PI/2;
        double secAngle=calendar.get(Calendar.SECOND)*2*Math.PI/60.0-Math.PI/2;        
        g.setStroke(new BasicStroke(1.0f));
        g.draw(v.winArrow(center,secAngle,0.9*radius,4.0));
        for(double ticks=-Math.PI/2;ticks<3*Math.PI;ticks+=Math.PI/6){
            g.draw(v.winLineAtRadius(center,ticks,0.85*radius, 0.95*radius));
        }
        g.setStroke(new BasicStroke(2.0f));
        // hour hand
        g.draw(v.winArrow(center,hrAngle,0.5*radius,5.0));
        // minute hand
        g.draw(v.winArrow(center,minAngle,0.8*radius,5.0));
        // date
        String day=Integer.toString(calendar.get(Calendar.DATE));
        String month=calendar.getDisplayName(Calendar.MONTH,Calendar.SHORT,Locale.getDefault());
        float x=(float) v.toWindowX(center.x);
        float y=(float) v.toWindowY(center.y);
        Font current=g.getFont();
        Font display=current.deriveFont(Font.BOLD,(float)(0.5*radius));
        g.setFont(display);
        g.drawString(day,x-15,y-(float)(0.1*radius));
        g.setFont(current);
        g.drawString(month,x-11,y-(float)(0.5*radius));
    }

    public void paintComponent(Graphics2D g, Euclidean2 v, IntegerRangeTimer t) {paintComponent(g,v);}
    public int getAnimatingSteps(){return 10;}    
    
    @Override
    public String toString(){return "Clock";}
}

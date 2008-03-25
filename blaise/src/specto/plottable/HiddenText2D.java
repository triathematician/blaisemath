/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * HiddenText2D.java
 * Created on Mar 24, 2008
 */

package specto.plottable;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import scio.coordinate.R2;
import specto.Plottable;
import specto.visometry.Euclidean2;

/**
 *
 * @author Elisha Peterson
 */
public class HiddenText2D extends Plottable<Euclidean2> {
    public R2 point;    
    float appearRange=1.0f;
    String text;

    public HiddenText2D(){this(3.14159,2.71828,"e^(i*pi) = -1");}
    public HiddenText2D(double x,double y,String s){point=new R2(x,y);text=s;}
    
    @Override
    public void paintComponent(Graphics2D g, Euclidean2 v) {
        if(!(v.contains(point))){return;}
        float size=(float) Math.min(v.getDrawWidth(),v.getDrawHeight());
        if(size>appearRange){return;}
        size=Math.min((appearRange/size-1)/2,1.0f);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,size));
        g.draw(v.dot(point,2));
        g.drawString(text,(float)v.toWindowX(point.x),(float)v.toWindowY(point.y));
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
    }

    @Override
    public String[] getStyleStrings() {return null;}
}

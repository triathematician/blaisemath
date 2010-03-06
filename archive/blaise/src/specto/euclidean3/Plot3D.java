/*
 * Plot3D.java
 * Created on Sep 14, 2007, 8:14:40 AM
 */

package specto.euclidean3;

import java.awt.Color;
import sequor.control.BoundedShape;
import sequor.control.ButtonBox;
import sequor.control.NumberSlider;
import sequor.control.SliderBox;
import sequor.control.ToggleButton;
import specto.PlotPanel;

/** The 3D plot window which should be used in applications. Will have support for drawing
 * three-dimensional axes, coordinate planes, labels, animations, event handling.
 * <br><br>
 * @author Elisha Peterson
 */
public class Plot3D extends PlotPanel<Euclidean3> {

    // PROPERTIES
    
    StandardGrid3D grid;
    Axes3D axes;

    SliderBox controlBox;
    ButtonBox viewBox;
    
    // CONSTANTS

    // CONSTRUCTORS

    /** Default constructor */
    public Plot3D(){
        super(new Euclidean3());   
        // this assumes that the screen is approximately 20cm wide
        visometry.setDesiredBounds(-20.0, -20.0, 20.0, 20.0); 
        
        grid = new StandardGrid3D();
        addBase(grid);
        axes = new Axes3D();
        addBase(axes);
        controlBox = get3DControls();
        add(controlBox,5,2);
        
        viewBox = new ButtonBox();
        viewBox.add(new ToggleButton(visometry.stereo, BoundedShape.BOWTIE));
        add(viewBox,5,1);
        //setBackground(Color.BLACK);
        //add(new ParametricSurface3D.Sphere(new R3(0,0,0), visometry.proj.sceneSize.getValue()));
    }


    // BEAN PATTERNS: GETTERS & SETTERS

    public String getXLabel() { return axes.getXLabel(); }
    public void setXLabel(String xLabel) { axes.setXLabel(xLabel); }
    public String getYLabel() { return axes.getYLabel(); }
    public void setYLabel(String yLabel) { axes.setYLabel(yLabel); }
    public String getZLabel() { return axes.getZLabel(); }
    public void setZLabel(String zLabel) { axes.setZLabel(zLabel); }
    
    public int getAxisStyle() { return axes.style.getValue(); }
    public void setAxisStyle(int newValue) { axes.style.setValue(newValue); }
    
    public int getXyStyle() { return grid.xyStyle; }
    public void setXyStyle(int xyStyle) { grid.xyStyle = xyStyle; }
    public int getXzStyle() { return grid.xzStyle; }
    public void setXzStyle(int xzStyle) { grid.xzStyle = xzStyle; }
    public int getYzStyle() { return grid.yzStyle; }
    public void setYzStyle(int yzStyle) { grid.yzStyle = yzStyle; }
    
    // HELPER METHODS
    
    /** Returns box of sliders for adjusting visual elements. */
    public SliderBox get3DControls(){
        SliderBox sb = new SliderBox();
        NumberSlider ns = new NumberSlider(visometry.proj.clipDist);
        ns.setName("clipping");
        sb.add(ns);
        ns = new NumberSlider(visometry.proj.viewDist);
        ns.setName("inverse zoom");
        sb.add(ns);
        ns = new NumberSlider(visometry.proj.sceneSize);
        ns.setName("scene size");
        sb.add(ns);
        ns = new NumberSlider(visometry.proj.timerDelay);
        ns.setName("rotation timer delay");
        sb.add(ns);
        ns = new NumberSlider(visometry.proj.eyeSep);
        ns.setName("eye separation");
        sb.add(ns);
        return sb;
    }

    public boolean isControlsVisible(){ return controlBox!=null && controlBox.isVisible();}
    public void setControlsVisible(boolean newValue){
        if(newValue!=controlBox.isVisible()){
            if(newValue==true){ 
                if(controlBox==null){controlBox=get3DControls(); add(controlBox,5,2);}
                controlBox.setVisible(true);
            }
            if(newValue==false){ controlBox.setVisible(false); }
        }
    }

    public boolean isViewBoxVisible(){ return viewBox!=null && viewBox.isVisible(); }
    public void setViewBoxVisible(boolean newValue){
        if(newValue!=viewBox.isVisible()){
            if(newValue==true){ 
                if(viewBox==null){
                    viewBox = new ButtonBox();
                    viewBox.add(new ToggleButton(visometry.stereo, BoundedShape.BOWTIE));
                    add(viewBox,5,1);
                }
                viewBox.setVisible(true);
            }
            if(newValue==false){ viewBox.setVisible(false); }
        }
    }
}

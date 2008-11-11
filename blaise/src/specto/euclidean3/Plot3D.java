/*
 * Plot3D.java
 * Created on Sep 14, 2007, 8:14:40 AM
 */

package specto.euclidean3;

import sequor.control.NumberSlider;
import sequor.control.SliderBox;
import specto.PlotPanel;

/** The 3D plot window which should be used in applications. Will have support for drawing
 * three-dimensional axes, coordinate planes, labels, animations, event handling.
 * <br><br>
 * @author Elisha Peterson
 */
public class Plot3D extends PlotPanel<Euclidean3> {

    // PROPERTIES
    
    // CONSTANTS
   


    // CONSTRUCTORS

    /** Default constructor */
    public Plot3D(){
        super(new Euclidean3());    
        
        addBase(new Axes3D());
        add(get3DControls(),5,2);
    }


    // BEAN PATTERNS: GETTERS & SETTERS


    
    // HELPER METHODS
    
    /** Returns box of sliders for adjusting visual elements. */
    public SliderBox get3DControls(){
        SliderBox sb = new SliderBox();
        NumberSlider ns = new NumberSlider(visometry.theta);
        ns.setName("theta");
        sb.add(ns);
        ns = new NumberSlider(visometry.phi);
        ns.setName("phi");
        sb.add(ns);
        ns = new NumberSlider(visometry.zoom);
        ns.setName("zoom");
        sb.add(ns);
        return sb;
    }
}

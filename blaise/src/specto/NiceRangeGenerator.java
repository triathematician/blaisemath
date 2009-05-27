/*
 * NiceRangeGenerator.java
 * Created on Mar 22, 2008
 */

package specto;

import java.util.Vector;
import sequor.model.DoubleRangeModel;

/**
 * NiceRangeGenerator is originally designed to generate nice grid spacing for plots,
 * so that when the plot is resized/moved, the grid lines are "stuck" on appropriate
 * values depending on the size of the window, NOT the position of the window. This
 * allows for the grid to be "smoothly" dragged around the screen.
 * 
 * Other uses for the methods herein include tick marks for sliders and graphs, etc.
 * 
 * @author Elisha Peterson
 */
public interface NiceRangeGenerator {

    public double getStep(double min,double max,double idealStep);
    public Vector<Double> niceRange(double min,double max,double idealStep);
    
    /** Generates standard range. Prefers step sizes of 1,2,2.5,5 */
    public class StandardRange implements NiceRangeGenerator {
        public double getStep(double min,double max,double idealStep){
            // make sure values are positive, nonzero, etc.
            if(min>max){double temp=min;min=max;max=temp;}
            if(idealStep==0){return 0;}
            idealStep=Math.abs(idealStep);
            // first, put the range in between 1 and 10
            double idealDigit=idealStep;
            while(idealDigit>10){idealDigit/=10;}
            while(idealDigit<1){idealDigit*=10;}
            // now set the factor as that times 1,2,2.5, or 5
            double factor=idealStep/idealDigit;
            factor *=
                    (idealDigit<1.8) ? 1 :
                        (idealDigit<2.2) ? 2 :
                            (idealDigit<3.5) ? 2.5 :
                                (idealDigit<7.5) ? 5 :
                                    10;
            return factor;
        }

        public Vector<Double> niceRange(double min,double max,double idealStep){
            double factor = getStep(min, max, idealStep);
            if(factor==0){return new Vector<Double>();}
            return new DoubleRangeModel(0,
                    factor*Math.ceil(min/factor),
                    factor*Math.floor(max/factor)*1.0001,
                    factor)
                    .getValueRange(true, 0.0);
        }
    };
    
    /** Generates values in multiples of pi. */
    public static final NiceRangeGenerator PI = new StandardRange() {
        @Override
        public double getStep(double min, double max, double idealStep) {
            return super.getStep(min, max, idealStep)*Math.PI/2;
        }
    };
    
}

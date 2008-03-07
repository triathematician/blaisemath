/*
 * LogPlot2D.java
 * Created on Mar 6, 2008
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.plotpanel;

import specto.transformer.LogarithmicTransformer;
import specto.visometry.Euclidean2;

/**
 * <p>
 * LogPlot2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class LogPlot2D extends Plot2D {
    LogarithmicTransformer lt;

    public LogPlot2D() {
    }
    
       
    public void setTransformation(LogarithmicTransformer lt){
        this.lt=lt;        
    }

    // TODO add code transforming to log coordinates
         
}

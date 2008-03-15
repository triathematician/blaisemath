/*
 * LogPlot2D.java
 * Created on Mar 6, 2008
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package specto.transformer;

import specto.PlotPanel;
import specto.gridplottable.Grid2D;
import specto.transformer.LogarithmicTransformer;
import specto.transformer.TransformedVisometry;
import specto.visometry.Euclidean2;

/**
 * <p>
 * LogPlot2D is ...
 * </p>
 * @author Elisha Peterson
 */
public class LogPlot2D extends PlotPanel<TransformedVisometry<Euclidean2,Euclidean2>> {
    public LogPlot2D() {
        super(new TransformedVisometry(new Euclidean2(),new LogarithmicTransformer(true,false)));
    }
}

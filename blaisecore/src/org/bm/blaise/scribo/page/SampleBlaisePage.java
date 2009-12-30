/**
 * SampleBlaisePage.java
 * Created on Dec 22, 2009
 */

package org.bm.blaise.scribo.page;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bm.blaise.scio.function.ParsedUnivariateRealFunction;
import org.bm.blaise.scribo.parser.ParseException;
import org.bm.blaise.specto.plane.PlaneAxes;
import org.bm.blaise.specto.plane.PlaneGrid;
import org.bm.blaise.specto.plane.PlanePlotComponent;
import org.bm.blaise.specto.plane.function.PlaneFunctionGraph;
import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>
 *    This class ...
 * </p>
 * @author Elisha Peterson
 */
class SampleBlaisePage implements BlaisePage {

    public String getIdentifier() {
        return "ID001";
    }

    public String getTitle() {
        return "SAMPLE";
    }

    public String getDescription() {
        return "This is a sample Blaise page, intended to test the layout functionality, etc.";
    }

    public String getText() {
        return "<html>Here we have the main text... so there's a lot going on here"
                + "and it should work to use <b>html</b> elements also (and possibly hyperlinks http://elishapeterson.wikidot.com)</html>";
    }

    public PlotComponent getActivePlot() {
        PlanePlotComponent ppc = new PlanePlotComponent();
        ppc.addPlottable(PlaneGrid.instance());
        ppc.addPlottable(PlaneAxes.instance("x", "y"));
        try {
            ppc.addPlottable(new PlaneFunctionGraph(new ParsedUnivariateRealFunction("cos(x)")));
        } catch (ParseException ex) {
            Logger.getLogger(SampleBlaisePage.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ppc;
    }

    public PlotComponent[] getPlot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PlotComponent getPlot(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PlotLayout getPlotLayout() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public BlaisePage getPreviousPage() {
        return null;
    }

    public BlaisePage getNextPage() {
        return null;
    }

}

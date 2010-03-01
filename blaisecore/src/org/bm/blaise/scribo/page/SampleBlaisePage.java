/**
 * SampleBlaisePage.java
 * Created on Dec 22, 2009
 */

package org.bm.blaise.scribo.page;

import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>
 *    This class ...
 * </p>
 * @author Elisha Peterson
 */
public class SampleBlaisePage implements BlaisePage, java.io.Serializable {

//    transient PlanePlotComponent ppc = null;

    public SampleBlaisePage() {
//        ppc = new PlanePlotComponent();
//        ppc.addPlottable(new PlaneGrid());
//        ppc.addPlottable(new PlaneAxes());
//        try {
//            ppc.addPlottable(new PlaneFunctionGraph(new ParsedUnivariateRealFunction("cos(x)")));
//        } catch (ParseException ex) {
//        }
    }

    public String getIdentifier() {
        return "ID001";
    }

    public String getTitle() {
        return "SAMPLE";
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public String getDescription() {
        return "This is a sample Blaise page, intended to test the layout functionality, etc.";
    }

    public String getText() {
        return "<html>Here we have the main text... so there's a lot going on here"
                + "and it should work to use <b>html</b> elements also (and possibly hyperlinks http://elishapeterson.wikidot.com)</html>";
    }

    public PlotComponent getActivePlot() {
        return null;
    }

    public void setActivePlot(PlotComponent plot) {
    }


    public PlotComponent[] getPlot() {
        return new PlotComponent[]{};
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

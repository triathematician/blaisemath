/**
 * BlaisePage.java
 * Created on Dec 21, 2009
 */

package org.bm.blaise.scribo.page;

import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>
 *    A <code>BlaisePage</code> is a panel combining a title, a description, one or
 *    more plot components, and possibly some settings. It is intended to be viewed
 *    as a combination of text and one or more plots.
 * </p>
 * @author Elisha Peterson
 */
public interface BlaisePage {

    /** Returns the unique identifier of the page. */
    String getIdentifier();

    /** Returns the title of the page. */
    String getTitle();
    /** Returns a description of the page. */
    String getDescription();


    /** Returns the previous page, possibly null. */
    BlaisePage getPreviousPage();
    /** Returns the next page, possibly null. */
    BlaisePage getNextPage();


    /** Returns the main text of the page. */
    String getText();


    /** Returns the active plot component(s) of the page. */
    PlotComponent getActivePlot();
    /** Returns all associated plot components. */
    PlotComponent[] getPlot();
    /** Returns the ith plot component. */
    PlotComponent getPlot(int i);
    /** Returns the plot layout. */
    PlotLayout getPlotLayout();


    /** Represents possible layouts for multi-plot elements. */
    public enum PlotLayout {
        TABBED,          // displays windows in tabs
        CENTER_TOP,      // places first window at top and large
        CENTER_LEFT,     // places first window at left and large
        TILE_VERTICAL,   // gives plots equal size in vertical direction
        TILE_HORIZONTAL, // gives plots equal size in horizontal direction
        TILE_SQUARE;     // attempts to place plots in a grid format
    }
}

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
    public String getIdentifier();
    
    /** Returns the title of the page. */
    public String getTitle();
    /** Returns a description of the page. */
    public String getDescription();

    /** Returns the main text of the page. */
    public String getText();

    /** Returns all associated plot components. */
    public PlotComponent[] getPlot();
    /** Returns the ith plot component. */
    public PlotComponent getPlot(int i);

    /** Returns the active plot component(s) of the page, or null if no plot is currently selected. */
    public PlotComponent getActivePlot();
    /** Sets the active plot component. Should be one of the plots existing on the page. */
    public void setActivePlot(PlotComponent plot);

    /** Returns the plot layout. */
    public PlotLayout getPlotLayout();

    /** Returns the previous page, possibly null. */
    public BlaisePage getPreviousPage();
    /** Returns the next page, possibly null. */
    public BlaisePage getNextPage();


    /** Represents possible layouts for multi-plot elements. */
    public enum PlotLayout {
        /** Displays multiple windows in tabs. */
        TABBED,
        /** Places first window at top, makes it large; remaining windows go underneath. */
        CENTER_TOP,
        /** Places first window at left, makes it large; remaining windows go to the right. */
        CENTER_LEFT,
        /** Gives plots equal size in vertical direction */
        TILE_VERTICAL,
        /** Gives plots equal size in horizontal direction */
        TILE_HORIZONTAL,
        /** Attempts to place plots in a grid format */
        TILE_SQUARE;
    }
}

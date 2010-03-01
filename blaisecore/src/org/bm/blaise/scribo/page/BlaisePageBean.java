/*
 * BlaisePageBean.java
 * Created Dec 31, 2009
 */
package org.bm.blaise.scribo.page;

import java.util.Arrays;
import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>Implements the <code>BlaisePage</code> interface with JavaBeans standards, to allow
 *   for simple XML serialization.</p>
 * @author Elisha Peterson
 */
public class BlaisePageBean implements BlaisePage, java.io.Serializable {

    protected String identifier;
    protected String title;
    protected String description;
    protected String text;

    protected PlotComponent[] plots;
    protected int activePlot;
    protected PlotLayout plotLayout;
    
    protected BlaisePage previous;
    protected BlaisePage next;

    public BlaisePageBean() {
        identifier = "";
        title = "";
        description = "";
        text = "";

        plots = new PlotComponent[0];
        activePlot = -1;
        plotLayout = PlotLayout.TABBED;

        previous = null;
        next = null;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public PlotComponent[] getPlot() {
        return plots;
    }

    public void setPlot(PlotComponent[] plots) {
        this.plots = plots;
    }

    public PlotComponent getPlot(int i) {
        return plots[i];
    }

    public void setPlot(int i, PlotComponent plot) {
        plots[i] = plot;
    }

    public PlotLayout getPlotLayout() {
        return plotLayout;
    }

    public void setPlotLayout(PlotLayout plotLayout) {
        this.plotLayout = plotLayout;
    }

    public PlotComponent getActivePlot() {
        if (activePlot >= 0 && activePlot < plots.length)
            return plots[activePlot];
        else
            return null;
    }

    public void setActivePlot(PlotComponent p) {
        activePlot = Arrays.asList(plots).indexOf(p);
    }

    public BlaisePage getPreviousPage() {
        return previous;
    }

    public void setPreviousPage(BlaisePage previous) {
        this.previous = previous;
    }

    public BlaisePage getNextPage() {
        return next;
    }

    public void setNextPage(BlaisePage next) {
        this.next = next;
    }
}

package Style;

import java.awt.Color;

/**
 * <b>BPlot2DOptions.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>February 21, 2007, 8:56 AM</i><br><br>
 * 
 * Contains a list of options for plotting functions... really just a
 * convenience method.
 */
public class BPlot2DOptions {
    
    /**
     * Constructor: creates a new instance of BPlot2DOptions
     */
    public BPlot2DOptions() {
    }

    /**
     * Holds value of property axesOn.
     */
    private boolean axesOn;

    /**
     * Getter for property axesOn.
     * @return Value of property axesOn.
     */
    public boolean isAxesOn() {
        return this.axesOn;
    }

    /**
     * Setter for property axesOn.
     * @param showAxes New value of property axesOn.
     */
    public void setAxesOn(boolean axesOn) {
        this.axesOn = axesOn;
    }

    /**
     * Holds value of property ticksOn.
     */
    private boolean ticksOn;

    /**
     * Getter for property ticksOn.
     * @return Value of property ticksOn.
     */
    public boolean isTicksOn() {
        return this.ticksOn;
    }

    /**
     * Setter for property ticksOn.
     * @param ticksOn New value of property ticksOn.
     */
    public void setTicksOn(boolean ticksOn) {
        this.ticksOn = ticksOn;
    }

    /**
     * Holds value of property axisLabelsOn.
     */
    private boolean axisLabelsOn;

    /**
     * Getter for property axisLabelsOn.
     * @return Value of property axisLabelsOn.
     */
    public boolean isAxisLabelsOn() {
        return this.axisLabelsOn;
    }

    /**
     * Setter for property axisLabelsOn.
     * @param axisLabelsOn New value of property axisLabelsOn.
     */
    public void setAxisLabelsOn(boolean axisLabelsOn) {
        this.axisLabelsOn = axisLabelsOn;
    }

    /**
     * Holds value of property gridOn.
     */
    private boolean gridOn;

    /**
     * Getter for property gridOn.
     * @return Value of property gridOn.
     */
    public boolean isGridOn() {
        return this.gridOn;
    }

    /**
     * Setter for property gridOn.
     * @param gridOn New value of property gridOn.
     */
    public void setGridOn(boolean gridOn) {
        this.gridOn = gridOn;
    }

    /**
     * Holds value of property axisValuesOn.
     */
    private boolean axisValuesOn;

    /**
     * Getter for property axisValuesOn.
     * @return Value of property axisValuesOn.
     */
    public boolean isAxisValuesOn() {
        return this.axisValuesOn;
    }

    /**
     * Setter for property axisValuesOn.
     * @param axisValuesOn New value of property axisValuesOn.
     */
    public void setAxisValuesOn(boolean axisValuesOn) {
        this.axisValuesOn = axisValuesOn;
    }

    /**
     * Holds value of property axesOutside.
     */
    private boolean axesOutside;

    /**
     * Getter for property axesOutside.
     * @return Value of property axesOutside.
     */
    public boolean isAxesOutside() {
        return this.axesOutside;
    }

    /**
     * Setter for property axesOutside.
     * @param axesOutside New value of property axesOutside.
     */
    public void setAxesOutside(boolean axesOutside) {
        this.axesOutside = axesOutside;
    }

    /**
     * Holds value of property axesColor.
     */
    private Color axesColor;

    /**
     * Getter for property axesColor.
     * @return Value of property axesColor.
     */
    public Color getAxesColor() {
        return this.axesColor;
    }

    /**
     * Setter for property axesColor.
     * @param axesColor New value of property axesColor.
     */
    public void setAxesColor(Color axesColor) {
        this.axesColor = axesColor;
    }

    /**
     * Holds value of property axisLabelsColor.
     */
    private Color axisLabelsColor;

    /**
     * Getter for property axisLabelsColor.
     * @return Value of property axisLabelsColor.
     */
    public Color getAxisLabelsColor() {
        return this.axisLabelsColor;
    }

    /**
     * Setter for property axisLabelsColor.
     * @param axisLabelsColor New value of property axisLabelsColor.
     */
    public void setAxisLabelsColor(Color axisLabelsColor) {
        this.axisLabelsColor = axisLabelsColor;
    }

    /**
     * Holds value of property defaultFunctionColor.
     */
    private Color defaultFunctionColor;

    /**
     * Getter for property defaultFunctionColor.
     * @return Value of property defaultFunctionColor.
     */
    public Color getDefaultFunctionColor() {
        return this.defaultFunctionColor;
    }

    /**
     * Setter for property defaultFunctionColor.
     * @param defaultFunctionColor New value of property defaultFunctionColor.
     */
    public void setDefaultFunctionColor(Color defaultFunctionColor) {
        this.defaultFunctionColor = defaultFunctionColor;
    }

    /**
     * Holds value of property gridColor.
     */
    private Color gridColor;

    /**
     * Getter for property gridColor.
     * @return Value of property gridColor.
     */
    public Color getGridColor() {
        return this.gridColor;
    }

    /**
     * Setter for property gridColor.
     * @param gridColor New value of property gridColor.
     */
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    /**
     * Holds value of property lowX.
     */
    private double lowX;

    /**
     * Getter for property lowX.
     * @return Value of property lowX.
     */
    public double getLowX() {
        return this.lowX;
    }

    /**
     * Setter for property lowX.
     * @param lowX New value of property lowX.
     */
    public void setLowX(double lowX) {
        this.lowX = lowX;
    }

    /**
     * Holds value of property lowY.
     */
    private double lowY;

    /**
     * Getter for property lowY.
     * @return Value of property lowY.
     */
    public double getLowY() {
        return this.lowY;
    }

    /**
     * Setter for property lowY.
     * @param lowY New value of property lowY.
     */
    public void setLowY(double lowY) {
        this.lowY = lowY;
    }

    /**
     * Holds value of property highX.
     */
    private double highX;

    /**
     * Getter for property highX.
     * @return Value of property highX.
     */
    public double getHighX() {
        return this.highX;
    }

    /**
     * Setter for property highX.
     * @param highX New value of property highX.
     */
    public void setHighX(double highX) {
        this.highX = highX;
    }

    /**
     * Holds value of property highY.
     */
    private double highY;

    /**
     * Getter for property highY.
     * @return Value of property highY.
     */
    public double getHighY() {
        return this.highY;
    }

    /**
     * Setter for property highY.
     * @param highY New value of property highY.
     */
    public void setHighY(double highY) {
        this.highY = highY;
    }

}

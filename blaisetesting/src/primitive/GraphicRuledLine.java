/*
 * GraphicRuledLine.java
 * Created Apr 13, 2010
 */

package primitive;

/**
 * This primitive is designed for objects such as a plot's axes, which display a line
 * together with several "tick marks" that label the line. The elements of the primitive
 * include the line's location (as two points), the locations of each tick mark (as doubles = distances from start),
 * labels associated with each tick mark (as an array, may be null), and a global line label (may be null).
 * 
 * @author Elisha Peterson
 */
public class GraphicRuledLine<C> {

    public C start;
    public C end;
    public double[] ticks;
    public String[] tickLabels;

    /**
     * Construct with all possible parameters.
     * @param start start position of line
     * @param end end position of line
     * @param ticks location of ticks relative to the start of the line, as a percentage of total distance
     * @param tickLabels labels of ticks
     */
    public GraphicRuledLine(C start, C end, double[] ticks, String[] tickLabels) {
        this.start = start;
        this.end = end;
        this.ticks = ticks;
        this.tickLabels = tickLabels;
    }

}

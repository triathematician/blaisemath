/*
 * ListSlider.java
 * Created Jul 8, 2010
 */

package util.swing;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * A slider that uses a list of underlying values for its peg points.
 *
 * @author Elisha Peterson
 */
public class ListSlider extends JSlider {

    /** Stores peg points for the slider */
    List<Double> pegs;

    /** Constructs with default values */
    public ListSlider() {
        this(Arrays.asList(0.0,1.0,2.0,3.0,4.0,5.0,6.0,7.0,8.0,9.0,10.0));
    }

    /** Constructs instance with specified min, max, and peg points */
    public ListSlider(List<Double> pegs) {
        super(0, pegs.size()-1, 0);
        setList(pegs);
    }

    /** @return current value as specified by the peg positions */
    public synchronized double getListValue() {
        int i = getValue();
        return i <= 0 ? pegs.get(0) : i >= pegs.size() ? pegs.get(pegs.size()-1) : pegs.get(i);
    }

    /** Sets up constraints */
    public synchronized void setList(List<Double> values) {
        this.pegs = values;
        setMaximum(pegs.size() - 1);
        setValue(0);
        initCustomLabels();
    }

    /** Sets up label table with associated peg marks */
    private void initCustomLabels() {
        int size = pegs.size();
        setMajorTickSpacing(size < 20 ? 1 : size < 50 ? 2
                : size < 100 ? 5 : size < 250 ? 10 : size < 500 ? 25 : size/20);
        setSnapToTicks(size < 20);

        setPaintTicks(true);
        setPaintLabels(true);
        Hashtable result = new Hashtable();
        try{
            NumberFormat nf = NumberFormat.getNumberInstance();
            for(int i = 0; i < pegs.size(); i += getMajorTickSpacing())
                result.put(i, new JLabel("t="+nf.format(pegs.get(i))));
        } catch (NullPointerException e) {
            result.put(0, new JLabel("low"));
            result.put(50, new JLabel("mid"));
            result.put(100, new JLabel("high"));
        }
        setLabelTable(result);
    }

}

/*
 * ListSlider.java
 * Created Jul 8, 2010
 */
package com.googlecode.blaisemath.util.swing;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
import com.google.common.base.Objects;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JSlider;

/**
 * A slider that uses a list of underlying values for its peg points.
 *
 * @author Elisha Peterson
 */
public final class ListSlider extends JSlider {

    /**
     * Stores peg points for the slider
     */
    private List<Double> pegs;

    /**
     * Constructs with default values
     */
    public ListSlider() {
        this(Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0));
    }

    /**
     * Constructs instance with specified peg points
     * @param pegs peg points for list
     */
    public ListSlider(List<Double> pegs) {
        super(0, pegs.size() - 1, 0);
        setList(pegs);
    }

    /**
     * Get current peg value of list
     * @return current value as specified by the peg positions
     */
    public double getListValue() {
        int i = getValue();
        return i <= 0 ? pegs.get(0) : i >= pegs.size() ? pegs.get(pegs.size() - 1) : pegs.get(i);
    }
    
    /**
     * Set the pegged value of the list
     * @param val double value of list
     */
    public void setListValue(double val) {
        for (int i = 0; i < pegs.size(); i++) {
            if (val == pegs.get(i)) {
                setValue(i);
                return;
            }
        }
        Logger.getLogger(ListSlider.class.getName()).log(Level.INFO, 
                "Value not found: {0}", val);
    }

    /**
     * Sets up constraints
     * @param values values for list
     */
    public void setList(List<Double> values) {
        if (!Objects.equal(this.pegs, values)) {
            this.pegs = values;
            setMaximum(pegs.size() - 1);
            setValue(0);
            initCustomLabels();
        }
    }

    /**
     * Sets up label table with associated peg marks
     */
    private void initCustomLabels() {
        int size = pegs.size();
        setMajorTickSpacing(size < 20 ? 1 : size < 50 ? 2
                : size < 100 ? 5 : size < 250 ? 10 : size < 500 ? 25 : size / 20);
        setSnapToTicks(size < 20);

        setPaintTicks(true);
        setPaintLabels(true);
        Dictionary result = new Hashtable();
        NumberFormat nf = NumberFormat.getNumberInstance();
        for (int i = 0; i < pegs.size(); i += getMajorTickSpacing()) {
            result.put(i, new JLabel("t=" + nf.format(pegs.get(i))));
        }
        setLabelTable(result);
    }

}

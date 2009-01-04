/**
 * DoubleSlider.java
 * Created on Mar 8, 2008
 */

package sequor.component;

import java.text.NumberFormat;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import sequor.model.DoubleRangeModel2;

/**
 *
 * @author Elisha Peterson
 */
public class DoubleSlider extends JSlider implements ChangeListener {
    private DoubleRangeModel2 model;

    public DoubleSlider(DoubleRangeModel2 model){
        this.model=model;
        model.addChangeListener(this);
        setPaintTicks(true);
        setPaintLabels(true);  
        setMajorTickSpacing(25);
        setMinorTickSpacing(5);
        initLabelTable();
        super.setValue(toInt(model.getValue()));
    }

    public void stateChanged(ChangeEvent e) {
        fireStateChanged();
        super.setValue(toInt(model.getValue()));
    }

    public int toInt(double d){
        return (int) ((getMaximum()-getMinimum())/(model.getRange())*(d-model.getMinimum())+getMinimum()); 
    }
    public double toModel(int n){
        return (model.getMaximum()-model.getMinimum())/(getMaximum()-getMinimum())*(n-getMinimum())+model.getMinimum();
    }
    @Override
    public void setValue(int n) {
        model.setValue(toModel(n));
        super.setValue(n);
    }

    public void initLabelTable() {
        Hashtable result=new Hashtable();
        try{
            NumberFormat nf=NumberFormat.getInstance();
            for(int i=getMinimum();i<=getMaximum();i+=getMajorTickSpacing()){
                result.put(i,new JLabel(nf.format(toModel(i))));
            }
        }catch(NullPointerException e){
            result.put(new Integer(0),new JLabel("low"));
            result.put(new Integer(50),new JLabel("mid"));
        }
        setLabelTable(result);
    }
}

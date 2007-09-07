/*
 * Settings.java
 * Created on Sep 6, 2007, 9:51:00 AM
 */

package Model;

import Interface.BModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class is intended as a superclass to add property change functionality to a collection
 * of settings. Mainly consists of some event handling routines! Extending classes should do the following
 * for each property which they implement:
 * <ul>
 *   <li>Implement as a model which generates checks the values and change events
 *   <li>Implement normal get/set methods for the property
 *   <li>Add the property with string, editor, and type code in the initializer using "addProperty"
 * </ul>
 * Property change events are automatically generated using the string from the property, and fired
 * to any classes which listen for property changes from this one.
 * If a GUI is required, a panel can be automatically generated using the underlying models
 * and a standard set of editors. The panel uses a SpringLayout to create two columns... one
 * with text labels and the other with the editing objects.
 * <br><br>
 * @author Elisha Peterson
 */
public abstract class Settings implements ChangeListener,PropertyChangeListener {
    
// PROPERTY HANDLING SUPPORT    
    
    /** List of property names. */
    private Vector<String> names=new Vector<String>();
    /** List of property object models (contains the underlying data). */
    private Vector<BModel> models=new Vector<BModel>();
    /** List of editor types to use. */
    private Vector<Integer> editors=new Vector<Integer>();

    // Constants for editor types
    public static final int EDIT_DOUBLE=0;
    public static final int EDIT_INTEGER=1;
    public static final int EDIT_COMBO=2;
    public static final int EDIT_STRING=3;
    public static final int EDIT_COLOR=4;
    public static final int EDIT_BOOLEAN=5;
    
    /** Adds property to the editor */
    public void addProperty(String s,BModel model,int type){names.add(s);models.add(model);editors.add(type);}
    
    
// EVENT HANDLING SUPPORT
    
    /** Sets up event listening. Should be called by the constructor!! */
    protected void initEventListening(){for(BModel m:models){m.addChangeListener(this);}}
    /**This should pass state changes to pcs. */
    public void stateChanged(ChangeEvent e){
        for(int i=0;i<models.size();i++){if(e.getSource()==models.get(i)){propertyChange(models.get(i).getChangeEvent(names.get(i)));}}
    }
    /** Utility class for handling bean property changes. */
    protected PropertyChangeSupport pcs=new PropertyChangeSupport(this);
    /**Add a property change listener for a specific property. */
    public void addPropertyChangeListener(PropertyChangeListener l){pcs.addPropertyChangeListener(l);}
    /**Remove a property change listener for a specific property. */
    public void removePropertyChangeListener(PropertyChangeListener l){pcs.removePropertyChangeListener(l);}    
    /** Handles property change events fired from a few properties */
    public void propertyChange(PropertyChangeEvent e) {pcs.firePropertyChange(e);}
    
    
// GUI HANDLING SUPPORT    
    
    /** Generates a combo box given a string list. */
    public ComboBoxModel getComboBoxModel(ComboBoxRangeModel cbrm){return new ComboBoxEditor(cbrm);}
    /** Generates a spinner model given a range model and a step size. */
    public SpinnerDoubleEditor getSpinnerModel(DoubleRangeModel drm,double step){return new SpinnerDoubleEditor(drm,step);}
    public SpinnerIntegerEditor getSpinnerModel(IntegerRangeModel irm,int step){return new SpinnerIntegerEditor(irm,step);}
    
    
// GUI METHODS
    
    /** Generates a JPanel with the settings contained herein */
    public JPanel getPanel(){
        JPanel result=new JPanel(new SpringLayout());
        for(int i=0;i<names.size();i++){
            result.add(new JLabel(names.get(i)));
            switch(editors.get(i)){
            case 0:result.add(new JSpinner(getSpinnerModel((DoubleRangeModel)models.get(i),.1)));break;
            case 1:result.add(new JSpinner(getSpinnerModel((IntegerRangeModel)models.get(i),1)));break;
            case 2:result.add(new JComboBox(getComboBoxModel((ComboBoxRangeModel)models.get(i))));break;
            case 3:result.add(new JTextField());break;
            case 4:result.add(new ColorEditor((ColorModel)models.get(i)).button);break;
            }
        }
        SpringUtilities.makeCompactGrid(result,names.size(),2,5,5,5,5);
        return result;
    }
}

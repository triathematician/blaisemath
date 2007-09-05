/*
 * PitchSettings.java
 * 
 * Created on Aug 30, 2007, 12:37:14 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package pursuitevasion;

import Model.DoubleRangeModel;
import Model.SpinnerDoubleEditor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import pursuitevasion.SimulationSettings;

/**
 *
 * @author ae3263
 */
public class PitchSettings implements ChangeListener {
    
// PROPERTIES   

    /** Default field size... for initial positions and window setting */
    private DoubleRangeModel size=new DoubleRangeModel(150,0,1000);
    
    
// CONSTRUCTORS
    
    /** Default constructor */
    public PitchSettings(){
        pcs=new PropertyChangeSupport(this);
        size.addChangeListener(this);
    }
    /** Constructs and adds support for change listening 
     * @param ss a SimulationSettings class */
    public PitchSettings(SimulationSettings ss){
        this();
        addPropertyChangeListener(ss);
    }
    
    
// GETTERS & SETTERS
    
    public double getSize(){return size.getValue();}
    
    public void setSize(int newValue){size.setValue(newValue);}

    
// EVENT HANDLING ROUTINES
    
    /** Utility class for handling bean property changes. */
    protected PropertyChangeSupport pcs;
    /**Add a property change listener for a specific property.
     * @param l the listener */
    public void addPropertyChangeListener(PropertyChangeListener l){pcs.addPropertyChangeListener(l);}
    /**Remove a property change listener for a specific property.
     * @param l the listener */
    public void removePropertyChangeListener(PropertyChangeListener l){pcs.removePropertyChangeListener(l);}    
    /** Handles change events by firing change events */
    public void stateChanged(ChangeEvent e) {
        if(e.getSource()==size){pcs.firePropertyChange("pitchSize",null,size.getValue());}
    }
    
    
// GUI ELEMENT GENERATING ROUTINES
// The routines below generate models for use with gui elements... these models also include step sizes for spinners */
    
    public SpinnerDoubleEditor getSizeSpinnerModel(){return new SpinnerDoubleEditor(size,5.0);}
    

// GENERATORS
    
    /** Generates a pitch class based on these settings. 
     * @return pitch class with these settings */
    public Pitch getPitch(){return new Pitch(this);}
}

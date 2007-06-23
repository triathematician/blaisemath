package PlanarAlgebra;

import Interface.BModel;

/**
 * <b>TLModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 12, 2007, 6:44 PM</i><br><br>
 * 
 * Model stores an element of the Temperley-Lieb algebra, and controls
 *   changes to such.
 */
public class TLModel extends BModel {
    private TLElement x;
    
    // Getters & Setters
    public TLElement getValue(){return new TLElement(x.getT());}
    public int getLength(){return x.getN();}
    public void setLength(int n){if(x.getN()!=n){setValue(new TLElement(n));}}
    public String getString(){return x.toString();}
    public String getLongString(){return x.toString();}
    public void setValue(String s){setValue(TLElement.valueOf(s));}    
    public int getNC(){return x.getNC();}
    public void setNC(int nc){if(getNC()!=nc){x.setNC(nc);fireStateChanged();}}
    public void addNC(int mc){if(mc!=0){x.addNC(mc);fireStateChanged();}}
    
    /** Class that performs all validity checking. */
    public void setValue(TLElement x){
        if(x.isValid()){
            this.x=x;
            fireStateChanged();
        }
    }
            
    /** Constructor: creates a new instance of TLModel as the identity. */
    public TLModel(){x=new TLElement();}
    /** Constructor: creates a new instance of TLModel as x */
    public TLModel(TLElement x){this.x=x;}
}

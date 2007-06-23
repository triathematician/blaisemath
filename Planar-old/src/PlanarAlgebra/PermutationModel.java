package PlanarAlgebra;

import Interface.BModel;

/**
 * <b>PermutationModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>March 8, 2007, 2:05 PM</i><br><br>
 *
 * Model storing an element of a permutation group. All change requests must be
 *   passed through this model.
 */
public class PermutationModel extends BModel {
    private PermutationElement x;
    
    // Getters & Setters
    public PermutationElement getValue(){return new PermutationElement(x.getP());}
    public int getLength(){return x.getN();}
    public String getShortString(){return x.toShortString();}
    public String getString(){return x.toString();}
    public String getLongString(){return x.toCycleString();}
    public void setValue(String s){setValue(PermutationElement.valueOf(s));}    
    
    /** Class that performs all validity checking. */
    public void setValue(PermutationElement x){
        if(x.isValid()){
            this.x=x;
            fireStateChanged();
        }
    }

            
    /** Constructor: creates a new instance of PermutationModel as the identity. */
    public PermutationModel(){x=new PermutationElement();}
    /** Constructor: creates a new instance of PermutationModel as x */
    public PermutationModel(PermutationElement x){this.x=x;}
}

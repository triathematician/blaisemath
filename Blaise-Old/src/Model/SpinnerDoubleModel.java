package Model;

import javax.swing.SpinnerNumberModel;

/**
 * <b>SpinnerIntModel.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 14, 2007, 1:48 PM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
class SpinnerDoubleModel extends SpinnerNumberModel{
    SpinnerDoubleModel(){super();}
    SpinnerDoubleModel(double initial,double first,double last,double step){
        super(initial,first,last,step);
    }
    public double getDoubleValue() {
        return (getNumber()).doubleValue();
    }
}

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
public class SpinnerIntModel extends SpinnerNumberModel{
    public SpinnerIntModel(){super();}
    public SpinnerIntModel(int initial,int first,int last,int step){
        super(initial,first,last,step);
    }
    public int getIntValue() {
        return (getNumber()).intValue();
    }
}

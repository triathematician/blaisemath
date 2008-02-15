/*
 * ComboRangeModel.java
 * Created on Sep 7, 2007, 1:55:21 PM
 */

package sequor.model;

/**
 * This class extends the standard combo box with an underlying IntegerRangeModel to handle
 * which property is selected.
 * <br><br>
 * @author Elisha Peterson
 */
public class ComboBoxRangeModel extends IntegerRangeModel{
    String[] s;
    public ComboBoxRangeModel(){}
    public ComboBoxRangeModel(String[] s,int newValue,int newMin,int newMax){this.s=s;setRangeProperties(newValue,newMin,newMax);}
    public String[] getStrings(){return s;}
    public String getString(int i){return s[i];}
    @Override
    public void setValue(String sv){
        for(int i=0;i<s.length;i++){
            if(sv.equals(s[i])){
                setValue(i);
            }
        }
    }
}

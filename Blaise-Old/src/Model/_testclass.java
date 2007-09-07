/*
 * NewClass.java
 * 
 * Created on Sep 7, 2007, 12:55:43 PM
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package Model;

import java.awt.Color;

public class _testclass extends Settings {
    private DoubleRangeModel sensorRange=new DoubleRangeModel(10,0,5000);
    private DoubleRangeModel commRange=new DoubleRangeModel(20,0,5000);
    private IntegerRangeModel topSpeed=new IntegerRangeModel(5,0,50);
    String[] behaviors={"A","B","Claustrophobia","D","E"};
    private ComboBoxRangeModel behavior=new ComboBoxRangeModel(behaviors,1,0,4);
    private ColorModel color=new ColorModel(Color.GREEN);
    
    public _testclass(){
        addProperty("Sensor Range",sensorRange,0);
        addProperty("Speed",topSpeed,1);
        addProperty("Behavior",behavior,2);
        addProperty("Comm Range",commRange,3);
        addProperty("Color",color,4);
        initEventListening();
    }
}

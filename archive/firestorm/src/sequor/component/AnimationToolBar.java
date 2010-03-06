/*
 * AnimationToolBar.java
 * Created on Nov 13, 2007, 8:46:58 AM
 */

package sequor.component;

import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * ToolBar button collection for animating an underlying RangeTimer.
 * 
 * @author ae3263
 */
public class AnimationToolBar extends JToolBar {    
    RangeTimer timer;
    
    /** Default constructs a new timer. */
    public AnimationToolBar(){
        this(new RangeTimer());
    }
    
    /** Construct to control an underlying timer. */
    public AnimationToolBar(RangeTimer timer){
        add(getButton("Restart","restart"));
        add(getButton("Stop","stop"));
        add(getButton("Pause","pause"));
        add(getButton("Slow","slower"));
        add(getButton("Play","play"));
        add(getButton("Fast","faster"));
    }
    
    public JButton getButton(String s,String ac){
        JButton adder=new JButton(s);
        adder.setActionCommand(ac);
        adder.addActionListener(timer);
        return adder;
    }
}

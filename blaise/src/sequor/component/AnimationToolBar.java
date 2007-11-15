/*
 * AnimationToolBar.java
 * Created on Nov 13, 2007, 8:46:58 AM
 */

package sequor.component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 * ToolBar button collection for animating an underlying RangeTimer.
 * <br><br>
 * @author ae3263
 */
public class AnimationToolBar extends JToolBar implements ActionListener {
    
    RangeTimer timer;
    
    /** Default constructs a new timer. */
    public AnimationToolBar(){
        this(new RangeTimer());
    }
    
    /** Construct to control an underlying timer. */
    public AnimationToolBar(RangeTimer timer){
        addButton("Reverse");
        addButton("Stop");
        addButton("Pause");
        addButton("Slow");
        addButton("Play");
        addButton("Fast");
    }
    
    public void addButton(String s){
        JButton adder=new JButton(s);
        adder.addActionListener(this);
        add(adder);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String s=e.getActionCommand();
        if(s.equals("Reverse")){   
            //timer.reverse();
        }else if(s.equals("Stop")){
            timer.stop();
        }else if(s.equals("Pause")){
            timer.pause();
        }else if(s.equals("Slow")){
            timer.slower();
        }else if(s.equals("Play")){
            timer.start();
        }else if(s.equals("Fast")){
            timer.faster();
        }
    }
}

/**
 * TimerBar.java
 * Created on Nov 24, 2009
 */

package org.bm.blaise.sequor.timer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

/**
 * <p>
 *    This class is a toolbar with controls for a timer.
 * </p>
 * @author Elisha Peterson
 */
public class TimerBar extends JToolBar implements ActionListener {

    BetterTimeClock timer;
    JLabel time;

    public TimerBar() {
    }

    public TimerBar(BetterTimeClock timer) {
        setTimer(timer);
    }

    private void initComponents() {
        removeAll();
        add(new JButton(timer.playAction));
        add(new JButton(timer.pauseAction));
        add(new JButton(timer.stopAction));
        add(new JButton(timer.slowDownAction));
        add(new JButton(timer.speedUpAction));
        add(new JButton(timer.restartAction));
        add(time = new JLabel("Time = " + timer.getTime()));
    }

    public StandardTimer getTimer() {
        return timer;
    }

    public void setTimer(BetterTimeClock timer) {
        if (timer != null) {
            if (this.timer != null) {
                this.timer.removeActionListener(this);
            }
            this.timer = timer;
            timer.addActionListener(this);
            initComponents();
        }
    }
    

    public void actionPerformed(ActionEvent e) {
        time.setText("Time = " + timer.getTime());
    }
}

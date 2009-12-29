/**
 * TimerBar.java
 * Created on Nov 24, 2009
 */

package org.bm.blaise.sequor.component;

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

    StandardTimer timer;

    public TimerBar() {
        initComponents();
        setTimer(null);
    }

    public TimerBar(StandardTimer timer) {
        initComponents();
        setTimer(timer);
    }

    private void initComponents() {
        add(play);
        add(pause);
        add(stop);
        add(speedUp);
        add(slowDown);
        add(restart);
        add(time);
        play.addActionListener(this);
        pause.addActionListener(this);
        stop.addActionListener(this);
        speedUp.addActionListener(this);
        slowDown.addActionListener(this);
        restart.addActionListener(this);
    }

    public StandardTimer getTimer() {
        return timer;
    }

    public void setTimer(StandardTimer timer) {
        if (timer != null) {
            if (this.timer != null) {
                this.timer.removeActionListener(this);
            }
            this.timer = timer;
            timer.addActionListener(this);
        }
        updateButtons();
    }

    /** Updates enabled status of the buttons based on current status of the timer. */
    void updateButtons() {
        if (timer != null) {
            play.setEnabled( !timer.isPlaying() );
            pause.setEnabled( !timer.isStopped() );
            speedUp.setEnabled( !timer.isStopped() );
            slowDown.setEnabled( !timer.isStopped() );
            stop.setEnabled( !timer.isStopped() );
            restart.setEnabled( true );
        } else {
            play.setEnabled(false);
            pause.setEnabled(false);
            stop.setEnabled(false);
            speedUp.setEnabled(false);
            slowDown.setEnabled(false);
            restart.setEnabled(false);
        }
    }

    JButton play = new JButton("Play");
    JButton pause = new JButton("Pause");
    JButton stop = new JButton("Stop");
    JButton speedUp = new JButton("Faster");
    JButton slowDown = new JButton("Slower");
    JButton restart = new JButton("Restart");
    JLabel time = new JLabel("Time = N/A");

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Play")) {
            timer.play();
        } else if (e.getActionCommand().equals("Pause")) {
            timer.pause();
        } else if (e.getActionCommand().equals("Stop")) {
            timer.stop();
        } else if (e.getActionCommand().equals("Faster")) {
            timer.speedUp();
        } else if (e.getActionCommand().equals("Slower")) {
            timer.slowDown();
        } else if (e.getActionCommand().equals("Restart")) {
            timer.restart();
        }
        updateButtons();
        if (timer instanceof ClockTimer) {
            time.setText("Time = " + ((ClockTimer)timer).getTime());
        }
    }
}

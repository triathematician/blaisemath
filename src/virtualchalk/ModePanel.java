/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package virtualchalk;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Panel contains buttons whose positions can be adjusted by dragging on the panel.
 *
 * @author ae3263
 */
public class ModePanel extends JPanel
        implements ActionListener, MouseListener, MouseMotionListener {

    public enum InputMode { ALPHA, GRAPH, CALC; }

    InputMode mode = InputMode.ALPHA;

    final int GAP = 8;
    final int SZ = 48;

    JToggleButton alphaButton = new JToggleButton("Abc");
    JToggleButton graphButton = new JToggleButton("GR");
    JToggleButton eqButton = new JToggleButton("$$");
    ButtonGroup gp = new ButtonGroup();

    public ModePanel() {
        setLayout(null);
        add(alphaButton); gp.add(alphaButton);
        add(graphButton); gp.add(graphButton);
        add(eqButton); gp.add(eqButton);
        setVert(32);
        addMouseListener(this);
        addMouseMotionListener(this);
        setOpaque(false);
        alphaButton.addActionListener(this);
        alphaButton.setSelected(true);
        graphButton.addActionListener(this);
        eqButton.addActionListener(this);
    }

    public InputMode getMode() {
        return mode;
    }

    public void setMode(InputMode mode) {
        this.mode = mode;
        switch (mode) {
            case ALPHA: alphaButton.setSelected(true); break;
            case GRAPH: graphButton.setSelected(true); break;
            case CALC: eqButton.setSelected(true); break;
        }
    }
    
    private void setVert(int y0) {
        y0 = Math.max(0, y0);
        if (getHeight() > 0)
            y0 = Math.min(y0, getHeight() - 3*SZ - 2*GAP);
        alphaButton.setBounds(0, y0, SZ, SZ);
        graphButton.setBounds(0, y0+SZ+GAP, SZ, SZ);
        eqButton.setBounds(0, y0+2*SZ+2*GAP, SZ, SZ);
    }

    public void actionPerformed(ActionEvent e) {
        if (gp.getSelection() == alphaButton)
            mode = InputMode.ALPHA;
        else if (gp.getSelection() == graphButton)
            mode = InputMode.GRAPH;
        else
            mode = InputMode.CALC;
    }


    Point start;
    int v0;

    public void mousePressed(MouseEvent e) { start = e.getPoint(); v0 = alphaButton.getY(); }
    public void mouseDragged(MouseEvent e) {
        if (start != null) {
            setVert(v0 + e.getY() - start.y);
        }
    }
    public void mouseReleased(MouseEvent e) { start = null; }

    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

}

/**
 * BlaisePagePanel.java
 * Created on Dec 21, 2009
 */

package org.bm.blaise.scribo.page;

import gui.RollupPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import org.bm.blaise.sequor.timer.BetterTimeClock;
import org.bm.blaise.specto.space.SpacePlotComponent;
import org.bm.blaise.specto.visometry.PlotComponent;

/**
 * <p>
 *    This class is used to display a BlaisePage.
 * </p>
 * @author Elisha Peterson
 */
public class BlaisePagePanel extends JPanel implements ActionListener, java.io.Serializable {

    final static Font TITLE_FONT = new Font("Default", Font.BOLD, 18);
    final static Font MAIN_FONT = new Font("Default", Font.PLAIN, 14);
    final static Font DESCRIP_FONT = new Font("Default", Font.ITALIC, 12);

    /** Stores the BlaisePage to display. */
    BlaisePage page;
    /** Stores the active plot. */
    transient PlotComponent activePlot;
    /** Stores the timer associated with the active plot. */
    transient BetterTimeClock activeTimer;

    public BlaisePagePanel() {
        this(new SampleBlaisePage());
    }

    public BlaisePagePanel(BlaisePage page) {
        setPage(page);
    }

    public BlaisePage getPage() {
        return page;
    }

    public void setPage(BlaisePage page) {
        if (page != null) {
            this.page = page;
            initComponents();
            fireActionPerformed(new ActionEvent(this, 0, "PLOT CHANGE"));
        }
    }

    /** Everything above the plot. */
    JPanel abovePlot;
    /** Title bar. */
    JPanel titlePanel;
    /** Main text element. */
    JTextPane mainTextArea;
    /** Toolbar. */
    PlotBar plotBar;
    /** Plot panel. */
    JComponent plotPanel;
    /** Settings panel. */
    RollupPanel settingsPanel;

    private void initComponents() {
        removeAll();
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(300, 500));
        setPreferredSize(new Dimension(500, 700));

        // plot area
        plotPanel = new JTabbedPane();
//        plotPanel.setLayout(new BoxLayout(plotPanel, BoxLayout.LINE_AXIS));
//        plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        for (int i = 0; i < page.getPlot().length; i++) {
            plotPanel.add(page.getPlot(i));
            ((JTabbedPane)plotPanel).add(page.getPlot(i).getTitle(), page.getPlot(i));
        }
        if (page.getPlot().length > 0) {
            activePlot = page.getPlot(0);
            activeTimer = (BetterTimeClock) activePlot.getTimeClock();
        } else {
            activePlot = null;
            activeTimer = null;
        }
        ((JTabbedPane)plotPanel).addChangeListener(new ChangeListener(){
            public void stateChanged(ChangeEvent e) {
                page.setActivePlot((PlotComponent) ((JTabbedPane)plotPanel).getSelectedComponent());
                plotBar.update();
                fireActionPerformed(new ActionEvent(this, 0, "PLOT CHANGE"));
            }
        });
//        if (page.getActivePlot() != null)
//            plotPanel.add(page.getActivePlot());
        plotPanel.setMinimumSize(new Dimension(200, 200));
        plotPanel.setPreferredSize(new Dimension(500, 500));
        plotPanel.setToolTipText("These are the plots associated with the BlaisePage.");
        add(plotPanel, BorderLayout.CENTER);

        // above the plot
        
        titlePanel = new TitleArea();

        mainTextArea = new JTextPane();
        mainTextArea.setFont(MAIN_FONT);
        mainTextArea.setText(page.getText());
        mainTextArea.setEditable(false);
        mainTextArea.setToolTipText("This is where the text associated with the plot goes.");
        mainTextArea.setBorder(null);
        JScrollPane scrollPane = new JScrollPane(mainTextArea);
        scrollPane.setBorder(null);

        plotBar = new PlotBar();
        
        abovePlot = new JPanel(new BorderLayout());
        abovePlot.add(titlePanel, BorderLayout.NORTH);
        abovePlot.add(scrollPane, BorderLayout.CENTER);
        abovePlot.add(plotBar, BorderLayout.SOUTH);
        abovePlot.setMinimumSize(new Dimension(200, 200));
        abovePlot.setPreferredSize(new Dimension(200, 200));
        add(abovePlot, BorderLayout.NORTH);

        // settings panel (below the plot)

//        settingsPanel = new RollupPanel();
//        settingsPanel.removeAll();
//        if (page.getActivePlot() != null) {
//            settingsPanel.add("Visometry", new PropertySheet(page.getActivePlot().getVisometry()));
//            settingsPanel.setToolTipText("This is for basic settings associated with the BlaisePage.");
//        }
//        add(new JScrollPane(settingsPanel), BorderLayout.SOUTH);

        validate();
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("3D")) {
            if (page.getActivePlot() != null && page.getActivePlot() instanceof SpacePlotComponent) {
                // toggle anaglyph setting
                SpacePlotComponent spc = (SpacePlotComponent) page.getActivePlot();
                spc.setAnaglyph( !spc.isAnaglyph() );
            }
        } else if (e.getActionCommand().equals("PREVIOUS PAGE")) {
            setPage(page.getPreviousPage());
        } else if (e.getActionCommand().equals("NEXT PAGE")) {
            setPage(page.getNextPage());
        }
    }


    //
    // EVENT HANDLING
    //

    protected EventListenerList actionListenerList = new EventListenerList();

    /**
     * Removes a listener from the list of classes receiving <code>ActionEvent</code>s
     * @param l the listener
     */
    public void addActionListener(ActionListener l) {
        actionListenerList.add(ActionListener.class, l);
    }

    /**
     * Adds a listener to receive <code>ActionEvent</code>s
     * @param l the listener
     */
    public void removeActionListener(ActionListener l) {
        actionListenerList.remove(ActionListener.class, l);
    }

    /**
     * Fires the change event to listeners.
     */
    protected void fireActionPerformed(ActionEvent e) {
        Object[] listeners = actionListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                if (e == null) {
                    return;
                }
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }

    //
    // INNER CLASSES
    //

    /** The title area. */
    class TitleArea extends JPanel {
        JLabel titleLabel;
        JTextPane descriptionArea;
        JToolBar navBar;

        TitleArea() {
            super(new BorderLayout());
            titleLabel = new JLabel(page.getTitle());
            titleLabel.setFont(TITLE_FONT);
            titleLabel.setToolTipText("This is the title of the BlaisePage.");
            add(titleLabel, BorderLayout.CENTER);

            if (page.getDescription() != null) {
                descriptionArea = new JTextPane();
                descriptionArea.setText(page.getDescription());
                descriptionArea.setBorder(null);
                descriptionArea.setEditable(false);
                descriptionArea.setBackground(new Color(220, 220, 220));
                descriptionArea.setFont(DESCRIP_FONT);
                descriptionArea.setToolTipText("This is a brief summary of the BlaisePage.");
                JScrollPane sp = new JScrollPane(descriptionArea);
                sp.setBorder(null);
                add(sp, BorderLayout.SOUTH);
            }

            navBar = new NavBar();
            add(navBar, BorderLayout.EAST);
            validate();
        }
    }

    /** The navigation toolbar. */
    class NavBar extends JToolBar {
        JButton prevButton, nextButton;
        NavBar() {
            super();
            prevButton = new JButton("<");
            nextButton = new JButton(">");
            if (page.getPreviousPage() == null) {
                prevButton.setEnabled(false);
            } else {
                prevButton.setActionCommand("PREVIOUS PAGE");
                prevButton.addActionListener(BlaisePagePanel.this);
                prevButton.setToolTipText("Go to previous page (" + page.getPreviousPage().getTitle()+")");
            }
            if (page.getNextPage() == null) {
                nextButton.setEnabled(false);
            } else {
                nextButton.setActionCommand("NEXT PAGE");
                nextButton.addActionListener(BlaisePagePanel.this);
                nextButton.setToolTipText("Go to next page (" + page.getNextPage().getTitle()+")");
            }
            add(prevButton);
            add(nextButton);
            setBorderPainted(false);
            setRollover(true);
            setFloatable(false);
            setToolTipText("This toolbar is for navigation between multiple BlaisePages.");
        }
    }

    /** The plot toolbar. */
    class PlotBar extends JToolBar {
        JButton play;
        JButton pause;
        JButton stop;
        JButton anaglyph;
        PlotBar() {
            add(play = new JButton(activeTimer.getPlayAction()));
            add(pause = new JButton(activeTimer.getPauseAction()));
            add(stop = new JButton(activeTimer.getStopAction()));
            anaglyph = new JButton("3D View");
                anaglyph.setActionCommand("3D");
                anaglyph.addActionListener(BlaisePagePanel.this);
                add(anaglyph);
            setBorderPainted(false);
            setRollover(true);
            setToolTipText("This toolbar is for actions associated with the plot(s) below.");
        }
        void update() {
            play.setAction(activeTimer.getPlayAction());
            pause.setAction(activeTimer.getPauseAction());
            stop.setAction(activeTimer.getStopAction());
        }
    }
}

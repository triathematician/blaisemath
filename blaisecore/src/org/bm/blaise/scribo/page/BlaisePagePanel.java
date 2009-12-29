/**
 * BlaisePagePanel.java
 * Created on Dec 21, 2009
 */

package org.bm.blaise.scribo.page;

import data.propertysheet.PropertySheet;
import gui.RollupPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;

/**
 * <p>
 *    This class is used to display a BlaisePage.
 * </p>
 * @author Elisha Peterson
 */
public class BlaisePagePanel extends JPanel implements java.io.Serializable {

    /** Stores the BlaisePage to display. */
    BlaisePage page;

    public BlaisePagePanel() {
        this(new SampleBlaisePage());
    }

    public BlaisePagePanel(BlaisePage page) {
        this.page = page;
        initComponents();
    }

    /** Everything above the plot. */
    JPanel abovePlot;
    /** Title bar. */
    JPanel titlePanel;
    /** Main text element. */
    JTextPane mainTextArea;
    /** Toolbar. */
    JToolBar plotBar;
    /** Plot panel. */
    JPanel plotPanel;
    /** Settings panel. */
    RollupPanel settingsPanel;

    private void initComponents() {
        setLayout(new BorderLayout());

        // title area
        titlePanel = new TitleArea();

        // text area
        mainTextArea = new JTextPane();
        mainTextArea.setText(page.getText());
        mainTextArea.setEditable(false);
        mainTextArea.setMinimumSize(new Dimension(200, 200));
        mainTextArea.setToolTipText("This is where the text associated with the plot goes.");

        // above the plot
        abovePlot = new JPanel(new BorderLayout());
        abovePlot.add(titlePanel, BorderLayout.NORTH);
        abovePlot.add(new JScrollPane(mainTextArea), BorderLayout.CENTER);
        plotBar = new PlotBar();
        abovePlot.add(plotBar, BorderLayout.SOUTH);
        add(abovePlot, BorderLayout.NORTH);
        abovePlot.validate();

        // plot area
        plotPanel = new JPanel();
        plotPanel.setLayout(new BoxLayout(plotPanel, BoxLayout.LINE_AXIS));
        plotPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        plotPanel.add(page.getActivePlot());
        plotPanel.setMinimumSize(new Dimension(200, 200));
        plotPanel.setPreferredSize(new Dimension(800, 600));
        plotPanel.setToolTipText("These are the plots associated with the BlaisePage.");
        add(plotPanel, BorderLayout.CENTER);

        // settings panel
        settingsPanel = new RollupPanel();
        settingsPanel.removeAll();
        settingsPanel.add("Visometry", new PropertySheet(page.getActivePlot().getVisometry()));
        settingsPanel.setToolTipText("This is for basic settings associated with the BlaisePage.");
        add(new JScrollPane(settingsPanel), BorderLayout.SOUTH);

        validate();
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
            titleLabel.setFont(new Font("Default", Font.BOLD, 18));
            titleLabel.setToolTipText("This is the title of the BlaisePage.");
            add(titleLabel, BorderLayout.CENTER);

            if (page.getDescription() != null) {
                descriptionArea = new JTextPane();
                descriptionArea.setText(page.getDescription());
                descriptionArea.setBorder(null);
                descriptionArea.setEditable(false);
                descriptionArea.setBackground(new Color(220, 220, 220));
                descriptionArea.setFont(new Font("Default", Font.ITALIC, 10));
                descriptionArea.setToolTipText("This is a brief summary of the BlaisePage.");
                JScrollPane sp = new JScrollPane(descriptionArea);
//                sp.setBorder(null);
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
            if (page.getPreviousPage() == null)
                prevButton.setEnabled(false);
            if (page.getNextPage() == null)
                nextButton.setEnabled(false);
            // TODO - add actions
            add(prevButton);
            add(nextButton);
            setBorderPainted(false);
            setRollover(true);
            setToolTipText("This toolbar is for navigation between multiple BlaisePages.");
        }
    }

    /** The plot toolbar. */
    class PlotBar extends JToolBar {
        JButton playButton;
        PlotBar() {
            playButton = new JButton("PLAY");
            add(playButton);
            setBorderPainted(false);
            setRollover(true);
            setToolTipText("This toolbar is for actions associated with the plot(s) below.");
        }
    }
}

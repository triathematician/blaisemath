package gui;

import Blaise.BPlot2D;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import pursuitevasion.Simulation;
import pursuitevasion.SimulationSettings;

/**
 * <b>PEA.java</b><br>
 * Author: <i>Elisha Peterson</i><br>
 * Created on <i>June 18, 2007, 10:45 AM</i><br><br>
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class PEA {
    
    /** Major components */
    private static Simulation sim;
    private static StatusArea statusArea;
    private static PlotArea plotArea;
    
    /** START main method */
    public static void main(String args[]){
        try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch(UnsupportedLookAndFeelException e){} catch (ClassNotFoundException e){} catch (InstantiationException e){} catch (IllegalAccessException e){}
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable(){public void run(){createModels();createAndShowGUI();}});
    }
    
    /** Creates the options/models to be used in the program. */
    private static void createModels(){
        sim=new Simulation(SimulationSettings.LOTS_OF_FUN);
        sim.run(100);
    }
    
    /** Create and show the GUI */
    private static void createAndShowGUI(){
        //Create and set up the window.
        JFrame frame = new JFrame("Pursuit-Evasion Games");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600,400));
        
        //Set up the content pane.
        (new PEA()).buildUI(frame.getContentPane());
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    /** GUI constructor */
    private void buildUI(Container container){
        plotArea=new PlotArea(sim);
        container.add(plotArea,BorderLayout.CENTER);
        statusArea=new StatusArea(sim);
        container.add(statusArea,BorderLayout.PAGE_END);
    }
    
    /**
     * <b>PEA.PlotArea</b> class<br>
     * Contains a BPlot2D object for displaying initial conditions and solution curves.
     * Listens for events and redraws as appropriate.
     */
    public static class PlotArea extends BPlot2D implements ActionListener,PropertyChangeListener {
        Simulation sim;
        public PlotArea(Simulation sim){
            super(new Dimension(600,601),sim.ss.getPitchSize());
            this.setBackground(Color.WHITE);
            this.sim=sim;
            sim.addActionListener(this);
            initPoints();
            setAnimateCycle(sim.ss.getNumSteps()+10);
        }
        public void initPoints(){
            deletePoints();
            addPoints(sim.getInitialPoints());
            addPlotPaths(sim.getComputedPaths());
        }
        public void propertyChange(PropertyChangeEvent e){
            if(e.getPropertyName()=="numPursuers"||e.getPropertyName()=="numEvaders"){initPoints();}
            else if(e.getPropertyName()=="fieldSize"){super.setBounds(sim.ss.getPitchSize());}
            else if(e.getPropertyName()=="pursueColor"){initPoints();}
            else if(e.getPropertyName()=="evadeColor"){initPoints();}
            else if(e.getPropertyName()=="numSteps"){setAnimateCycle(sim.ss.getNumSteps()+10);}
        }
        public void actionPerformed(ActionEvent e){refresh();}
        public void refresh(){
            deleteFixedPoints();
            deletePaths();
            addPlotPaths(sim.getComputedPaths());
            repaint();
        }
    }
    
    /**
     * <B>PEA.StatusArea</B> class<br>
     * Supports displaying a status line and buttons for the plot window
     */
    public static class StatusArea extends JPanel implements ActionListener {
        Simulation sim;
        private JLabel statusLine;
        StatusArea(Simulation sim){
            super();
            this.sim=sim;
            sim.addActionListener(this);
            setBorder(BorderFactory.createLineBorder(Color.black));
            setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
            statusLine=new JLabel("Impatient to Pursue!");
            setBackground(Color.BLACK);
            statusLine.setFont(new java.awt.Font("Courier New", 0, 14));
            statusLine.setForeground(new Color(128,255,128));
            add(statusLine);
            add(Box.createHorizontalGlue());
        }
        public void setStatus(String s){statusLine.setText(s);}
        public void actionPerformed(ActionEvent e){
            if(e.getSource()==sim){setStatus("Capture Times: ");repaint();}
        }
    }
    /* END StatusArea class */
}
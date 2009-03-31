/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import metrics.CaptureCondition;
import metrics.Valuation;
import metrics.VictoryCondition;
import sequor.Settings;
import sequor.component.SettingsTreePanel;
import simulation.Agent;
import simulation.Simulation;
import simulation.Team;
import tasking.Tasking;

/**
 *
 * @author ae3263
 */
public class SimulationSettingsPanel extends SettingsTreePanel implements ActionListener {
    
    Simulation sim;
    
    /** Default constructor */
    public SimulationSettingsPanel(){}
    
    /** Construct with sim. */
    public SimulationSettingsPanel(Simulation sim){
        this();
        setSim(sim);
        initPopup();
    }
    
    /** Constructs with settings */
    public SimulationSettingsPanel(Settings s){super(s);initPopup();}

    @Override
    protected void initPopup() {
        popup = new JPopupMenu();
        JMenuItem mi = new JMenuItem("Insert child");
        mi.addActionListener(this);
        mi.setActionCommand("insert");
        popup.add(mi);
        mi = new JMenuItem("Remove node");
        mi.addActionListener(this);
        mi.setActionCommand("remove");
        popup.add(mi);
        popup.setOpaque(true);
        popup.setLightWeightPopupEnabled(true);
        super.initPopup();
    }

    /** Sets simulation */
    public void setSim(Simulation sim){
        if(sim == null) { return; }
        if(this.sim != null) { this.sim.removeActionListener(this); }
        this.sim = sim;
        setName(sim.getName());
        sim.addActionListener(this);
        setTree(sim.ss);
        initPopup();
    }
    public Simulation getSim(){return sim;}

    /** Returns object underlying a selected element: either the Simulation or the Agent. */
    public Object getSelectedAdder() {
        if (tree.getSelectionPath()==null) { return null; }
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
        Settings sParent = (Settings) parent.getUserObject();
        if (sParent instanceof Simulation.SimSettings) {
            return sim;
        } else if (sParent instanceof Team.TeamSettings) {
            return sim.getTeam(((Team.TeamSettings)sParent).getName());
        } else {
            parent=(DefaultMutableTreeNode) tree.getSelectionPath().getPathComponent(1);
            sParent = (Settings) parent.getUserObject();
            return sim.getTeam(((Team.TeamSettings)sParent).getName());
        }
    }

    /** Rebuilds tree when action is fired from Simulation. */
    public void actionPerformed(ActionEvent e) {
        String ac = e.getActionCommand();
        if(e.getSource().equals(sim)){
            if (ac.equals("reset")) { setTree(sim.ss); }
        } else if (ac.equals("insert") && tree.getSelectionPath()!=null) {
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            Settings sParent = (Settings) parent.getUserObject();
            Team team = (sParent instanceof Simulation.SimSettings) ? null : (Team) getSelectedAdder();
            if (sParent instanceof Simulation.SimSettings) {
                sim.addTeam(new Team("new team"));
            } else if (sParent instanceof Team.TeamSettings || sParent instanceof Agent.AgentSettings || sParent.getName().equals("Agents")) {
                team.setAgentNumber(team.getAgentNumber()+1);
            } else if (sParent instanceof Tasking.TaskSettings || sParent.getName().equals("Taskings")) {
                team.addTasking(new Tasking());
            } else if (sParent instanceof VictoryCondition.VictorySettings) {
                if (team.victory == null) { team.setVictoryCondition(new VictoryCondition()); }
            } else if (sParent.getName().equals("Capture Condition")) {
                team.addCaptureCondition(new CaptureCondition());
            } else if (sParent instanceof Valuation.ValuationSettings || sParent.getName().equals("Metrics")) {
                team.addValuation(new Valuation());
            }
            sim.update();
        } else if (ac.equals("remove") && tree.getSelectionPath()!=null) {
            DefaultMutableTreeNode parent,node;
            node = (DefaultMutableTreeNode) tree.getSelectionPath().getLastPathComponent();
            parent = (DefaultMutableTreeNode) node.getParent();
            int nodeIndex=parent.getIndex(node);
            node.removeAllChildren();         
            if (nodeIndex != -1) { parent.remove(nodeIndex); }
            ((DefaultTreeModel )tree.getModel()).nodeStructureChanged((TreeNode)node);
            sim.update();
        }
    }
    
    /** Overrides default tree construction method to include custom rendering cabability. */
    @Override
    public void setTree(Settings s){
        super.setTree(s);
        tree.setCellRenderer(new MyTreeCellRenderer(createImageIcon("images/teamicon.gif"),createImageIcon("images/teamicon2.gif"),createImageIcon("images/agenticon.gif")));
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = PEGPlot.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    } 
    
    static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
        private Icon teamIcon=null;
        private Icon teamIcon2=null;
        private Icon agentIcon=null;
        MyTreeCellRenderer(){super();}
        MyTreeCellRenderer(Icon t,Icon t2,Icon a){
            super();
            teamIcon=t;teamIcon2=t2;agentIcon=a;
            if(t!=null){this.setOpenIcon(t);}
            if(t2!=null){this.setClosedIcon(t2);}
            if(a!=null){setLeafIcon(a);}
        }
        private DefaultMutableTreeNode curNode=null;
        //public Icon getLeafIcon(){return (agentIcon==null)?agentIcon:super.getLeafIcon();}
        @Override
        public Color getTextSelectionColor(){
            if(curNode!=null){
                return ((Settings)curNode.getUserObject()).getColor();
            }
            return super.getTextSelectionColor();
        }
        @Override
        public Color getTextNonSelectionColor() {
            if(curNode!=null){
                return ((Settings)curNode.getUserObject()).getColor();
            }
            return super.getTextNonSelectionColor();
        }
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus){
            if(value instanceof DefaultMutableTreeNode){curNode=(DefaultMutableTreeNode)value;}
            return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }
    }
}

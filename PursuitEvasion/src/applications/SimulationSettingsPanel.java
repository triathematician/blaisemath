/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package applications;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import sequor.Settings;
import sequor.component.SettingsTreePanel;

/**
 *
 * @author ae3263
 */
public class SimulationSettingsPanel extends SettingsTreePanel {
    
    /** Default constructor */
    public SimulationSettingsPanel(){super();}
    
    /** Instructor with settings */
    public SimulationSettingsPanel(Settings s){super(s);}
    
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
        MyTreeCellRenderer(){super();}
        MyTreeCellRenderer(Icon t,Icon t2,Icon a){
            super();
            teamIcon=t;teamIcon2=t2;agentIcon=a;
            if(t!=null){this.setOpenIcon(t);}
            if(t2!=null){this.setClosedIcon(t2);}
            if(a!=null){setLeafIcon(a);}
        }
        private Icon teamIcon=null;
        private Icon teamIcon2=null;
        private Icon agentIcon=null;
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

/*
 * SettingsPanel.java
 * Created on Feb 15, 2008
 */

package sequor.component;

import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;

/**
 * SettingsPanel is designed to streamline the process of creating a panel for editing several
 * settings by setting up a tree in one pane and the settings in another pane of a split window.
 * 
 * @author Elisha Peterson
 */
public class SettingsPanel extends JSplitPane{
    
    JScrollPane pane1;
    JScrollPane pane2;
    JTree tree;
    
    public SettingsPanel(){
        super();
        initComponents();  
        
        // default settings for the windows
        pane1.setViewportView(new JTree());
        setLeftComponent(pane1);
        setRightComponent(pane2);     
    }
    
    public SettingsPanel(Settings s){
        super();
        initComponents();        
        setTree(s);
        pane1.setViewportView(tree);
        pane2.setViewportView(s.getPanel());
        setLeftComponent(pane1);
        setRightComponent(pane2);
    }
    
    private void initComponents(){
        // default settings for the panel
        setDividerLocation(130);
        setResizeWeight(0.5);
        setMaximumSize(new Dimension(1000,300));
        setContinuousLayout(true);
        setOneTouchExpandable(true);    
        JScrollPane pane1=new JScrollPane();  
        pane1.setMinimumSize(new Dimension(100,23));
        JScrollPane pane2=new JScrollPane();
        pane2.setMinimumSize(new Dimension(100,23));
    }
    
    /** Constructs the tree based on the settings class. */
    private void setTree(Settings s){
        tree=new JTree(s.getTreeModel());
    }
}

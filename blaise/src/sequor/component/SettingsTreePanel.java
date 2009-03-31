/*
 * SettingsTreePanel.java
 * Created on Feb 15, 2008
 */

package sequor.component;

import sequor.Settings;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * SettingsTreePanel is designed to streamline the process of creating a panel for editing several
 * settings by setting up a tree in one pane and the settings in another pane of a split window.
 * 
 * @author Elisha Peterson
 */
public class SettingsTreePanel extends JSplitPane{
    
    protected JScrollPane pane1;
    protected JScrollPane pane2;
    protected JTree tree;
    protected JPopupMenu popup;
    
    public SettingsTreePanel(){
        super();
        initComponents();  
        
        // default settings for the windows
        pane1.setViewportView(new JTree());
        setLeftComponent(pane1);
        setRightComponent(pane2);
        initPopup();
    }
    
    public SettingsTreePanel(Settings s){
        super();
        initComponents();        
        setTree(s);
        pane2.setViewportView(s.getPanel());
        setLeftComponent(pane1);
        setRightComponent(pane2);
        initPopup();
    }

    private void initComponents(){
        // default settings for the panel
        setDividerLocation(130);
        setResizeWeight(0.5);
        setPreferredSize(new Dimension(400,500));
        setMaximumSize(new Dimension(400,1000));
        setContinuousLayout(true);
        setOneTouchExpandable(true);  
        pane1=new JScrollPane();  
        pane1.setMinimumSize(new Dimension(100,23));
        pane2=new JScrollPane();
        pane2.setMinimumSize(new Dimension(200,23));
    }

    protected void initPopup(){
        if (popup!=null && tree!=null) {
            tree.addMouseListener( new MouseAdapter() {
                @Override public void mouseReleased( MouseEvent e ) {
                    if ( e.isPopupTrigger()) { popup.show( (JComponent)e.getSource(), e.getX(), e.getY() ); }
                }
            });
        }
    }
    
    /** Constructs the tree based on the settings class. */
    protected void setTree(Settings s){
        tree=new JTree(s.getTreeModel());
        tree.addTreeSelectionListener(new TreeSelectionListener(){
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(node==null){return;}
                Settings nodeObject=(Settings)node.getUserObject();
                pane2.setViewportView(nodeObject.getPanel());
            }
        });
        pane1.setViewportView(tree);
        initPopup();
    }
}

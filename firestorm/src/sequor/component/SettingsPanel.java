/**
 * SettingsPanel.java
 * Created on Mar 11, 2008
 */

package sequor.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import sequor.Settings;
import sequor.SettingsFactory;
import sequor.SettingsProperty;

/**
 * A JPanel with several editors determined by a particular Settings class.
 * 
 * @author Elisha Peterson
 */
public class SettingsPanel extends JPanel {
    /** Underlying data. */
    Settings s;
    /** Display type. */
    int displayType;
    /** Display as a table. */
    public static final int DISPLAY_TABLE=0;
    /** Display using spinners. */
    public static final int DISPLAY_SPINNERS=1;
    /** Display using sliders. */
    public static final int DISPLAY_SLIDERS=2;
    
    public SettingsPanel(){
        s=new Settings();
        this.displayType=DISPLAY_SPINNERS;
        updatePanel();
    }
    public SettingsPanel(Settings s){this(s,DISPLAY_SPINNERS);}
    public SettingsPanel(Settings s,int displayType){
        this.s=s;
        this.displayType=displayType;
        updatePanel();
    }

    public void setSettings(Settings s) {this.s=s;updatePanel();}
    public Settings getSettings(){return s;}
    public int getDisplayType(){return displayType;}
    public void setDisplayType(int displayType){this.displayType=displayType;updatePanel();}
    
    /** Places the settings on a given panel, and performs proper layout. */
    public void updatePanel(){
        if(s==null){return;}
        removeAll();
        if(displayType==DISPLAY_TABLE){
            setLayout(new BorderLayout());
            JScrollPane container=new JScrollPane();
            container.setViewportView(new SettingsTable(s));
            container.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            container.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            add(container);
        }else{
            setLayout(new BorderLayout());
            JPanel jp=new JPanel();
            jp.setLayout(new SpringLayout());

            // adds all the elements defined in this class
            int numComponents = s.size();
            for (SettingsProperty sp : s) {
                if (sp.getEditorType()==Settings.NO_EDIT){
                    numComponents--;
                    continue;
                }
                if (sp.getEditorType()==Settings.EDIT_SEPARATOR){
                    jp.add(new JSeparator());
                    jp.add(new JSeparator());
                    continue;
                }
                JLabel label=new JLabel(sp.getName());
                label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
                label.setInheritsPopupMenu(true);
                JComponent component = SettingsFactory.getEditor(sp);
                if(component!=null){
                    label.setToolTipText(sp.getTooltipText());
                    component.setToolTipText(sp.getTooltipText());
                    component.setInheritsPopupMenu(true);
                    jp.add(label);
                    jp.add(component);
                }
            }
            SpringUtilities.makeCompactGrid(jp,numComponents, 2, 5, 5, 5, 5);
            jp.setInheritsPopupMenu(true);
            JScrollPane container=new JScrollPane();
            container.setViewportView(jp);
            container.setInheritsPopupMenu(true);
            container.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            container.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            add(container);
        }
        setToolTipText(s.toString());
        setName(s.toString());
        validate();
    }
    
    

    
    // CONTEXT MENU SUPPORT
    
    public JMenu getDeleteMenu() { 
        JMenu result = new JMenu("Remove setting");
        for(final SettingsProperty sp: s){
            result.add(new JMenuItem(sp.getName())).addActionListener( new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    s.remove(sp);
                    updatePanel();
                }                
            });
        }
        return result;
    }
}

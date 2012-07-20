/**
 * EventTimelineView.java
 * Created Jul 20, 2012
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package timeline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * <p>
 * </p>
 * @author elisha
 */
public class EventTimelineView extends JScrollPane implements PropertyChangeListener, ListDataListener {

    TimelineModel tModel;
    TypedListModel<EventChannelModel> chModel;
    
    TimelineView header;
    ChannelControls left;
    JPanel centerView;
    
    public EventTimelineView() {
        setColumnHeaderView(header = new TimelineView());
        setRowHeaderView(left = new ChannelControls());
        centerView = new JPanel();
    }
    
    
    //<editor-fold defaultstate="collapsed" desc="EVENT HANDLING">
    //
    // EVENT HANDLING
    //

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == tModel) {
            
        }
    }

    public void contentsChanged(ListDataEvent e) {
        rebuild();
    }

    public void intervalAdded(ListDataEvent e) {
        rebuild();
    }

    public void intervalRemoved(ListDataEvent e) {
        rebuild();
    }
    
    private void rebuild() {
        // TODO - reconstruct layouts
    }
    
    //</editor-fold>
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    //
    // PROPERTIES
    //

    /** Returns tModel of channel data */
    public TypedListModel<EventChannelModel> getChannelModel() {
        return chModel;
    }
    
    /** Sets tModel of channel data */
    public void setChannelModel(TypedListModel<EventChannelModel> model) {
        if (this.chModel != model) {
            if (this.chModel != null)
                this.chModel.removeListDataListener(this);
            this.chModel = model;
            if (this.chModel != null)
                this.chModel.addListDataListener(this);
            repaint();
        }
    }
    
    /** Returns tModel of timeline */
    public TimelineModel getTimelineModel() {
        return tModel;
    }
    
    /** Sets tModel of timeline */
    public void setTimelineModel(TimelineModel m) {
        if (this.tModel != tModel) {
            if (this.tModel != null)
                this.tModel.removePropertyChangeListener(this);
            this.tModel = tModel;
            if (this.tModel != null)
                this.tModel.addPropertyChangeListener(this);
            repaint();
        }
    }
    
    //</editor-fold>
    
    

    public class TimelineView extends JComponent {}
    public class ChannelControls extends JComponent {}
    
}

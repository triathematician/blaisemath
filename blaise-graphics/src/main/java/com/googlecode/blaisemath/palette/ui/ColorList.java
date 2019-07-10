package com.googlecode.blaisemath.palette.ui;

/*
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.googlecode.blaisemath.firestarter.PropertySheetDialog;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import static java.util.Objects.requireNonNull;
import javax.swing.UIManager;

/**
 * UI with an editable list of colors, each of which may be associated with a string. This presents the colors
 * in a list, and may allow users to add/edit/remove/rearrange the colors. The allowed behavior can be configured
 * within {@link ColorListEditConstraints}. Changes are propagated through the "colors" properties when either
 * the list of colors changes, or individual items within the list change.
 * 
 * @author petereb1
 */
public final class ColorList extends JList {
    
    private static final String MODEL = "colorListModel";
    public static final String COLORS = "colors";
    
    private ColorListModel model;
    private ColorListEditConstraints editConstraints = new ColorListEditConstraints();
    private final ListDataListener modelListener = new ListModelListener();

    public ColorList() {
        setColorListModel(new ColorListModel());
        setCellRenderer(new BasicShapeStyleRenderer());
        setLayoutOrientation(JList.VERTICAL_WRAP);
        setVisibleRowCount(0);
        addMouseListener(new PopupListener());
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    public List<KeyColorBean> getColors() {
        return model.getColors();
    }
    
    public void setColors(List<KeyColorBean> colors) {
        model.setColors(colors);
    }
    
    public ColorListModel getColorListModel() {
        return model;
    }
    
    public void setColorListModel(ColorListModel model) {
        if (this.model != model) {
            Object old = this.model;
            setModel(this.model = model);
            firePropertyChange(MODEL, old, model);
        }
    }

    public ColorListEditConstraints getEditConstraints() {
        return editConstraints;
    }

    public void setEditConstraints(ColorListEditConstraints editConstraints) {
        this.editConstraints = requireNonNull(editConstraints);
    }
    
    //</editor-fold>
    
    private void listChanged(ListDataEvent e) {
        firePropertyChange(COLORS, null, getColors());
    }
    
    //<editor-fold defaultstate="collapsed" desc="INNER CLASSES">
    
    /** Propagates changes from list model. */
    class ListModelListener implements ListDataListener {
        @Override
        public void intervalAdded(ListDataEvent e) {
            listChanged(e);
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            listChanged(e);
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            listChanged(e);
        }
    }
    
    /** Shows popup menu */
    private class PopupListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }
        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                int idx = locationToIndex(e.getPoint());
                setSelectedIndex(idx);
                if (idx != -1) {
                    final KeyColorBean item = model.getElementAt(idx);
                    
                    AddAction add = new AddAction();
                    add.setEnabled(editConstraints.isAddSupported());
                    
                    EditAction edit = new EditAction(item);
                    edit.setEnabled(editConstraints.isKeyEditable(item.getName()));
                             
                    RemoveAction remove = new RemoveAction(item);
                    remove.setEnabled(editConstraints.isRemovable(item.getName()));
                    
                    final ColorPicker cp = new ColorPicker();
                    cp.setColor(item.getColor());
                    cp.addPropertyChangeListener("color", new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            item.setColor(cp.getColor());
                            ColorList.this.repaint();
                            listChanged(null);
                        }
                    });

                    JPopupMenu popup = new JPopupMenu();
                    popup.add(cp);
                    popup.addSeparator();
                    popup.add(edit);
                    popup.add(remove);
                    popup.addSeparator();
                    popup.add(add);
                    popup.show(ColorList.this, e.getX(), e.getY());
                } else if (editConstraints.isAddSupported()) {
                    JPopupMenu popup = new JPopupMenu();
                    popup.add(new AddAction());
                    popup.show(ColorList.this, e.getX(), e.getY());
                }
            }
        }
    }
    
    private class AddAction extends AbstractAction {
        private AddAction() {
            super("Add...");
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            KeyColorBean item = new KeyColorBean();
            item.setColor(UIManager.getColor("List.foreground"));
            JFrame frm = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, ColorList.this);
            if (editConstraints.isKeysEditable()) {
                PropertySheetDialog.show(frm, true, item);
            }
            model.addElement(item);
            listChanged(null);
        }
    }
    
    private class EditAction extends AbstractAction {
        private final KeyColorBean item;
        
        private EditAction(KeyColorBean item) {
            super("Edit...");
            this.item = item;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frm = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, ColorList.this);
            PropertySheetDialog.show(frm, true, item);
            listChanged(null);
            revalidate();
        }
    }

    private class RemoveAction extends AbstractAction {
        private final KeyColorBean item;
        
        private RemoveAction(KeyColorBean item) {
            super("Remove");
            this.item = item;
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            model.removeElement(item);
            revalidate();
            listChanged(null);
        }
    }

    private class BasicShapeStyleRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            KeyColorBean lc = (KeyColorBean) value;
            setText(lc.getName());
            setIcon(new BasicColorIcon(lc.getColor(), 18, null));
            return this;
        }
    }
    
    //</editor-fold>
    
}

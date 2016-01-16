/**
 * PropertyActionPanel.java
 * Created Mar 28, 2015
 */
package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2016 Elisha Peterson
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


import com.googlecode.blaisemath.firestarter.BeanPropertyModel;
import com.googlecode.blaisemath.firestarter.PropertySheet;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

/**
 * Combines a property editor panel with OK/Cancel buttons.
 * @author elisha
 */
public class PropertyActionPanel extends JPanel {
    
    private JToolBar tb;
    private PropertySheet panel;
    
    private final JButton okButton;
    private Action userOkAction;
    private final JButton cancelButton;
    private Action userCancelAction;

    public PropertyActionPanel() {
        super(new BorderLayout());
        tb = new JToolBar();
        tb.setFloatable(false);
        add(tb, BorderLayout.NORTH);
        
        panel = PropertySheet.forBean(new Object());
        add(panel, BorderLayout.CENTER);
        
        okButton = new JButton(new AbstractAction("Apply"){
            @Override
            public void actionPerformed(ActionEvent e) {
                okAction();
            }
        });
        okButton.setEnabled(false);
        
        cancelButton = new JButton(new AbstractAction("Cancel"){
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });
        cancelButton.setEnabled(false);
        
        JPanel south = new JPanel(new FlowLayout());
        south.add(okButton);
//        south.add(cancelButton);
        add(south, BorderLayout.SOUTH);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public JToolBar getToolBar() {
        return tb;
    }
    
    public Object getBean() {
        return ((BeanPropertyModel)panel.getPropertyModel()).getBean();
    }
    
    public void setBean(Object bean) {
        if (bean != getBean()) {
            remove(panel);
            panel = PropertySheet.forBean(bean);
            add(panel, BorderLayout.CENTER);
            invalidate();
        }
    }

    public Action getUserOkAction() {
        return userOkAction;
    }

    public void setUserOkAction(Action userOkAction) {
        this.userOkAction = userOkAction;
        okButton.setEnabled(userOkAction != null);        
        okButton.setVisible(userOkAction != null);        
    }

    public Action getUserCancelAction() {
        return userCancelAction;
    }

    public void setUserCancelAction(Action userCancelAction) {
        this.userCancelAction = userCancelAction;
        cancelButton.setEnabled(userCancelAction != null);        
    }
    
    //</editor-fold>
    
    private void okAction() {
        if (userOkAction != null) {
            userOkAction.actionPerformed(new ActionEvent(getBean(), 0, "Ok"));
        }
    }
    
    private void cancelAction() {
        if (userCancelAction != null) {
            userCancelAction.actionPerformed(new ActionEvent(getBean(), 0, "Cancel"));
        }
    }
}

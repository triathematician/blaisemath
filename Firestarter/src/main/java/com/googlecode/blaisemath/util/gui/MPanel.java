/**
 * MPanel.java
 * Created on Jun 25, 2009
 */
package com.googlecode.blaisemath.util.gui;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * <p>
 *   <code>MPanel</code> encapsulates another Component with a title bar.
 *   The component may be minimized or maximized depending upon the user's actions.
 *   The title bar defaults to a simple label, but the user may also alter that
 *   component directly for custom displays.
 * </p>
 *
 * @author Elisha Peterson
 */
public class MPanel extends JPanel {

    /** The title bar */
    JPanel titleBar;
    /** The button for maximizing/minimizing */
    JButton rollupButton;
    /** The label displaying the title */
    JLabel titleLabel;

    /** Component within the panel */
    protected Component component;

    /** Whether panel is currently minimized */
    boolean minimized;

    //
    // CONSTRUCTORS
    //

    public MPanel() {
        this("Title", new JLabel("Component"), false);
    }

    public MPanel(Component component) {
        this(component.toString(), component, false);
    }

    public MPanel(String title, Component component) {
        this(title, component, false);
    }

    public MPanel(JLabel label, Component component) {
        this(label, component, false);
    }

    public MPanel(Component component, boolean minimized) {
        this(component.toString(), component, minimized);
    }

    public MPanel(String title, Component component, boolean minimized) {
        this(new JLabel(title), component, minimized);
    }

    public MPanel(JLabel label, Component component, boolean minimized) {
        titleLabel = label;
        this.component = component;
        this.minimized = minimized;
        initComponents();
    }

    //
    // INITIALIZERS
    //

    private void initComponents() {
        int TITLEHT = titleLabel.getPreferredSize().height;

        rollupButton = new JButton(new MinimizeAction());
          rollupButton.setMargin(new Insets(0, 0, 0, 0));
          Dimension square = new Dimension(TITLEHT, TITLEHT);
          rollupButton.setMinimumSize(square);
          rollupButton.setMaximumSize(square);
          rollupButton.setPreferredSize(square);
          rollupButton.setOpaque(false);

        titleBar = new JPanel();
        titleBar.setMinimumSize(new Dimension(component.getMinimumSize().width + 2, TITLEHT));
        titleBar.setMaximumSize(new Dimension(component.getMaximumSize().width + 2, TITLEHT));
        titleBar.setPreferredSize(new Dimension(component.getPreferredSize().width + 2, TITLEHT));
//        titleLabel.setOpaque(false);
        titleBar.setBackground(new Color(192, 192, 216));

        titleBar.setLayout(new BorderLayout());
          titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
          titleBar.add(rollupButton, BorderLayout.WEST);
          //titleLabel.setForeground(Color.WHITE);
          titleBar.add(titleLabel, BorderLayout.CENTER);

        setBorder(BorderFactory.createLineBorder(titleBar.getBackground(), 2));
        setLayout(new BorderLayout());
        add(titleBar, BorderLayout.NORTH);
        setPrimaryComponent(component);

        updateSize();

        addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                updateSize();
            }
        });
    }

    private void updateSize() {
        int height = 0;
        int minWidth = 0;
        int prefWidth = 0;
        if (minimized) {
            if (Arrays.asList(getComponents()).contains(component)) {
                remove(component);
            }
            height = titleBar.getPreferredSize().height + 4;
            minWidth = 20;
            prefWidth = 20;
        } else {
            if (!Arrays.asList(getComponents()).contains(component)) {
                add(component);
            }
            height = component.getPreferredSize().height + titleBar.getPreferredSize().height + 4;
            minWidth = component.getMinimumSize().width;
            prefWidth = component.getPreferredSize().width;
        }
        setMinimumSize(new Dimension(minWidth + 4, height));
        setMaximumSize(new Dimension(prefWidth + 4, height));
        setPreferredSize(new Dimension(component.getPreferredSize().width + 4, height));
        revalidate();
        repaint();
    }

    //
    // BEAN PATTERNS
    //

    /** @return title */
    public String getTitle() {
        return titleLabel.getText();
    }

    /** Sets title */
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    /** @return the component active in this panel */
    public Component getPrimaryComponent() {
        return component;
    }

    /** Sets main component */
    public void setPrimaryComponent(Component c) {
        if (component != null) {
            component.removePropertyChangeListener(COMPONENT_SIZE_LISTENER);
            remove(component);
        }
        component = c;
        if (component instanceof JComponent) {
            ((JComponent) component).setBorder(null);
        }
        component.addPropertyChangeListener(COMPONENT_SIZE_LISTENER);

        add(component, BorderLayout.CENTER);
        updateSize();
    }

    //
    // EVENT HANDLING
    //

    private final PropertyChangeListener COMPONENT_SIZE_LISTENER = new PropertyChangeListener(){
        public void propertyChange(PropertyChangeEvent evt) {
            String prop = evt.getPropertyName();
            if ("size".equals(prop) || "minimumSize".equals(prop)
                    || "preferredSize".equals(prop) || "maximumSize".equals(prop)) {
                updateSize();
            }
        }
    };


    /** Action to minimize or maximize the window. */
    public class MinimizeAction extends AbstractAction {
        public MinimizeAction() {
            super(minimized ? " + " : " - ");
            putValue(SHORT_DESCRIPTION, "Minimize/maximize panel");
        }
        public void actionPerformed(ActionEvent e) {
            minimized = !minimized;
            putValue(NAME, minimized ? " + " : " - ");
            updateSize();
        }
    }
}

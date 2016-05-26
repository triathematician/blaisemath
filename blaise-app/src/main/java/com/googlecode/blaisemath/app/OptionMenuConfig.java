/**
 * OptionMenuConfig.java
 * Created May 2016
 */
package com.googlecode.blaisemath.app;

/*
 * #%L
 * blaise-app
 * --
 * Copyright (C) 2014 - 2016 Elisha Peterson
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


import com.google.common.collect.Lists;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Describes a menu with an auto-generated list of staticMethod.
 * @author elisha
 */
public final class OptionMenuConfig {
    
    private static final Logger LOG = Logger.getLogger(OptionMenuConfig.class.getName());
    private static final String INVALID_METHOD_ERROR = "Invalid method used for configurable options menu";
    
    /** The action key for a menu selection */
    private String action;
    /** The name of the sub-menu */
    private String name;
    /** Reference to a static method invoked to get the selections */
    private String staticMethod;

    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getOptions() {
        return staticMethod;
    }

    public void setOptions(String options) {
        this.staticMethod = options;
    }
    
    //</editor-fold>

    /**
     * Creates menu from this config.
     * @param am string/action mapping
     * @return menu items
     */
    JMenu createMenu(Map<String,javax.swing.Action> am) {
        JMenu res = new JMenu(name);
        for (JMenuItem item : createMenuItems(am)) {
            res.add(item);
        }
        return res;
    }

    /**
     * Creates popup menu from this config.
     * @param am string/action mapping
     * @return menu items
     */
    JPopupMenu createPopupMenu(Map<String,javax.swing.Action> am) {
        JPopupMenu res = new JPopupMenu(name);
        for (JMenuItem item : createMenuItems(am)) {
            res.add(item);
        }
        return res;
    }

    /**
     * Adds a configurable collection of menu items to the menu.
     * @param menu the menu to add to
     * @param props configuration staticMethod for the menu
     * @param am string/action mapping
     * @return menu items
     */
    List<JMenuItem> createMenuItems(Map<String,javax.swing.Action> am) {
        Action swingAction = am.get(action);
        List options = invokeListMethod(staticMethod);
        if (swingAction == null) {
            LOG.log(Level.SEVERE, "Action not found: {0}", action);
            return Collections.emptyList();
        }
        if (options.isEmpty()) {
            LOG.log(Level.SEVERE, "Method did not return any valid options: {0}", staticMethod);
        }
        List<JMenuItem> res = Lists.newArrayList();
        for (Object o : options) {
            res.add(new JMenuItem(sourceAction(swingAction, o)));
        }
        return res;
    }

    /** Attempt to invoke static method defined by given string, returning result as a list. */
    private static List invokeListMethod(String string) {
        try {
            Method method = findMethod(string);
            if (method == null) {
                LOG.log(Level.SEVERE, "Invalid method. Should be in the form   org.package.class#method  ");
                return Collections.emptyList();
            }
            Object res = method.invoke(null);
            if (!(res instanceof Iterable)) {
                LOG.log(Level.SEVERE, "Did not return iterable: {0}", res);
            }
            return Lists.newArrayList((Iterable) res);
        } catch (ClassNotFoundException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (SecurityException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalAccessException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (IllegalArgumentException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        } catch (InvocationTargetException ex) {
            LOG.log(Level.SEVERE, INVALID_METHOD_ERROR, ex);
        }
        return Collections.emptyList();
    }
    
    /** Looks up method based on given string name */
    @Nullable
    private static Method findMethod(String name) throws NoSuchMethodException, ClassNotFoundException {
        String[] spl = name.split("#");
        if (spl.length != 2) {
            return null;
        }
        String className = spl[0];
        String methodName = spl[1];
        Class clazz = Class.forName(className);
        return clazz.getMethod(methodName);
    }

    /**
     * Override the default action behavior to always use the provided object as source.
     */
    private static Action sourceAction(final Action action, final Object src) {
        return new AbstractAction(src+"") {
            @Override
            public void actionPerformed(ActionEvent e) {
                ActionEvent evt = new ActionEvent(src, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers());
                action.actionPerformed(evt);
            }
        };
    }

}

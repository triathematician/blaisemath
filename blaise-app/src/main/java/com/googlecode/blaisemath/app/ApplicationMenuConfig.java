/**
 * ApplicationMenuConfig.java
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


import com.google.common.collect.Maps;
import static com.googlecode.blaisemath.app.MenuConfig.DEFAULT_MENUBAR_KEY;
import static com.googlecode.blaisemath.app.MenuConfig.DEFAULT_TOOLBAR_KEY;
import static com.googlecode.blaisemath.app.MenuConfig.readMenuBarConfig;
import static com.googlecode.blaisemath.app.MenuConfig.readToolBarConfig;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;

/**
 * Integrates {@link MenuConfig} with the Better Swing Application Framework.
 * 
 * @author elisha
 */
public class ApplicationMenuConfig {
    
    private static final Logger LOG = Logger.getLogger(ApplicationMenuConfig.class.getName());
    
    private ApplicationMenuConfig() {
    }
    
    /**
     * Load default menubar and toolbar for given view.
     * @param view the view to configure
     */
    public static void loadMenus(FrameView view) {
        Class<? extends Application> appType = view.getApplication().getClass();
        try {
            view.setMenuBar(readMenuBar(appType, view));
            view.setToolBar(readToolBar(appType, view));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Menu config failure.", ex);
        }
    }
    
    /**
     * Create menubar from file
     * @param appClass application type
     * @param actionParents potential parents for actions
     * @return menu bar
     * @throws IOException if there's an error reading from the config file 
     */
    public static JMenuBar readMenuBar(Class<? extends Application> appClass, Object... actionParents) throws IOException {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String,javax.swing.Action> actionMap = actionMap(appContext, actionParents);
        List<Map> config = readMenuBarConfig(appClass, DEFAULT_MENUBAR_KEY);
        JMenuBar res = new JMenuBar();
        List<JMenuItem> items = MenuConfig.createMenuItemList(config, actionMap);
        for (JMenuItem mi : items) {
            if (mi instanceof JMenu) {
                res.add((JMenu) mi);
            } else {
                LOG.log(Level.WARNING, "Item cannot be added to menubar: {0}", mi);
            }
        }
        return res;
    }
    
    /**
     * Create toolbar component from file
     * @param appClass application type
     * @param actionParents potential parents for actions
     * @return toolbar
     * @throws IOException if there's an error reading from the config file 
     */
    public static JToolBar readToolBar(Class<? extends Application> appClass, Object... actionParents) throws IOException {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String,javax.swing.Action> actionMap = actionMap(appContext, actionParents);
        List<String> config = readToolBarConfig(appClass, DEFAULT_TOOLBAR_KEY);
        return MenuConfig.createToolBar(null, config, actionMap);
    }

    /**
     * Create context menu initializer from file
     * @param key config key
     * @param appClass application type
     * @param actionParents potential parents for actions
     * @return toolbar
     * @throws IOException if there's an error reading from the config file 
     */
    public static PresetMenuInitializer readMenuInitializer(String key, Class<? extends Application> appClass, Object... actionParents) throws IOException {
        ApplicationContext appContext = Application.getInstance(appClass).getContext();
        Map<String,javax.swing.Action> actionMap = actionMap(appContext, actionParents);
        List config = MenuConfig.readMenuConfig(appClass, key);
        return new PresetMenuInitializer(MenuConfig.createMenuItemList(config, actionMap));
    }

    /**
     * Uses a variable number of parent objects as potential action owners, creating a common lookup table for actions.
     * If more than one object has the same action, the result table will use the first it finds.
     * @param appContext context of application
     * @param actionParents parent objects for actions
     * @return table of actions available within the app context
     */
    private static Map<String, javax.swing.Action> actionMap(ApplicationContext appContext, Object... actionParents) {
        Map<String, javax.swing.Action> res = Maps.newHashMap();
        for (Object o : actionParents) {
            ActionMap am = appContext.getActionMap(o);
            for (Object k : am.allKeys()) {
                if (!res.containsKey((String) k)) {
                    res.put((String) k, am.get(k));
                }
            }
        }
        return res;
    }
    
}

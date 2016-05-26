/*
 * Copyright 2016 elisha.
 *
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


import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;

/**
 *
 * @author elisha
 */
public class MenuConfigTestFrame extends FrameView {

    public MenuConfigTestFrame(Application app) {
        super(app);
        
        // set up menus
        ApplicationMenuConfig.loadMenus(this);
        
        JLabel label = new JLabel("application stuff");
        setComponent(label);
    }
    
    @Action
    public void editThis(ActionEvent evt) {
        System.out.println("edit: "+evt.getSource());
    }
    
    @Action
    public void action1() {
        System.out.println("action1");
    }
    
    @Action
    public void action2() {
        System.out.println("action2");
    }
    
    @Action
    public void subOption1() {
        System.out.println("action1");
    }
    
    @Action
    public void subOption2() {
        System.out.println("action2");
    }

    
}

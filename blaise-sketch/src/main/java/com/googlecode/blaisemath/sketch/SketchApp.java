/**
 * BlaiseSketchApp.java
 * Created Oct 1, 2014
 */
package com.googlecode.blaisemath.sketch;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2017 Elisha Peterson
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


import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import static org.jdesktop.application.Application.launch;
import org.jdesktop.application.SingleFrameApplication;

/**
 * Root application for interactive drawing on a {@link JGraphicComponent}.
 * 
 * @author Elisha
 */
public class SketchApp extends SingleFrameApplication {
    
    @Override
    protected void startup() {
        SketchFrameView view = new SketchFrameView(this);
        show(view);
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", 
            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        launch(SketchApp.class, args);
    }
    
}

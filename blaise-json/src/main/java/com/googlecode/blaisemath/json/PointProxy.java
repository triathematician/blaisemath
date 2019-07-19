package com.googlecode.blaisemath.json;

/*-
 * #%L
 * blaise-json
 * --
 * Copyright (C) 2019 Elisha Peterson
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

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Serializable proxy for a {@link Rectangle}.
 * @author Elisha Peterson
 */
public class PointProxy {
    
    private int x;
    private int y;
    
    public PointProxy() {
    }
    
    public PointProxy(Point p) {
        x = p.x;
        y = p.y;
    }
    
    public Point toPoint() {
        return new Point(x, y);
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    //</editor-fold>

}

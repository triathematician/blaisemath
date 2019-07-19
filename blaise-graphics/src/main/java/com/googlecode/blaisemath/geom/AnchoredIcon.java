package com.googlecode.blaisemath.geom;

/*
 * #%L
 * BlaiseGraphics
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

import com.googlecode.blaisemath.coordinate.Point2DBean;
import javax.swing.Icon;

/**
 * An icon anchored at a given location.
 * @author Elisha Peterson
 */
public final class AnchoredIcon extends Point2DBean {

    private final Icon icon;

    public AnchoredIcon(double x, double y, Icon icon) {
        super(x, y);
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "AnchoredIcon{" + getX() + ',' + getY() + ',' + getIcon() + '}';
    }

    //region PROPERTIES
    
    public Icon getIcon() {
        return icon;
    }

    public int getIconWidth() {
        return icon.getIconWidth();
    }

    public int getIconHeight() {
        return icon.getIconHeight();
    }
    
    //endregion
    
}

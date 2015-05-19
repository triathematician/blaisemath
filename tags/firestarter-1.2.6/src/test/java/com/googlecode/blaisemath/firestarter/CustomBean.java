/**
 * NumberBean.java
 * Created on Jun 29, 2009
 */

package com.googlecode.blaisemath.firestarter;

/*
 * #%L
 * Firestarter
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * <p>
 *   <code>NumberBean</code> ...
 * </p>
 *
 * @author Elisha Peterson
 */
public class CustomBean {

    protected Color color= Color.BLACK;
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }

    protected Color color2=Color.YELLOW;
    public Color getColor2() { return color2; }
    public void setColor2(Color color) { this.color = color; }

    protected Boolean boo = true;
    public Boolean getBoo() { return boo; }
    public void setBoo(Boolean boo) { this.boo = boo; }

    protected boolean boo2 = true;
    public boolean getBoo2() { return boo; }
    public void setBoo2(boolean boo) { this.boo = boo; }

    protected Font font = new Font("", 0, 10);
    public Font getFont() { return font; }
    public void setFont(Font font) { this.font = font; }

    protected String string="string";
    public String getString() { return string; }
    public void setString(String string) { this.string = string; }

    protected NumberBean nb = new NumberBean();
    public NumberBean getBean() { return nb; }
    public void setBean(NumberBean nb) { this.nb = nb; }
    
}

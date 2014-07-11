/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.testframes;

/*
 * #%L
 * BlaiseGraphics
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

import java.awt.Color;
import com.googlecode.blaisemath.style.PathStyle;
import com.googlecode.blaisemath.style.PathStyleBasic;
import com.googlecode.blaisemath.style.PointStyle;
import com.googlecode.blaisemath.style.PointStyleBasic;
import com.googlecode.blaisemath.style.ShapeStyle;
import com.googlecode.blaisemath.style.ShapeStyleBasic;
import com.googlecode.blaisemath.style.TextStyle;
import com.googlecode.blaisemath.style.TextStyleBasic;

/**
 *
 * @author elisha
 */
class RandomStyles {

    static PointStyle point() {
        return new PointStyleBasic()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .strokeWidth((float)(2*Math.random()))
                    .markerRadius((int)(25*Math.random()));
    }
    
    static PathStyle path() {
        return new PathStyleBasic()
                .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .strokeWidth((float)(12*Math.random()));
    }
    
    static ShapeStyle shape() {
        return new ShapeStyleBasic()
                    .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .stroke(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                    .strokeWidth((float)(Math.random()));
    }

    static TextStyle string() {
        return new TextStyleBasic()
                .fill(new Color((float)Math.random(),(float)Math.random(),(float)Math.random()))
                .fontSize((float)(5+10*Math.random()));
    }
    
}

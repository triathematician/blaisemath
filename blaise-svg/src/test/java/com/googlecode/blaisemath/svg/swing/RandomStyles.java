/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.svg;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2014 - 2020 Elisha Peterson
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

import com.googlecode.blaisemath.style.AttributeSet;
import static com.googlecode.blaisemath.style.Styles.*;
import java.awt.Color;
import java.util.Random;

/**
 *
 * @author Elisha Peterson
 */
class RandomStyles {
    
    static Color color() {
        return new Color((float)Math.random(),(float)Math.random(),(float)Math.random());
    }
    
    static float strokeWidth() {
        return (float) (2*Math.random());
    }
    
    static int markerRadius() {
        return (int)(25*Math.random());
    }
    
    static int fontSize() {
        return (int)(5+10*Math.random());
    }

    static AttributeSet point() {
        return AttributeSet
                .of(FILL, color())
                .and(STROKE, color())
                .and(STROKE_WIDTH, strokeWidth())
                .and(MARKER_RADIUS, markerRadius());
    }
    
    static AttributeSet path() {
        return AttributeSet
                .of(STROKE, color())
                .and(STROKE_WIDTH, strokeWidth());
    }
    
    static AttributeSet shape() {
        return AttributeSet
                .of(FILL, color())
                .and(STROKE, color())
                .and(STROKE_WIDTH, strokeWidth());
    }

    static AttributeSet string() {
        return AttributeSet
                .of(FILL, color())
                .and(STROKE, null)
                .and(FONT_SIZE, fontSize());
    }

    static AttributeSet anchoredString() {
        return string()
                .and(ALIGN_BASELINE, baseline())
                .and(TEXT_ANCHOR, anchor());
    }
    
    static String baseline() {
        switch (new Random().nextInt(3)) {
            case 0: return ALIGN_BASELINE_BASELINE;
            case 1: return ALIGN_BASELINE_MIDDLE;
            case 2: return ALIGN_BASELINE_HANGING;
            default: throw new IllegalStateException();
        }
    }
    
    static String anchor() {
        switch (new Random().nextInt(3)) {
            case 0: return TEXT_ANCHOR_START;
            case 1: return TEXT_ANCHOR_MIDDLE;
            case 2: return TEXT_ANCHOR_END;
            default: throw new IllegalStateException();
        }
    }
    
}

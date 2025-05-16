package com.googlecode.blaisemath.graphics.testui;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2019 - 2025 Elisha Peterson
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

import java.awt.*;

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
    
    static String fontWeight() {
        return Math.random() < .25 ? FONT_WEIGHT_BOLD : null;
    }
    
    static String fontStyle() {
        return Math.random() < .25 ? FONT_STYLE_ITALIC : null;
    }

    static AttributeSet point() {
        return AttributeSet.of(FILL, color(), STROKE, color(), STROKE_WIDTH, strokeWidth())
                .and(MARKER_RADIUS, markerRadius());
    }
    
    static AttributeSet path() {
        AttributeSet res = AttributeSet.of(STROKE, color(), STROKE_WIDTH, strokeWidth());
        if (Math.random() < .95) {
            res.and(STROKE_DASHES, (float) (Math.random())+","+(float) (Math.random()));
        }
        return res;
    }
    
    static AttributeSet shape() {
        return AttributeSet.of(FILL, color(), STROKE, color(), STROKE_WIDTH, strokeWidth());
    }

    static AttributeSet string() {
        return AttributeSet.of(FILL, color(), FONT_SIZE, fontSize())
                .and(FONT_WEIGHT, fontWeight())
                .and(FONT_STYLE, fontStyle());
    }
    
}

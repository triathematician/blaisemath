/*
 * PointAdapter.class
 * Created Oct 9, 2013
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
 * --
 * Copyright (C) 2014 - 2019 Elisha Peterson
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


import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import java.awt.Point;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Exports a point as a string.
 * @author petereb1
 */
public class PointAdapter extends XmlAdapter<String,Point> {

    @Override
    public Point unmarshal(String v) {
        if (v == null) {
            return null;
        }
        Matcher m = Pattern.compile("point\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Iterable<String> kv = Splitter.on(",").trimResults().split(inner);
            try {
                int x = Integer.valueOf(Iterables.get(kv, 0));
                int y = Integer.valueOf(Iterables.get(kv, 1));
                return new Point(x,y);
            } catch (NumberFormatException x) {
                Logger.getLogger(PointAdapter.class.getName()).log(Level.FINEST,
                        "Not an integer", x);
                return null;
            }
        } else {
            Logger.getLogger(PointAdapter.class.getName()).log(Level.FINEST,
                    "Not a valid point", v);
            return null;
        }
    }

    @Override
    public String marshal(Point v) {
        return v == null ? "null"
                : String.format("point[%d,%d]", v.x, v.y);
    }
    
}

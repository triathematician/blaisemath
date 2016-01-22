/*
 * InsetsAdapter.java
 * Created on Aug 26, 2013
 */
package com.googlecode.blaisemath.util.xml;

/*
 * #%L
 * BlaiseCommon
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

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import java.awt.Insets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Serializes an insets object to/from text.
 * @author petereb1
 */
public class InsetsAdapter extends XmlAdapter<String,Insets> {

    @Override
    public Insets unmarshal(String v) throws Exception {
        if (Strings.isNullOrEmpty(v)) {
            return null;
        }
        Matcher m = Pattern.compile("insets\\[(.*)\\]").matcher(v.toLowerCase().trim());
        if (m.find()) {
            String inner = m.group(1);
            Map<String,String> kv = Splitter.on(",").trimResults().withKeyValueSeparator("=").split(inner);
            try {
                Integer t = Integer.valueOf(kv.get("t"));
                Integer l = Integer.valueOf(kv.get("l"));
                Integer b = Integer.valueOf(kv.get("b"));
                Integer r = Integer.valueOf(kv.get("r"));
                return new Insets(t, l, b, r);
            } catch (NumberFormatException x) {
                Logger.getLogger(InsetsAdapter.class.getName()).log(Level.FINEST,
                        "Not an integer", x);
                return null;
            }
        } else {
            throw new IllegalArgumentException("Invalid insets: "+v);
        }
    }

    @Override
    public String marshal(Insets v) {
        return v == null ? "null"
                : String.format("insets[t=%d,l=%d,b=%d,r=%d]", 
                        v.top, v.left, v.bottom, v.right);
    }

}

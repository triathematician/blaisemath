package com.googlecode.blaisemath.encode;

/*-
 * #%L
 * blaise-common
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

import com.google.common.base.Strings;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.requireNonNull;

/**
 * Adapter converting insets to/from strings of the form "insets(top,left,bottom,right)". Requires non-null values.
 *
 * @author Elisha Peterson
 */
public final class InsetsCoder implements StringEncoder<Insets>, StringDecoder<Insets> {

    private static final Logger LOG = Logger.getLogger(InsetsCoder.class.getName());

    @Override
    public String encode(Insets v) {
        requireNonNull(v);
        return String.format("insets(%d,%d,%d,%d)", v.top, v.left, v.bottom, v.right);
    }

    @Override
    public @Nullable Insets decode(String v) {
        if (Strings.isNullOrEmpty(v)) {
            return null;
        }
        Matcher m = Pattern.compile("insets\\s*\\((.*),(.*),(.*),(.*)\\)").matcher(v.toLowerCase().trim());
        if (m.matches()) {
            try {
                int t = Integer.parseInt(m.group(1));
                int l = Integer.parseInt(m.group(2));
                int b = Integer.parseInt(m.group(3));
                int r = Integer.parseInt(m.group(4));
                return new Insets(t, l, b, r);
            } catch (NumberFormatException x) {
                LOG.log(Level.FINEST, "Not an integer", x);
                return null;
            }
        } else {
            LOG.log(Level.FINEST, "Not a valid insets", v);
            return null;
        }
    }
    
}


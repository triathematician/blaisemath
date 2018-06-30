package com.googlecode.blaisemath.util;

/*-
 * #%L
 * blaise-common
 * --
 * Copyright (C) 2014 - 2018 Elisha Peterson
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
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import java.util.List;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Splitter that decodes values as numbers.
 * 
 * @author Elisha Peterson
 */
public class NumberSplitter {
    
    private Splitter splitter;

    /**
     * Create splitter that divides on the given regex.
     * @param regex regex
     * @return splitter
     */
    public static NumberSplitter onPattern(String regex) {
        NumberSplitter ns = new NumberSplitter();
        ns.splitter = Splitter.onPattern(regex);
        return ns;
    }

    /**
     * Attempts to split the given string as integers.
     * @param str what to split
     * @param def default return value if there is a problem splitting
     * @return 
     */
    public List<Integer> trySplitToIntegers(String str, @Nullable List<Integer> def) {
        List<String> split = splitter.trimResults().splitToList(str);
        List<Integer> res = Lists.newArrayList();
        for (String s : split) {
            Integer i = Ints.tryParse(s);
            if (i == null) {
                return def;
            }
            res.add(i);
        }
        return res;
    }

    /**
     * Attempts to split the given string as integers.
     * @param str what to split
     * @param def default return value if there is a problem splitting
     * @return 
     */
    public List<Double> trySplitToDoubles(String str, @Nullable List<Double> def) {
        List<String> split = splitter.trimResults().splitToList(str);
        List<Double> res = Lists.newArrayList();
        for (String s : split) {
            Double d = Doubles.tryParse(s);
            if (d == null) {
                return def;
            }
            res.add(d);
        }
        return res;
    }
    
}

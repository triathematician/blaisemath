package com.googlecode.blaisemath.svg.internal;

/*
 * #%L
 * blaise-svg
 * --
 * Copyright (C) 2014 - 2025 Elisha Peterson
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


import static com.googlecode.blaisemath.svg.internal.SvgUtils.parseLength;

import org.junit.Assert;
import org.junit.Test;

import java.util.Optional;

public class SvgUtilsTest {

    @Test
    public void testParseLength() {
        Assert.assertEquals(Optional.empty(), parseLength(null));
        Assert.assertEquals(Optional.empty(), parseLength("not a number"));
        Assert.assertEquals(Optional.empty(), parseLength("auto"));
        Assert.assertEquals(Optional.empty(), parseLength("50.5%"));
                
        Assert.assertEquals(12.0, parseLength("12").get(), 1e-3);
        Assert.assertEquals(15.5999, parseLength("12px").get(), 1e-3);
        Assert.assertEquals(12.0, parseLength("12pt").get(), 1e-3);
        Assert.assertEquals(864.0, parseLength("12in").get(), 1e-3);
        Assert.assertEquals(143.4599, parseLength("12em").get(), 1e-3);
        Assert.assertEquals(-143.4599, parseLength("-12ex").get(), 1e-3);
        Assert.assertEquals(16.2499, parseLength("12.5px").get(), 1e-3);
    }
    
}

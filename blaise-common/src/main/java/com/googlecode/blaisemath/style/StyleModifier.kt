package com.googlecode.blaisemath.style

import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2014 - 2021 Elisha Peterson
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
*/ /**
 * Creates a modified version of a style based on criteria provided as hints.
 *
 * @author Elisha Peterson
 */
interface StyleModifier {
    /**
     * Modify the attribute set according to the given set of hints.
     * @param style the attribute set to modify
     * @param hints the hints w/ modify instructions
     * @return the modified set
     */
    open fun apply(style: AttributeSet?, hints: MutableSet<String?>?): AttributeSet?
}
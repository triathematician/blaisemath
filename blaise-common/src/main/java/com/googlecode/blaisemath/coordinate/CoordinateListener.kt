package com.googlecode.blaisemath.coordinate

import com.googlecode.blaisemath.annotation.InvokedFromThread
import com.googlecode.blaisemath.encode.ColorCoderTest
import com.googlecode.blaisemath.encode.FontCoderTest
import com.googlecode.blaisemath.encode.PointCoderTest
import com.googlecode.blaisemath.style.AttributeSetCoderTest
import com.googlecode.blaisemath.util.ColorsTest
import junit.framework.TestCase
import org.junit.Before

/*-
* #%L
* blaise-common
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
 * Receives updates regarding the locations of a collection of objects. Handlers
 * should be aware that the update may be invoked from any thread.
 *
 * @param <S> type of object being located
 * @param <C> type of coordinate
 *
 * @author Elisha Peterson
</C></S> */
interface CoordinateListener<S, C> {
    /**
     * Called when coordinates/points are added or changed in a [CoordinateManager].
     * This method is called from the same thread that made the change.
     * @param evt description of what coordinates were added/removed/changed
     */
    @InvokedFromThread("unknown")
    open fun coordinatesChanged(evt: CoordinateChangeEvent<S?, C?>?)
}
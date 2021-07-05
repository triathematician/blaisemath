package com.googlecode.blaisemath.graphics

import com.googlecode.blaisemath.coordinate.HasPoint2D
import com.googlecode.blaisemath.style.AttributeSet

/*
* #%L
* BlaiseGraphics
* --
* Copyright (C) 2009 - 2021 Elisha Peterson
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

/** Text at a given location. */
class AnchoredText(x: kotlin.Double, y: kotlin.Double, var text: String? = null) : HasPoint2D(x, y)

/** Augments [AnchoredText] with style information. */
class StyledText(val text: AnchoredText, val style: AttributeSet)
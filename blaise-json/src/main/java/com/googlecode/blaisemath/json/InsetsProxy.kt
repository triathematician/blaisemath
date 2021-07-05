package com.googlecode.blaisemath.json

import java.awt.Insets

/*-
* #%L
* blaise-json
* --
* Copyright (C) 2019 - 2021 Elisha Peterson
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
 * Serializable proxy for an [Insets].
 * @author Elisha Peterson
 */
class InsetsProxy {
    private var top = 0
    private var bottom = 0
    private var left = 0
    private var right = 0
    fun toInsets(): Insets? {
        return Insets(top, left, bottom, right)
    }

    fun getTop(): Int {
        return top
    }

    fun setTop(top: Int) {
        this.top = top
    }

    fun getBottom(): Int {
        return bottom
    }

    fun setBottom(bottom: Int) {
        this.bottom = bottom
    }

    fun getLeft(): Int {
        return left
    }

    fun setLeft(left: Int) {
        this.left = left
    }

    fun getRight(): Int {
        return right
    }

    fun setRight(right: Int) {
        this.right = right
    }
}
package com.googlecode.blaisemath.palette.ui

import com.googlecode.blaisemath.app.ApplicationMenuConfig
import com.googlecode.blaisemath.graphics.testui.AnchorTestFrame
import com.googlecode.blaisemath.palette.ui.PaletteEditorTestUi
import com.googlecode.blaisemath.palette.ui.PaletteIconsTestUi
import com.googlecode.blaisemath.style.ui.AttributeSetPropertyModelTestFrame

/*
* #%L
* blaise-graphics
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
 * Object describing ways in which a particular list of colors may be edited.
 * @author Elisha Peterson
 */
open class ColorListEditConstraints {
    var addSupported = true
    var removable = true
    var keysEditable = true
    open fun isKeyEditable(item: String?): Boolean {
        return isKeysEditable()
    }

    open fun isRemovable(item: String?): Boolean {
        return isRemovable()
    }

    //<editor-fold defaultstate="collapsed" desc="BUILDER PATTERN">
    fun addSupported(`val`: Boolean): ColorListEditConstraints? {
        setAddSupported(`val`)
        return this
    }

    fun removable(`val`: Boolean): ColorListEditConstraints? {
        setRemovable(`val`)
        return this
    }

    fun keysEditable(`val`: Boolean): ColorListEditConstraints? {
        setKeysEditable(`val`)
        return this
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">
    fun isAddSupported(): Boolean {
        return addSupported
    }

    fun setAddSupported(addSupported: Boolean) {
        this.addSupported = addSupported
    }

    fun isRemovable(): Boolean {
        return removable
    }

    fun setRemovable(removable: Boolean) {
        this.removable = removable
    }

    fun isKeysEditable(): Boolean {
        return keysEditable
    }

    fun setKeysEditable(keysEditable: Boolean) {
        this.keysEditable = keysEditable
    } //</editor-fold>
}
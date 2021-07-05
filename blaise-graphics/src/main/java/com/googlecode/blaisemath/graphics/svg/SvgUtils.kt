package com.googlecode.blaisemath.graphics.svg

import com.googlecode.blaisemath.graphics.svg.SvgPathCoderTest
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent
import com.googlecode.blaisemath.util.Images
import junit.framework.TestCase
import java.awt.Component
import java.awt.Container
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Stream

/*-
* #%L
* blaise-graphics
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
*/ /**
 * Utilities for working with SVG.
 *
 * @author Elisha Peterson
 */
object SvgUtils {
    private val LOG = Logger.getLogger(SvgUtils::class.java.name)
    private val GET_GRAPHIC_COMPONENT_METHOD: String? = "getGraphicComponent"

    /** Identifier for svg (raw)  */
    val FORMAT_SVG_RAW: String? = "svg+raw"

    /** Identifier for svg (base64 encoded)  */
    val FORMAT_SVG_BASE64: String? = "svg"

    /** String produced when unable to export SVG  */
    val UNSUPPORTED_SVG: String? = "<!-- Unsupported><svg></svg>"

    /**
     * Encode component as target vector or raster format.
     * @param component compatible component to encode
     * @param format image format type
     * @return encoded string
     * @throws IOException if there's an encoding error
     */
    @Throws(IOException::class)
    fun encode(component: Component?, format: String?): String? {
        return if (FORMAT_SVG_RAW == format) encodeSvg(component, false) else if (FORMAT_SVG_BASE64 == format) encodeSvg(component, true) else Images.encodeStandardBase64(Images.renderImage(component), format)
    }

    /**
     * Attempt to encode SVG for the target component (which must be compatible).
     * @param component SVG-compatible component to encode
     * @param base64 whether to encode as base-64
     * @return encoded string
     */
    fun encodeSvg(component: Component?, base64: Boolean): String? {
        if (component is SvgExportable) {
            return encode((component as SvgExportable?).toSvg(component), base64)
        }
        val gc = findGraphicComponent(component)
        if (gc is SvgExportable) {
            return encode((gc as SvgExportable?).toSvg(gc), base64)
        }
        val gr = findSvgRoot(component)
        return if (gr == null) UNSUPPORTED_SVG else SvgCoder.Companion.defaultInstance().encode(gr)
    }

    private fun encode(str: String?, base64: Boolean): String? {
        return if (base64) Base64.getEncoder().encodeToString(str.toByteArray()) else str
    }

    /**
     * Attempt to serialize the given SVG component to string, with optional base64 encoding.
     * @param root SVG component to encode
     * @return encoded string
     */
    fun encodeSvg(root: SvgGraphic?): String? {
        val res: String? = if (root == null) null else SvgCoder.Companion.defaultInstance().encode(root)
        return res ?: UNSUPPORTED_SVG
    }

    /**
     * Test whether the given component is compatible with SVG-export.
     * @param component to test
     * @return true if compatible
     */
    fun hasSvgRoot(component: Component?): Boolean {
        Objects.requireNonNull(component)
        return component is SvgExportable || findSvgRoot(component) != null
    }

    fun findSvgRoot(component: Component?): SvgGraphic? {
        Objects.requireNonNull(component)
        val comp = findGraphicComponent(component)
        return if (comp == null) null else SvgCoder.Companion.defaultInstance().graphicFrom(comp)
    }

    private fun findGraphicComponent(component: Component?): JGraphicComponent? {
        Objects.requireNonNull(component)
        if (component is JGraphicComponent) {
            return component as JGraphicComponent?
        }
        try {
            val m = component.javaClass.getMethod(GET_GRAPHIC_COMPONENT_METHOD)
            val res = m?.invoke(component)
            if (res is JGraphicComponent) {
                return res as JGraphicComponent?
            }
        } catch (ex: IllegalAccessException) {
            LOG.log(Level.FINE, "Can't find JGraphicComponent instance!", ex)
        } catch (ex: InvocationTargetException) {
            LOG.log(Level.FINE, "Can't find JGraphicComponent instance!", ex)
        } catch (ex: NoSuchMethodException) {
            LOG.log(Level.FINE, "Can't find JGraphicComponent instance!", ex)
        }
        return if (component is Container) {
            Stream.of(*(component as Container?).getComponents())
                    .map { obj: Component? -> findGraphicComponent() }
                    .filter { obj: JGraphicComponent? -> Objects.nonNull(obj) }
                    .findFirst().orElse(null)
        } else null
    }
}
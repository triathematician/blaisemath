package com.googlecode.blaisemath.graphics.svg;

/*-
 * #%L
 * blaise-graphics
 * --
 * Copyright (C) 2009 - 2024 Elisha Peterson
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

import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.graphics.swing.JGraphicComponent;
import com.googlecode.blaisemath.util.Images;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.Component;
import java.awt.Container;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import java.util.Objects;
import static java.util.Objects.requireNonNull;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Utilities for working with SVG.
 * 
 * @author Elisha Peterson
 */
@Beta
public class SvgUtils {

    private static final Logger LOG = Logger.getLogger(SvgUtils.class.getName());
    private static final String GET_GRAPHIC_COMPONENT_METHOD = "getGraphicComponent";
    
    /** Identifier for svg (raw) */
    public static final String FORMAT_SVG_RAW = "svg+raw";
    /** Identifier for svg (base64 encoded) */
    public static final String FORMAT_SVG_BASE64 = "svg";
    
    /** String produced when unable to export SVG */
    public static final String UNSUPPORTED_SVG = "<!-- Unsupported><svg></svg>";

    /** Utility class */
    private SvgUtils() {
    }

    /**
     * Encode component as target vector or raster format.
     * @param component compatible component to encode
     * @param format image format type
     * @return encoded string
     * @throws IOException if there's an encoding error
     */
    public static String encode(Component component, String format) throws IOException {
        return FORMAT_SVG_RAW.equals(format) ? encodeSvg(component, false)
                : FORMAT_SVG_BASE64.equals(format) ? encodeSvg(component, true)
                : Images.encodeStandardBase64(Images.renderImage(component), format);
    }

    /**
     * Attempt to encode SVG for the target component (which must be compatible).
     * @param component SVG-compatible component to encode
     * @param base64 whether to encode as base-64
     * @return encoded string
     */
    public static String encodeSvg(Component component, boolean base64) {
        if (component instanceof SvgExportable) {
            return encode(((SvgExportable) component).toSvg(component), base64);
        }
        JGraphicComponent gc = findGraphicComponent(component);
        if (gc instanceof SvgExportable) {
            return encode(((SvgExportable) gc).toSvg(gc), base64);
        }
        SvgGraphic gr = findSvgRoot(component);
        return gr == null ? UNSUPPORTED_SVG : SvgCoder.defaultInstance().encode(gr);
    }
    
    private static String encode(String str, boolean base64) {
        return base64 ? Base64.getEncoder().encodeToString(str.getBytes()) : str;
    }
    
    /**
     * Attempt to serialize the given SVG component to string, with optional base64 encoding.
     * @param root SVG component to encode
     * @return encoded string
     */
    public static String encodeSvg(@Nullable SvgGraphic root) {
        String res = root == null ? null : SvgCoder.defaultInstance().encode(root);
        return res == null ? UNSUPPORTED_SVG : res;
    }

    /**
     * Test whether the given component is compatible with SVG-export.
     * @param component to test
     * @return true if compatible
     */
    public static boolean hasSvgRoot(Component component) {
        requireNonNull(component);
        return component instanceof SvgExportable || findSvgRoot(component) != null;
    }
    
    public static @Nullable SvgGraphic findSvgRoot(Component component) {
        requireNonNull(component);
        JGraphicComponent comp = findGraphicComponent(component);
        return comp == null ? null : SvgCoder.defaultInstance().graphicFrom(comp);
    }

    private static JGraphicComponent findGraphicComponent(Component component) {
        requireNonNull(component);
        if (component instanceof JGraphicComponent) {
            return (JGraphicComponent) component;
        }
        try {
            Method m = component.getClass().getMethod(GET_GRAPHIC_COMPONENT_METHOD);
            Object res = m == null ? null : m.invoke(component);
            if (res instanceof JGraphicComponent) {
                return (JGraphicComponent) res;
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            LOG.log(Level.FINE, "Can't find JGraphicComponent instance!", ex);
        }
        if (component instanceof Container) {
            return Stream.of(((Container) component).getComponents())
                    .map(SvgUtils::findGraphicComponent)
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);
        }
        return null;
    }
    
}

package com.googlecode.blaisemath.svg.reader;

/*-
 * #%L
 * blaise-svg
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
 */

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.graphics.Graphic;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.AttributeSetCoder;
import com.googlecode.blaisemath.style.Styles;
import com.googlecode.blaisemath.svg.xml.SvgElement;

import javax.xml.namespace.QName;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

/**
 * Maps SVG elements to primitives.
 * @author Elisha Peterson
 */
public abstract class SvgReader<S extends SvgElement, P> {

    private static final Map<Class, ServiceLoader> SERVICE_CACHE = Maps.newHashMap();

    private Class applyMethodType;

    /**
     * Tests to see if this can transform the target element. Checks to see if the element is an instance of the declared
     * "createPrimitive" method argument type.
     * @param el element
     * @return true if able to transform target
     */
    final boolean canRead(SvgElement el) {
        if (applyMethodType == null) {
            applyMethodType = Stream.of(getClass().getDeclaredMethods())
                    .filter(m -> "createPrimitive".equals(m.getName()))
                    .flatMap(m -> Arrays.stream(m.getParameterTypes()))
                    .filter(c -> c != SvgElement.class)
                    .findFirst().orElse(Void.class);
            if (applyMethodType == Void.class) {
                throw new IllegalStateException("Could not find method with declared SVG type");
            }
        }
        return applyMethodType.isInstance(el);
    }

    /**
     * Transforms the given element to a graphic object.
     * @param el element
     * @return true if able to transform target
     * @throws SvgReadException if there's an error creating the primitive
     */
    public final Graphic<Graphics2D> read(S el) throws SvgReadException {
        AttributeSet style = createStyle(el);
        P prim = createPrimitive(el);
        return createGraphic(prim, style);
    }

    /**
     * Construct style information object from the SVG element.
     * @param element SVG element
     * @return style info
     * @throws SvgReadException if there's an error creating the style object
     */
    protected AttributeSet createStyle(SvgElement element) {
        AttributeSetCoder coder = new AttributeSetCoder();
        AttributeSet shapeStyle = element.style == null ? new AttributeSet() : coder.decode(element.style);
        AttributeSet res = shapeStyle == null ? new AttributeSet() : shapeStyle.copy();

        Map<QName, Object> attr = element.otherAttr;
        if (attr != null) {
            for (Map.Entry<QName, Object> en : attr.entrySet()) {
                res.put(en.getKey().toString(), coder.decodeValue(Objects.toString(en.getValue()), Object.class));
            }
        }
        if (element.id != null) {
            res.put(Styles.ID, element.id);
        }
        StyleReader.updateColorFields(res);
        return res;
    }

    /**
     * Create primitive from SVG element.
     * @param el element
     * @return primitive
     * @throws SvgReadException if there's an error creating the primitive
     */
    protected abstract P createPrimitive(S el) throws SvgReadException;

    /**
     * Create graphic from primitive and style.
     * @param prim the primitive
     * @param style the style
     * @return graphic
     */
    protected abstract Graphic<Graphics2D> createGraphic(P prim, AttributeSet style);

    static List<SvgReader> readers() {
        return services(SvgReader.class);
    }

    /** Utility method to dynamically get list of services. */
    private static <X> List<X> services(Class<X> type) {
        if (SERVICE_CACHE.get(type) == null) {
            SERVICE_CACHE.put(type, ServiceLoader.load(type));
        }
        return Lists.newArrayList((Iterable<X>) SERVICE_CACHE.get(type));
    }

}

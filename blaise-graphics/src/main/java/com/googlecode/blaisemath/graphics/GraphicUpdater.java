package com.googlecode.blaisemath.graphics;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2025 Elisha Peterson
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

import org.checkerframework.checker.nullness.qual.Nullable;

import java.awt.geom.Rectangle2D;

/**
 * Create {@link Graphic} objects for rendering items.
 * @param <E> item type
 * @param <G> canvas type
 * @author Elisha Peterson
 */
public interface GraphicUpdater<E, G> {

    /**
     * Create or edit graphic object for a specified entity.
     * @param e the entity
     * @param bounds the position of the entity
     * @param existing graphic already associated with the entity (if it exists)
     * @return graphic, possible null
     */
    @Nullable Graphic<G> update(E e, @Nullable Rectangle2D bounds, @Nullable Graphic<G> existing);

}

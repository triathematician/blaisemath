package com.googlecode.blaisemath.style;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2019 Elisha Peterson
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

import com.google.common.collect.Sets;
import org.checkerframework.checker.nullness.qual.Nullable;
import java.util.Set;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Arrays.asList;

/**
 * Maintains multiple types of styles within a single context, and also
 * contains logic for modifying the style attribute sets based on "hints".
 *
 * @author Elisha Peterson
 */
public final class StyleContext {
    
    /** Parent context. */
    private final @Nullable StyleContext parent;
    /** Modifiers that apply to the styles in this context. */
    private final Set<StyleModifier> modifiers = Sets.newHashSet();

    public StyleContext() {
        this(null);
    }

    public StyleContext(@Nullable StyleContext parent) {
        this.parent = parent;
    }    
    
    //region PROPERTIES
    
    /**
     * Get collection of style types supported by this context, not including
     * types supported by the parent.
     * @return types
     */
    public Set<StyleModifier> getModifiers() {
        return modifiers;
    }

    /**
     * Add new modifier.
     * @param mod modifier
     * @return true if changed
     */
    public boolean addModifier(StyleModifier mod) {
        return modifiers.add(mod);
    }
    /**
     * Remove modifier.
     * @param mod modifier
     * @return true if removed
     */
    public boolean removeModifier(StyleModifier mod) {
        return modifiers.remove(mod);
    }
    
    //endregion
    
    /**
     * Get collection of style types supported by this context, including
     * types supported by the parent context.
     * @return types
     */
    public Set<StyleModifier> getAllModifiers() {
        return parent != null ? Sets.union(parent.getAllModifiers(), modifiers) : modifiers;
    }

    /**
     * Applies all modifiers in this context to the given style, returning the result.
     * @param style the style to modify
     * @param hints the hints to apply
     * @return the modified style
     */
    public AttributeSet applyModifiers(AttributeSet style, String... hints) {
        return applyModifiers(style, Sets.newLinkedHashSet(asList(hints)));
    }

    /**
     * Applies all modifiers in this context to the given style, returning the result.
     * @param style the style to modify
     * @param hints the hints to apply
     * @return the modified style
     */
    public AttributeSet applyModifiers(AttributeSet style, Set<String> hints) {
        checkNotNull(style);
        AttributeSet res = style;
        for (StyleModifier mod : getAllModifiers()) {
            res = mod.apply(res, hints);
        }
        return res;
    }

}

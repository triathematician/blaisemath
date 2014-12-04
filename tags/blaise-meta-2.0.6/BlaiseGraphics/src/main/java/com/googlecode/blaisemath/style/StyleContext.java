/*
 * StyleContext.java
 * Created Jan 22, 2011
 */
package com.googlecode.blaisemath.style;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
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

/**
 * <p>
 *   Maintains multiple types of styles within a single context, and also
 *   contains logic for modifying the style attribute sets based on "hints".
 * </p>
 *
 * @author Elisha Peterson
 */
public final class StyleContext {
    
    /** Parent context. */
    private final Optional<StyleContext> parent;
    /** Modifiers that apply to the styles in this context. */
    private final Set<StyleModifier> modifiers = Sets.newHashSet();

    public StyleContext() {
        this(null);
    }

    public StyleContext(@Nullable StyleContext parent) {
        this.parent = Optional.fromNullable(parent);
    }    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY/COMPOSITE PATTERNS">
    //
    // PROPERTY/COMPOSITE PATTERNS
    //
    
    /**
     * Get collection of style types supported by this context, not including
     * types supported by the parent.
     * @return types
     */
    public Set<StyleModifier> getModifiers() {
        return modifiers;
    }
    
    public boolean addModifier(StyleModifier mod) {
        return modifiers.add(mod);
    }
    
    public boolean removeModifier(StyleModifier mod) {
        return modifiers.remove(mod);
    }
    
    //</editor-fold>
    
    /**
     * Get collection of style types supported by this context, including
     * types supported by the parent context.
     * @return types
     */
    public Set<StyleModifier> getAllModifiers() {
        return parent.isPresent() 
                ? Sets.union(parent.get().getAllModifiers(), modifiers)
                : modifiers;
    }
    
    /**
     * Applies all modifiers in this context to the given style, returning the result.
     * @param style the style to modify
     * @param hints the hints to apply
     * @return the modified style
     */
    @Nonnull
    public AttributeSet applyModifiers(AttributeSet style, AttributeSet hints) {
        checkNotNull(style);
        for (StyleModifier mod : getAllModifiers()) {
            style = mod.apply(style, hints);
        }
        return style;
    }

}

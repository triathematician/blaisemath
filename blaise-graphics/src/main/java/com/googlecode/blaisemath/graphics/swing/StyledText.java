package com.googlecode.blaisemath.graphics.swing;

import com.google.common.annotations.Beta;
import com.googlecode.blaisemath.graphics.AnchoredText;
import com.googlecode.blaisemath.style.AttributeSet;

/**
 * Augments {@link AnchoredText} with style information.
 * @author Elisha Peterson
 */
@Beta
public class StyledText {

    private final AnchoredText text;
    private final AttributeSet style;

    public StyledText(AnchoredText text, AttributeSet style) {
        this.text = text;
        this.style = style;
    }

    public AnchoredText getText() {
        return text;
    }

    public AttributeSet getStyle() {
        return style;
    }

}

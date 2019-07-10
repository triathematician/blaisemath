package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * SimpleStyle.java
 * edu.jhuapl.vis:vis-utils
 * %%
 * Copyright (C) 2016 - 2019 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */
import com.googlecode.blaisemath.style.Marker;
import java.awt.Color;
import javax.annotation.Nullable;

/**
 * Bean that tracks a key/name associated with a color (and optionally a marker).
 * 
 * @author petereb1
 */
public class KeyColorBean {
    
    private @Nullable String name;
    private @Nullable Color color;
    private @Nullable Marker marker;

    /**
     * Create bean with name and color
     * @param name style name
     * @param col color
     * @return style
     */
    public static KeyColorBean create(@Nullable String name, @Nullable Color col) {
        KeyColorBean res = new KeyColorBean();
        res.setName(name);
        res.setColor(col);
        return res;
    }

    /**
     * Get style for a color.
     * @param col color
     * @return style
     */
    public static KeyColorBean create(@Nullable Color col) {
        KeyColorBean res = new KeyColorBean();
        res.setColor(col);
        return res;
    }
    
    /**
     * Get style for a color.
     * @param mark marker
     * @return style
     */
    public static KeyColorBean create(@Nullable Marker mark) {
        KeyColorBean res = new KeyColorBean();
        res.setMarker(mark);
        return res;
    }
    
    /**
     * Creates copy of another scale.
     * @param style what to copy
     * @return copy
     */
    public static KeyColorBean copyOf(KeyColorBean style) {
        KeyColorBean res = new KeyColorBean();
        res.name = style.name;
        res.color = style.color;
        res.marker = style.marker;
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public @Nullable String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public @Nullable Color getColor() {
        return color;
    }

    public void setColor(@Nullable Color color) {
        this.color = color;
    }

    public @Nullable Marker getMarker() {
        return marker;
    }

    public void setMarker(@Nullable Marker marker) {
        this.marker = marker;
    }
    
    //</editor-fold>
}

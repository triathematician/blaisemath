package com.googlecode.blaisemath.palette.ui;

/*-
 * #%L
 * ******************************* UNCLASSIFIED *******************************
 * ColorListModel.java
 * edu.jhuapl.vis:conjecture-legacy
 * %%
 * Copyright (C) 2012 - 2017 Johns Hopkins University Applied Physics Laboratory
 * %%
 * (c) Johns Hopkins University Applied Physics Laboratory. All Rights Reserved.
 * JHU-APL Proprietary Information. Do not disseminate without prior approval.
 * #L%
 */
import com.google.common.collect.Maps;
import com.googlecode.blaisemath.palette.Palette;
import com.googlecode.blaisemath.palette.Palettes;
import com.googlecode.blaisemath.style.Marker;
import java.awt.Color;
import static java.util.Collections.list;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import javax.swing.DefaultListModel;

/**
 * List model for colors associated with strings.
 * @author petereb1
 */
public class ColorListModel extends DefaultListModel<KeyColorBean> {
    
    /**
     * Construct color list model from given palette.
     * @param p palette
     * @return list model
     */
    public static ColorListModel create(Palette p) {
        ColorListModel res = new ColorListModel();
        res.setColors(p);
        return res;
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    
    public List<KeyColorBean> getColors() {
        return list(elements());
    }
    
    public void setColors(List<KeyColorBean> colors) {
        clear();
        for (KeyColorBean c : colors) {
            addElement(c);
        }
    }
    
    public void setColors(Palette pal) {
        setColorMap(Palettes.colorMap(pal));
    }
    
    public Map<String, Color> getColorMap() {
        LinkedHashMap<String, Color> res = Maps.newLinkedHashMap();
        for (KeyColorBean en : list(elements())) {
            res.put(en.getName(), en.getColor());
        }
        return res;
    }
    
    public void setColorMap(Map<String, Color> cols) {
        clear();
        for (Entry<String, Color> en : cols.entrySet()) {
            addElement(KeyColorBean.create(en.getKey(), en.getValue()));
        }
    }

    //</editor-fold>
    
    public @Nullable String name(int index) {
        return index >= 0 && index < size() ? get(index).getName(): null;
    }
    
    public @Nullable Color color(int index) {
        return index >= 0 && index < size() ? get(index).getColor(): null;
    }
    
    public @Nullable Marker marker(int index) {
        return index >= 0 && index < size() ? get(index).getMarker(): null;
    }
    
}

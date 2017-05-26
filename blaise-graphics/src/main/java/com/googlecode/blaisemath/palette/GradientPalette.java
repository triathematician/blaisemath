package com.googlecode.blaisemath.palette;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.Sets;
import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * #%L
 * BlaiseGraphics
 * --
 * Copyright (C) 2009 - 2017 Elisha Peterson
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
 * Describes a gradient scale, a linear interpolation of two or more colors.
 * 
 * @author Elisha Peterson
 * @since 2.3.0
 */
public final class GradientPalette {

    private static final Logger LOG = Logger.getLogger(GradientPalette.class.getName());
    
    private String name;
    private TreeSet<GradientColorStop> stops = new TreeSet<>();
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<GradientColorStop> getStops() {
        return stops;
    }

    public void setStops(Set<GradientColorStop> stops) {
        this.stops = Sets.newTreeSet(stops);
    }    
    
    //</editor-fold>
    
    public Color color(float val) {
        checkArgument(val >= 0 && val <= 1);
        GradientColorStop toFind = stop(null, val);
        GradientColorStop floor = stops.floor(toFind);
        GradientColorStop ceil = stops.ceiling(toFind);
        
        if (floor == null && ceil == null) {
            LOG.log(Level.WARNING, "Must have at least one stop to compute color. Returning null.");
            return null;
        } else if (floor == null) {
            return ceil.getColor();
        } else if (ceil == null) {
            return floor.getColor();
        } else {
            float pct = ceil.getStop() == floor.getStop() ? 0f
                    : (val - floor.getStop()) / (ceil.getStop() - floor.getStop());
            return interpolate(floor.getColor(), pct, ceil.getColor());
        }
    }
    
    public Paint linearGradientPaint(Point2D start, Point2D stop) {
        Color[] colors = new Color[stops.size()];
        float[] fstops = new float[colors.length];
        int i = 0;
        for (GradientColorStop s : stops) {
            colors[i] = s.getColor();
            fstops[i] = s.getStop();
            i++;
        }
        if (colors.length == 1) {
            return colors[0];
        } else if (colors.length == 0) {
            LOG.log(Level.WARNING, "No colors have been configured");
            return Color.gray;
        }
        
        return new LinearGradientPaint(start, stop, fstops, colors);
    }
    
    private static Color interpolate(Color c1, float pct, Color c2) {
        float apct = 1-pct;
        int r = (int) (c1.getRed()*pct + c2.getRed()*apct);
        int g = (int) (c1.getGreen()*pct + c2.getGreen()*apct);
        int b = (int) (c1.getBlue()*pct + c2.getBlue()*apct);
        int a = (int) (c1.getAlpha()*pct + c2.getAlpha()*apct);
        return new Color(r, g, b, a);
    }

    public boolean addStop(GradientColorStop stop) {
        return stops.add(stop);
    }

    public boolean removeStop(GradientColorStop stop) {
        return stops.remove(stop);
    }
    
    public static GradientColorStop stop(Color color, float pos) {
        return new GradientColorStop(color, pos);
    }
    
    /** A stop in the gradient */
    public static class GradientColorStop implements Comparable<GradientColorStop> {
        private Color color;
        private float stop;

        public GradientColorStop() {
        }

        private GradientColorStop(Color color, float pos) {
            this.color = color;
            this.stop = pos;
        }

        public Color getColor() {
            return color;
        }

        public void setColor(Color color) {
            this.color = color;
        }

        public float getStop() {
            return stop;
        }

        public void setStop(float stop) {
            this.stop = stop;
        }

        @Override
        public int compareTo(GradientColorStop o) {
            return (int) Math.signum(stop - o.stop);
        }
    }
    
}

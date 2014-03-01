/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.blaise.pr;

/*
 * #%L
 * BlaiseWidgets
 * --
 * Copyright (C) 2009 - 2014 Elisha Peterson
 * --
 * Licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
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


import com.google.common.base.Function;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.blaise.graphics.BasicPointGraphic;
import org.blaise.graphics.BasicShapeGraphic;
import org.blaise.graphics.BasicTextGraphic;
import org.blaise.graphics.GraphicComponent;
import org.blaise.graphics.GraphicComposite;
import org.blaise.pr.PrModel.PrEntry;
import org.blaise.pr.Units.Distance;
import org.blaise.pr.Units.Speed;
import org.blaise.style.Anchor;
import org.blaise.style.Markers;
import org.blaise.style.PointStyleBasic;
import org.blaise.style.Styles;
import org.blaise.style.TextStyleBasic;

/**
 *
 * @author Elisha
 */
public class PrVis extends GraphicComponent {
    
    private PrModel model = new PrModel();
    
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTY PATTERNS">
    //
    // PROPERTY PATTERNS
    //
    
    public PrModel getModel() {
        return model;
    }

    public void setModel(PrModel model) {
        this.model = model;
        updateGraphics();
    }
    
    //</editor-fold>

    
    private void updateGraphics() {
        // get list of entries in sorted order
        List<PrEntry> entries = Lists.newArrayList(model.getEntries());
        Collections.sort(entries, new Comparator<PrEntry>(){
            public int compare(PrEntry t, PrEntry t1) {
                int dateCompare = t.getDate().compareTo(t1.getDate());
                return dateCompare;
            }
        });
        
        // now create and populate three sets of entries: PRs, former PRs, and non-PRs
        List<PrEntry> prs = Lists.newArrayList();
        List<PrEntry> formerPrs = Lists.newArrayList();
        List<PrEntry> nonPrs = Lists.newArrayList();
        
        
        //<editor-fold defaultstate="collapsed" desc="populate the lists">
        // populate the lists
        for (PrEntry en : entries) {
            // determine whether any PR's strictly beat this entry
            boolean strictlyBeaten = false;
            for (PrEntry pr : prs) {
                if (firstBetter(pr, en)) {
                    strictlyBeaten = true;
                    break;
                }
            }
            
            // if strictly beaten, add to non-PRs            
            if (strictlyBeaten) {
                nonPrs.add(en);
            } else {
                // see what PR's were beaten by this entry
                List<PrEntry> beatenPrs = Lists.newArrayList();
                for (PrEntry pr : prs) {
                    if (firstBetter(en, pr)) {
                        beatenPrs.add(pr);
                    }
                }
                
                // move beaten PR's to list of old PR's
                prs.removeAll(beatenPrs);
                formerPrs.addAll(beatenPrs);
                
                // add entry to PR list
                prs.add(en);
            }
        }
        //</editor-fold>
        
        // set up color map
        Function<Date,Color> cMap = new Function<Date,Color>(){
            long min = model.getMinDate().getTime()/1000/60/60/24;
            long max = model.getMaxDate().getTime()/1000/60/60/24;
            public Color apply(Date d) {
                long t = d.getTime()/1000/60/60/24;
                float pct = (t-min)/(float)(max-min);
                return Color.getHSBColor(.5f*pct, 1, .9f);
            }
        };
        Function<Date,Color> cMap2 = new Function<Date,Color>(){
            long min = model.getMinDate().getTime()/1000/60/60/24;
            long max = model.getMaxDate().getTime()/1000/60/60/24;
            public Color apply(Date d) {
                long t = d.getTime()/1000/60/60/24;
                float pct = (t-min)/(float)(max-min);
                return Color.getHSBColor(.5f*pct, .3f, .9f);
            }
        };
        
        // set up x and y maps
        final Rectangle2D bounds = new Rectangle2D.Double(50, 50, 500, 500);
        final double x0 = bounds.getMinX();
        final double y0 = bounds.getMinY();
        final double xmin = 0; //model.getMinDistance().getValue();
        final double xmax = model.getMaxDistance().getValue();
        final double xwid = bounds.getWidth();
        Function<Distance,Double> xMap = new Function<Distance,Double>(){
            public Double apply(Distance f) {
                double pct = (f.getValue()-xmin)/(xmax-xmin);
                return x0+pct*xwid;
            }            
        };        
        final double ymax = Math.floor(model.getMinSpeed().getValue());
        final double ymin = 45; // model.getMaxSpeed().getValue();
        final double yht = bounds.getHeight();
        Function<Speed,Double> yMap = new Function<Speed,Double>(){
            public Double apply(Speed f) {
                double pct = (f.getValue()-ymin)/(ymax-ymin);
                return y0+pct*yht;
            }
        };
        
        // supporting gfx
        clearGraphics();
        addGraphic(new BasicShapeGraphic(bounds, Styles.fillStroke(Color.black, null)));
        for (double d = xmin; d <= xmax; d += 100) {
            double y = x0+xwid*(d-xmin)/(xmax-xmin);
            addGraphic(new BasicShapeGraphic(new Line2D.Double(43, y, 50, y)));
            addGraphic(new BasicTextGraphic(new Point2D.Double(42, y), ((int)d)+"yd", new TextStyleBasic().textAnchor(Anchor.EAST)));
        }
        for (double s = ymin; s >= ymax; s--) {
            double x = y0+yht*(s-ymin)/(ymax-ymin);
            addGraphic(new BasicShapeGraphic(new Line2D.Double(x, 43, x, 50)));
            addGraphic(new BasicTextGraphic(new Point2D.Double(x, 42), ((int)s)+"s", new TextStyleBasic().textAnchor(Anchor.SOUTH)));
        }
        
        // shape gfx
        Area filled = new Area();
        for (PrEntry en : entries) {
            double x = xMap.apply(en.getDistance());
            double y = yMap.apply(en.getSpeed());
            Point2D.Double pt = new Point2D.Double(y,x);
            if (prs.contains(en)) {
                Rectangle2D shape = new Rectangle2D.Double(y0, x0, y-y0, x-x0);
                Area a = new Area(shape);
                a.subtract(filled);
                filled.add(new Area(shape));
                GraphicComposite prGr = new GraphicComposite();
                Color c = cMap.apply(en.getDate());
                prGr.addGraphic(new BasicShapeGraphic(a, Styles.fillStroke(alpha(c,192), c.brighter())));
                prGr.addGraphic(new BasicPointGraphic(pt, new PointStyleBasic().fill(c).stroke(c.darker())));                
                prGr.setDefaultTooltip(en+"");
                addGraphic(prGr);
            } else if (formerPrs.contains(en)) {
                Rectangle2D shape = new Rectangle2D.Double(y0, x0, y-y0, x-x0);
                Area a = new Area(shape);
                a.subtract(filled);
                filled.add(new Area(shape));
                GraphicComposite prGr = new GraphicComposite();
                Color c = cMap2.apply(en.getDate());
                prGr.addGraphic(new BasicShapeGraphic(a, Styles.fillStroke(alpha(c,192), c.brighter())));
                prGr.addGraphic(new BasicPointGraphic(pt, new PointStyleBasic().fill(c).stroke(c.darker())));                
                prGr.setDefaultTooltip(en+"");
                addGraphic(prGr);
            } else {
                BasicPointGraphic bpg = new BasicPointGraphic(pt, new PointStyleBasic().stroke(cMap.apply(en.getDate()).darker()).marker(Markers.CROSS));
                bpg.setDefaultTooltip(en+"");
                addGraphic(bpg);
            }         
        }
    }

    /** Ordering of PR's */
    private boolean firstBetter(PrEntry first, PrEntry second) {
        return first.getSpeed().compareTo(second.getSpeed()) < 0
                && first.getDistance().compareTo(second.getDistance()) >= 0;
    }
    
    private static Color alpha(Color c, int a) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
    }
    
    
    
    
}

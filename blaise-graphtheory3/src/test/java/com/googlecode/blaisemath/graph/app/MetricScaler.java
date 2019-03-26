/**
 * MetricScaler.java
 * Created Jan 2016
 */
package com.googlecode.blaisemath.graph.app;

/*
 * #%L
 * BlaiseGraphTheory (v3)
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


import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphNodeMetric;
import com.googlecode.blaisemath.style.AttributeSet;
import com.googlecode.blaisemath.style.Styles;
import java.util.Map;

/**
 * Scales nodes based on a metric value, and provides associated style.
 * @author elisha
 */
public class MetricScaler<T extends Number & Comparable> implements Function<Object,AttributeSet> {
    
    private Graph graph;
    private GraphNodeMetric<T> metric;
    private Map<Object,T> scores;
    private double min;
    private double max;
    
    private AttributeSet defStyle = Styles.defaultPointStyle();
    private float unkRad = 1f;
    private float minRad = 2f;
    private float maxRad = 10f;
    
    private void recompute() {
        scores = Maps.newLinkedHashMap();
        if (graph == null || metric == null) {
            return;
        }
        for (Object n : graph.nodes()) {
            scores.put(n, (T) metric.apply(graph, n));
        }
        min = Ordering.natural().min(scores.values()).doubleValue();
        max = Ordering.natural().max(scores.values()).doubleValue();
    }
    
    //<editor-fold defaultstate="collapsed" desc="PROPERTIES">

    public Graph getGraph() {
        return graph;
    }

    public void setGraph(Graph graph) {
        if (this.graph != graph) {
            this.graph = graph;
            recompute();
        }
    }

    public GraphNodeMetric getMetric() {
        return metric;
    }

    public void setMetric(GraphNodeMetric metric) {
        if (this.metric != metric) {
            this.metric = metric;
            recompute();
        }
    }
    
    //</editor-fold>

    @Override
    public AttributeSet apply(Object input) {
        if (scores.isEmpty()) {
            return defStyle;
        } else {
            return AttributeSet.createWithParent(defStyle)
                    .and(Styles.MARKER_RADIUS, radScale(input));
        }
    }
    
    private double radScale(Object input) {
        Number nScore = scores.get(input);
        if (nScore == null) {
            return unkRad;
        } else if (min == max) {
            return .5*(minRad+maxRad);
        }
        double score = nScore.doubleValue();
        return minRad + (maxRad-minRad)*Math.sqrt(score-min)/Math.sqrt(max-min);
    }
    
}

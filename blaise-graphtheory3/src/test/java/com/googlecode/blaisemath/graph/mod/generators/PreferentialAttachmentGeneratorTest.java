package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2018 Elisha Peterson
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

import com.google.common.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator.PreferentialAttachmentParameters;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PreferentialAttachmentGeneratorTest {

    @Test
    public void testGetSeededInstance_fixed_add_number() {
        Graph<Integer> seed = null;
        while (seed == null || seed.edges().size() == 0) {
            seed = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodParameters(false, 4, .5f));
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        Graph<Integer> pref = new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(seed, 10, 1));
        System.out.println("    result: " + pref.edges().size() + " edges, " + GraphUtils.printGraph(pref));
        assertEquals(seed.edges().size() + (pref.nodes().size() - seed.nodes().size()), pref.edges().size());

        seed = new CycleGraphGenerator().apply(new DefaultGeneratorParameters(false, 4));
        pref = new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(seed, 10, 2));
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        System.out.println("    result: " + pref.edges().size() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edges().size() + 2 * (pref.nodes().size() - seed.nodes().size())) + " edges or less");

        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().apply(new DefaultGeneratorParameters(false, 5)), 20, -1));
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().apply(new DefaultGeneratorParameters(true, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment from directed graph.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().apply(new DefaultGeneratorParameters(false, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetSeededInstance_variable_add_number() {
        Graph<Integer> seed = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodParameters(false, 4, .5f));
        while (seed.edges().size() == 0) {
            seed = new EdgeLikelihoodGenerator().apply(new EdgeLikelihoodParameters(false, 4, .5f));
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(seed, 10, probs1));
        System.out.println("    result (probs .5, .5): " + pref.edges().size() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edges().size() + probs1[1] * (pref.nodes().size() - seed.nodes().size())) + " edges");

        seed = new CycleGraphGenerator().apply(new DefaultGeneratorParameters(false, 4));
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(seed, 10, probs2));
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edges().size() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edges().size() + (probs2[1] + 2 * probs2[2] + 3 * probs2[3]) * (pref.nodes().size() - seed.nodes().size())) + " edges");

        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().apply(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.25f, 0f, -1f}));
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().apply(new DefaultGeneratorParameters(true, 5)), 20, new float[]{.5f, .5f}));
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().apply(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().apply(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.5f, .5f}));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {
        }
    }

}

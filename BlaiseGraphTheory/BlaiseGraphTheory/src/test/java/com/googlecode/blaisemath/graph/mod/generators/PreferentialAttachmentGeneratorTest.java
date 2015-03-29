/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.googlecode.blaisemath.graph.mod.generators;

/*
 * #%L
 * BlaiseGraphTheory
 * --
 * Copyright (C) 2009 - 2015 Elisha Peterson
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
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator;
import com.googlecode.blaisemath.graph.mod.generators.DefaultGeneratorParameters;
import com.googlecode.blaisemath.graph.mod.generators.EmptyGraphGenerator;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator;
import com.googlecode.blaisemath.graph.mod.generators.CycleGraphGenerator;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator.PreferentialAttachmentParameters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class PreferentialAttachmentGeneratorTest {

    @Test
    public void testGetSeededInstance_fixed_add_number() {
        System.out.println("getSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = null;
        while (seed == null || seed.edgeCount() == 0) {
            seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        Graph<Integer> pref = new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(seed, 10, 1));
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        assertEquals(seed.edgeCount() + (pref.nodeCount() - seed.nodeCount()), pref.edgeCount());

        seed = new CycleGraphGenerator().generate(new DefaultGeneratorParameters(false, 4));
        pref = new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(seed, 10, 2));
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount() + 2 * (pref.nodeCount() - seed.nodeCount())) + " edges or less");

        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, -1));
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().generate(new DefaultGeneratorParameters(true, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment from directed graph.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testGetSeededInstance_variable_add_number() {
        System.out.println("getSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        while (seed.edgeCount() == 0) {
            seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(seed, 10, probs1));
        System.out.println("    result (probs .5, .5): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount() + probs1[1] * (pref.nodeCount() - seed.nodeCount())) + " edges");

        seed = new CycleGraphGenerator().generate(new DefaultGeneratorParameters(false, 4));
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(seed, 10, probs2));
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount() + (probs2[1] + 2 * probs2[2] + 3 * probs2[3]) * (pref.nodeCount() - seed.nodeCount())) + " edges");

        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.25f, 0f, -1f}));
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new CycleGraphGenerator().generate(new DefaultGeneratorParameters(true, 5)), 20, new float[]{.5f, .5f}));
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {
        }
        try {
            new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                    new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.5f, .5f}));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {
        }
    }

}

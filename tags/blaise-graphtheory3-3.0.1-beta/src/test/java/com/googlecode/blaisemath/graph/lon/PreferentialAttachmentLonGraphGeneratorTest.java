/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.lon;

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

import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import com.googlecode.blaisemath.graph.mod.generators.CycleGraphGenerator;
import com.googlecode.blaisemath.graph.mod.generators.DefaultGeneratorParameters;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator;
import com.googlecode.blaisemath.graph.mod.generators.EdgeLikelihoodGenerator.EdgeLikelihoodParameters;
import com.googlecode.blaisemath.graph.mod.generators.EmptyGraphGenerator;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator;
import com.googlecode.blaisemath.graph.mod.generators.PreferentialAttachmentGenerator.PreferentialAttachmentParameters;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class PreferentialAttachmentLonGraphGeneratorTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- PreferentialAttachmentTest --");
    }

    @Test
    public void testGetLongitudinalSeededInstance_fixed_add_number() {
        System.out.println("getLongitudinalSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        while (seed.edgeCount() == 0) {
            seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        LonGraph<Integer> pref = new PreferentialAttachmentLonGraphGenerator().generate(new PreferentialAttachmentParameters(seed, 10, 1));
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        assertEquals(seed.edgeCount()+(slice.nodeCount()-seed.nodeCount()), slice.edgeCount());

        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, -1));
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new CycleGraphGenerator().generate(new DefaultGeneratorParameters(true, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, 1));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}

    }

    @Test
    public void testGetLongitudinalSeededInstance_variable_add_number() {
        System.out.println("getLongitudinalSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeLikelihoodGenerator().generate(new EdgeLikelihoodParameters(false, 4, .5f));
        System.out.println("  SEEDED with 4 vertex random graph, connection probs (.5,.5): " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        LonGraph<Integer> pref = new PreferentialAttachmentLonGraphGenerator().generate(new PreferentialAttachmentParameters(seed, 10, probs1));
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(slice.nodeCount()-seed.nodeCount())) + " edges");

        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.25f,0f,-1f}));
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new CycleGraphGenerator().generate(new DefaultGeneratorParameters(true, 5)), 20, new float[]{.5f,.5f}));
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGenerator().generate(new PreferentialAttachmentParameters(
                new EmptyGraphGenerator().generate(new DefaultGeneratorParameters(false, 5)), 20, new float[]{.5f,.5f}));
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}
    }

}

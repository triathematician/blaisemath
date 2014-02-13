/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.modules;

import org.blaise.graph.Graph;
import org.blaise.graph.GraphSuppliers;
import org.blaise.graph.GraphUtils;
import org.blaise.graph.longitudinal.LongitudinalGraph;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class PreferentialAttachmentLongitudinalGraphSupplierTest {

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- PreferentialAttachmentTest --");
    }

    @Test
    public void testGetLongitudinalSeededInstance_fixed_add_number() {
        System.out.println("getLongitudinalSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityGraphSupplier(false, 4, .5f).get();
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        LongitudinalGraph<Integer> pref = new PreferentialAttachmentLongitudinalGraphSupplier(seed, 10, 1).get();
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        assertEquals(seed.edgeCount()+(slice.nodeCount()-seed.nodeCount()), slice.edgeCount());

        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphBuilder(false, 5).get(), 20, -1).get();
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.CycleGraphBuilder(true, 5).get(), 20, 1).get();
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphBuilder(false, 5).get(), 20, 1).get();
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}

    }

    @Test
    public void testGetLongitudinalSeededInstance_variable_add_number() {
        System.out.println("getLongitudinalSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityGraphSupplier(false, 4, .5f).get();
        System.out.println("  SEEDED with 4 vertex random graph, connection probs (.5,.5): " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        LongitudinalGraph<Integer> pref = new PreferentialAttachmentLongitudinalGraphSupplier(seed, 10, probs1).get();
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(slice.nodeCount()-seed.nodeCount())) + " edges");

        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphBuilder(false, 5).get(), 20, new float[]{.25f,0f,-1f}).get();
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.CycleGraphBuilder(true, 5).get(), 20, new float[]{.5f,.5f}).get();
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphBuilder(false, 5).get(), 20, new float[]{.5f,.5f}).get();
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}
    }

}
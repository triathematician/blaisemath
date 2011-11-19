/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

import org.bm.blaise.scio.graph.time.TimeGraph;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author elisha
 */
public class PreferentialAttachmentTest {

    public PreferentialAttachmentTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("-- PreferentialAttachmentTest --");
    }

    @Test
    public void testGetSeededInstance_fixed_add_number() {
        System.out.println("getSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = RandomGraph.getInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        Graph<Integer> pref = PreferentialAttachment.getSeededInstance(seed, 10, 1);
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        assertEquals(seed.edgeCount()+(pref.order()-seed.order()), pref.edgeCount());

        seed = GraphFactory.getCycleGraph(4, false);
        pref = PreferentialAttachment.getSeededInstance(seed, 10, 2);
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+2*(pref.order()-seed.order())) + " edges or less");

        try { PreferentialAttachment.getSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, -1);
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getSeededInstance(GraphFactory.getCycleGraph(5, true), 20, 1);
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, 1);
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetSeededInstance_variable_add_number() {
        System.out.println("getSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = RandomGraph.getInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = PreferentialAttachment.getSeededInstance(seed, 10, probs1);
        System.out.println("    result (probs .5, .5): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(pref.order()-seed.order())) + " edges");

        seed = GraphFactory.getCycleGraph(4, false);
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = PreferentialAttachment.getSeededInstance(seed, 10, probs2);
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+(probs2[1]+2*probs2[2]+3*probs2[3])*(pref.order()-seed.order())) + " edges");

        try { PreferentialAttachment.getSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, new float[]{.25f,0f,-1f});
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getSeededInstance(GraphFactory.getCycleGraph(5, true), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetLongitudinalSeededInstance_fixed_add_number() {
        System.out.println("getLongitudinalSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = RandomGraph.getInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        TimeGraph<Integer> pref = PreferentialAttachment.getLongitudinalSeededInstance(seed, 10, 1);
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        assertEquals(seed.edgeCount()+(slice.order()-seed.order()), slice.edgeCount());

        try { PreferentialAttachment.getLongitudinalSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, -1);
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getLongitudinalSeededInstance(GraphFactory.getCycleGraph(5, true), 20, 1);
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getLongitudinalSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, 1);
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}

    }

    @Test
    public void testGetLongitudinalSeededInstance_variable_add_number() {
        System.out.println("getLongitudinalSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = RandomGraph.getInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph, connection probs (.5,.5): " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        TimeGraph<Integer> pref = PreferentialAttachment.getTimeSeededInstance(seed, 10, probs1);
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(slice.order()-seed.order())) + " edges");

        try { PreferentialAttachment.getTimeSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, new float[]{.25f,0f,-1f});
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getTimeSeededInstance(GraphFactory.getCycleGraph(5, true), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getTimeSeededInstance(GraphFactory.getEmptyGraph(5, false), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}
    }

}
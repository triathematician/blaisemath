/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.blaise.graph.modules;

import org.blaise.graph.Graph;
import org.blaise.graph.GraphBuilders;
import org.blaise.graph.GraphUtils;
import org.blaise.graph.dynamic.TimeGraph;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Test;

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

        Graph<Integer> seed = null;
        while (seed == null || seed.edgeCount() == 0) {
            seed = new EdgeProbabilityBuilder(false, 4, .5f).createGraph();
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        Graph<Integer> pref = new PreferentialAttachment(seed, 10, 1).createGraph();
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        assertEquals(seed.edgeCount()+(pref.nodeCount()-seed.nodeCount()), pref.edgeCount());

        seed = new GraphBuilders.CycleGraphBuilder(false, 4).createGraph();
        pref = new PreferentialAttachment(seed, 10, 2).createGraph();
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+2*(pref.nodeCount()-seed.nodeCount())) + " edges or less");

        try { new PreferentialAttachment(new GraphBuilders.CycleGraphBuilder(false, 5).createGraph(), 20, -1);
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.CycleGraphBuilder(true, 5).createGraph(), 20, 1);
            fail("Should not be able to construct preferential attachment from directed graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, 1);
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetSeededInstance_variable_add_number() {
        System.out.println("getSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityBuilder(false, 4, .5f).createGraph();
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = new PreferentialAttachment(seed, 10, probs1).createGraph();
        System.out.println("    result (probs .5, .5): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(pref.nodeCount()-seed.nodeCount())) + " edges");

        seed = new GraphBuilders.CycleGraphBuilder(false, 4).createGraph();
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = new PreferentialAttachment(seed, 10, probs2).createGraph();
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+(probs2[1]+2*probs2[2]+3*probs2[3])*(pref.nodeCount()-seed.nodeCount())) + " edges");

        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, new float[]{.25f,0f,-1f});
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.CycleGraphBuilder(true, 5).createGraph(), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetLongitudinalSeededInstance_fixed_add_number() {
        System.out.println("getLongitudinalSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityBuilder(false, 4, .5f).createGraph();
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        TimeGraph<Integer> pref = new PreferentialAttachment(seed, 10, 1).createTimeGraph();
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        assertEquals(seed.edgeCount()+(slice.nodeCount()-seed.nodeCount()), slice.edgeCount());

        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, -1).createTimeGraph();
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.CycleGraphBuilder(true, 5).createGraph(), 20, 1).createTimeGraph();
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, 1).createTimeGraph();
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}

    }

    @Test
    public void testGetLongitudinalSeededInstance_variable_add_number() {
        System.out.println("getLongitudinalSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityBuilder(false, 4, .5f).createGraph();
        System.out.println("  SEEDED with 4 vertex random graph, connection probs (.5,.5): " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        TimeGraph<Integer> pref = new PreferentialAttachment(seed, 10, probs1).createTimeGraph();
        Graph<Integer> slice = pref.slice(0.0, true);
        System.out.println("    result (t=0): " + slice.edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=3): " + (slice=pref.slice(3.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    result (t=6): " + (slice=pref.slice(6.0, true)).edgeCount() + " edges, " + GraphUtils.printGraph(slice));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(slice.nodeCount()-seed.nodeCount())) + " edges");

        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, new float[]{.25f,0f,-1f}).createTimeGraph();
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.CycleGraphBuilder(true, 5).createGraph(), 20, new float[]{.5f,.5f}).createTimeGraph();
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachment(new GraphBuilders.EmptyGraphBuilder(false, 5).createGraph(), 20, new float[]{.5f,.5f}).createTimeGraph();
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}
    }

}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph;

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

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testGetRandomInstance_fixed_add_number() {
        System.out.println("getRandomInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = Graphs.getRandomInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph: " + Graphs.printGraph(seed));
        Graph<Integer> pref = PreferentialAttachment.getRandomInstance(seed, 10, 1);
        System.out.println("    result: " + pref.edgeNumber() + " edges, " + Graphs.printGraph(pref));
        assertEquals(seed.edgeNumber()+(pref.order()-seed.order()), pref.edgeNumber());

        seed = Graphs.getCycleGraphInstance(4, false);
        pref = PreferentialAttachment.getRandomInstance(seed, 10, 2);
        System.out.println("  SEEDED with 4 vertex cycle graph: " + Graphs.printGraph(seed));
        System.out.println("    result: " + pref.edgeNumber() + " edges, " + Graphs.printGraph(pref));
        System.out.println("    expected " + (seed.edgeNumber()+2*(pref.order()-seed.order())) + " edges or less");

        try { PreferentialAttachment.getRandomInstance(Graphs.getEmptyGraphInstance(5, false), 20, -1);
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getRandomInstance(Graphs.getCycleGraphInstance(5, true), 20, 1);
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getRandomInstance(Graphs.getEmptyGraphInstance(5, false), 20, 1);
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetRandomInstance_variable_add_number() {
        System.out.println("getRandomInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = Graphs.getRandomInstance(4, .5f, false);
        System.out.println("  SEEDED with 4 vertex random graph: " + Graphs.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = PreferentialAttachment.getRandomInstance(seed, 10, probs1);
        System.out.println("    result (probs .5, .5): " + pref.edgeNumber() + " edges, " + Graphs.printGraph(pref));
        System.out.println("    expected " + (seed.edgeNumber()+probs1[1]*(pref.order()-seed.order())) + " edges");

        seed = Graphs.getCycleGraphInstance(4, false);
        System.out.println("  SEEDED with 4 vertex cycle graph: " + Graphs.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = PreferentialAttachment.getRandomInstance(seed, 10, probs2);
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edgeNumber() + " edges, " + Graphs.printGraph(pref));
        System.out.println("    expected " + (seed.edgeNumber()+(probs2[1]+2*probs2[2]+3*probs2[3])*(pref.order()-seed.order())) + " edges");

        try { PreferentialAttachment.getRandomInstance(Graphs.getEmptyGraphInstance(5, false), 20, new float[]{.25f,0f,-1f});
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getRandomInstance(Graphs.getCycleGraphInstance(5, true), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { PreferentialAttachment.getRandomInstance(Graphs.getEmptyGraphInstance(5, false), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

}
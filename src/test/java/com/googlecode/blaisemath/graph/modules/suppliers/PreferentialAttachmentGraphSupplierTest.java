/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.googlecode.blaisemath.graph.modules.suppliers;

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

import com.googlecode.blaisemath.graph.modules.suppliers.EdgeProbabilityGraphSupplier;
import com.googlecode.blaisemath.graph.modules.suppliers.PreferentialAttachmentGraphSupplier;
import com.googlecode.blaisemath.graph.Graph;
import com.googlecode.blaisemath.graph.GraphUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author elisha
 */
public class PreferentialAttachmentGraphSupplierTest {

    @Test
    public void testGetSeededInstance_fixed_add_number() {
        System.out.println("getSeededInstance (fixed # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = null;
        while (seed == null || seed.edgeCount() == 0) {
            seed = new EdgeProbabilityGraphSupplier(false, 4, .5f).get();
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        Graph<Integer> pref = new PreferentialAttachmentGraphSupplier(seed, 10, 1).get();
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        assertEquals(seed.edgeCount()+(pref.nodeCount()-seed.nodeCount()), pref.edgeCount());

        seed = new GraphSuppliers.CycleGraphSupplier(false, 4).get();
        pref = new PreferentialAttachmentGraphSupplier(seed, 10, 2).get();
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        System.out.println("    result: " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+2*(pref.nodeCount()-seed.nodeCount())) + " edges or less");

        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.CycleGraphSupplier(false, 5).get(), 20, -1);
            fail("Should not be able to construct preferential attachment with negative connection numbers.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.CycleGraphSupplier(true, 5).get(), 20, 1);
            fail("Should not be able to construct preferential attachment from directed graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphSupplier(false, 5).get(), 20, 1);
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

    @Test
    public void testGetSeededInstance_variable_add_number() {
        System.out.println("getSeededInstance (variable # edges/step): MANUALLY CHECK FOR DESIRED OUTPUT");

        Graph<Integer> seed = new EdgeProbabilityGraphSupplier(false, 4, .5f).get();
        while (seed.edgeCount() == 0) {
            seed = new EdgeProbabilityGraphSupplier(false, 4, .5f).get();
        }
        System.out.println("  SEEDED with 4 vertex random graph: " + GraphUtils.printGraph(seed));
        float[] probs1 = {.5f, .5f};
        Graph<Integer> pref = new PreferentialAttachmentGraphSupplier(seed, 10, probs1).get();
        System.out.println("    result (probs .5, .5): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+probs1[1]*(pref.nodeCount()-seed.nodeCount())) + " edges");

        seed = new GraphSuppliers.CycleGraphSupplier(false, 4).get();
        System.out.println("  SEEDED with 4 vertex cycle graph: " + GraphUtils.printGraph(seed));
        float[] probs2 = {0f, .5f, .25f, .25f};
        pref = new PreferentialAttachmentGraphSupplier(seed, 10, probs2).get();
        System.out.println("    result (probs 0, .5, .25, .25): " + pref.edgeCount() + " edges, " + GraphUtils.printGraph(pref));
        System.out.println("    expected " + (seed.edgeCount()+(probs2[1]+2*probs2[2]+3*probs2[3])*(pref.nodeCount()-seed.nodeCount())) + " edges");

        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphSupplier(false, 5).get(), 20, new float[]{.25f,0f,-1f});
            fail("Should not be able to construct preferential attachment with bad probability vector.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.CycleGraphSupplier(true, 5).get(), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment from undirected graph.");
        } catch (Exception ex) {}
        try { new PreferentialAttachmentGraphSupplier(new GraphSuppliers.EmptyGraphSupplier(false, 5).get(), 20, new float[]{.5f,.5f});
            fail("Should not be able to construct preferential attachment with empty graph.");
        } catch (Exception ex) {}        
    }

}

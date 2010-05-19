/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.bm.blaise.scio.graph.metrics;

import org.bm.blaise.scio.graph.SimpleGraph;
import org.bm.blaise.scio.graph.io.SimpleGraphIOTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ae3263
 */
public class GraphMetricsTest {

    static SimpleGraph SAMPLE1, SAMPLE2;

    public GraphMetricsTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        SAMPLE1 = SimpleGraphIOTest.sample1();
        SAMPLE2 = SimpleGraphIOTest.sample2();
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Test
    public void testDegreeMetric() {
        System.out.println("DEGREE");
        assertEquals("[1, 3, 2, 3, 3, 1, 4, 1, 6, 1, 3, 0, 3, 2, 4, 3]", GraphMetrics.computeValues(SAMPLE1, GraphMetrics.DEGREE).toString());
        assertEquals("{0=1, 1=4, 2=2, 3=6, 4=2, 6=1}", GraphMetrics.computeDistribution(SAMPLE1, GraphMetrics.DEGREE).toString());

        assertEquals("{1=75, 2=107, 3=66, 4=42, 5=28, 6=16, 7=20, 8=11, 9=12, 10=4, 11=10, 12=5, 13=3, 14=8, 15=6, 16=5, 17=3, 18=5, 19=3, 20=1, 21=3, 22=3, 23=1, 24=3, 26=2, 27=1, 28=2, 29=2, 30=5, 33=1, 34=3, 35=2, 40=3, 42=3, 44=1, 46=2, 48=1, 53=2, 59=1, 61=1, 62=1, 64=1, 66=1, 67=3, 68=1, 70=1, 72=1, 74=1, 75=2, 76=1, 84=1, 85=1, 89=1, 91=1, 94=1, 98=1, 109=1, 110=1, 114=2, 122=1, 130=1, 132=1, 136=1, 145=1}", GraphMetrics.computeDistribution(SAMPLE2, GraphMetrics.DEGREE).toString());
    }

    @Test
    public void testDegree2Metric() {
        System.out.println("DEGREE2");
        assertEquals("[6, 10, 9, 8, 6, 3, 9, 4, 11, 2, 6, 0, 11, 7, 8, 10]", GraphMetrics.computeValues(SAMPLE1, GraphMetrics.DEGREE2).toString());
        assertEquals("{0=1, 2=1, 3=1, 4=1, 6=3, 7=1, 8=2, 9=2, 10=2, 11=2}", GraphMetrics.computeDistribution(SAMPLE1, GraphMetrics.DEGREE2).toString());

        assertEquals("{2=4, 3=3, 4=6, 5=4, 6=1, 7=4, 8=1, 9=6, 11=5, 12=4, 13=6, 14=15, 15=10, 16=1, 17=3, 18=1, 19=3, 20=1, 22=1, 23=3, 28=4, 34=7, 35=1, 36=1, 37=1, 40=6, 43=1, 45=1, 46=5, 47=1, 56=1, 57=2, 58=1, 62=3, 67=4, 68=6, 69=1, 70=1, 74=1, 75=6, 76=6, 78=1, 79=1, 82=1, 84=4, 88=2, 89=2, 90=2, 91=2, 92=1, 94=4, 95=1, 97=1, 98=13, 102=2, 105=1, 107=1, 109=7, 110=11, 112=1, 113=2, 114=1, 115=2, 116=2, 118=1, 120=2, 122=15, 123=5, 124=5, 126=1, 129=1, 130=7, 131=5, 132=13, 133=4, 134=2, 135=1, 136=2, 138=1, 139=1, 140=5, 141=1, 142=1, 143=2, 144=2, 145=6, 147=1, 149=1, 150=12, 154=2, 155=3, 157=2, 158=2, 159=1, 160=1, 163=1, 164=1, 166=2, 167=2, 169=5, 170=1, 172=1, 175=2, 177=1, 178=1, 180=2, 181=4, 182=2, 183=1, 184=2, 185=1, 187=1, 188=1, 189=2, 190=2, 191=1, 194=2, 195=3, 197=2, 198=2, 200=2, 202=1, 203=3, 206=2, 209=1, 210=2, 211=1, 214=1, 215=2, 216=3, 217=1, 218=1, 221=1, 222=1, 224=1, 225=1, 227=1, 228=2, 229=4, 230=1, 231=2, 232=1, 234=2, 238=1, 240=1, 242=1, 248=1, 251=1, 252=1, 253=1, 254=1, 255=2, 256=1, 257=1, 258=1, 259=1, 260=1, 264=2, 267=2, 269=1, 272=1, 275=1, 278=1, 280=2, 281=1, 283=1, 287=2, 288=1, 289=1, 290=1, 294=1, 296=1, 298=1, 300=1, 302=1, 304=1, 305=1, 307=2, 312=1, 318=1, 319=1, 321=2, 327=2, 328=3, 330=1, 331=1, 333=1, 337=1, 338=1, 340=1, 345=1, 346=1, 348=2, 357=1, 358=1, 363=1, 364=2, 369=1, 372=1, 374=2, 375=1, 379=1, 380=2, 381=1, 382=1, 383=1, 385=1, 388=1, 389=2, 390=1, 391=1, 392=2, 395=1, 396=2, 397=1, 403=1, 404=1, 410=1, 412=1, 413=1, 414=1, 416=3, 417=1, 418=1, 422=1, 432=1, 441=1}", GraphMetrics.computeDistribution(SAMPLE2, GraphMetrics.DEGREE2).toString());
    }

    @Test
    public void testCliqueMetric() {
        System.out.println("CLIQUE_COUNT");
        assertEquals("[0, 0, 0, 1, 1, 0, 0, 0, 1, 0, 2, 0, 1, 0, 2, 1]", GraphMetrics.computeValues(SAMPLE1, GraphMetrics.CLIQUE_COUNT).toString());
        assertEquals("{0=100, 1=95, 2=27, 3=37, 4=13, 5=18, 6=14, 7=3, 8=7, 9=3, 10=13, 11=3, 12=1, 13=3, 14=1, 15=8, 16=3, 17=5, 18=1, 19=1, 20=5, 21=5, 22=2, 23=4, 24=1, 26=1, 27=1, 28=1, 29=3, 31=1, 34=1, 36=3, 37=1, 39=1, 40=1, 41=1, 43=2, 44=1, 47=2, 48=1, 49=1, 51=2, 54=1, 56=1, 58=1, 60=2, 63=1, 64=1, 65=1, 67=1, 74=1, 75=2, 88=2, 91=2, 94=1, 95=1, 100=1, 103=1, 109=1, 115=2, 116=1, 118=1, 131=1, 132=1, 133=1, 135=1, 141=1, 145=1, 146=1, 150=1, 157=1, 159=1, 178=1, 180=1, 185=1, 189=1, 196=1, 203=1, 213=1, 221=1, 222=1, 225=1, 242=2, 260=1, 268=1, 269=1, 280=1, 300=1, 302=2, 308=1, 341=1, 342=1, 347=1, 367=2, 373=1, 443=1, 448=1, 502=1, 572=1, 581=1, 595=1, 597=1, 604=1, 608=1, 670=1, 676=1, 678=1, 717=1, 738=1, 810=1, 863=1, 866=1, 896=1, 956=1, 964=1, 966=1, 972=2, 1075=1, 1082=1, 1105=1, 1110=1, 1184=1, 1206=1, 1259=1, 1261=1, 1272=1, 1295=1, 1313=1, 1316=1, 1431=1, 1498=1, 1530=1, 1618=1, 1643=1, 1733=1, 1808=1}", GraphMetrics.computeDistribution(SAMPLE1, GraphMetrics.CLIQUE_COUNT).toString());

        assertEquals("", GraphMetrics.computeDistribution(SAMPLE2, GraphMetrics.CLIQUE_COUNT).toString());
    }

    @Test
    public void testDecayCentrality() {
        System.out.println("DECAY_CENTRALITY");
        assertEquals("[2.5625, 3.75, 3.3125, 3.28125, 3.0625, 2.125, 3.8125, 2.15625, 4.625, 1.71875, 2.96875, 0.0, 3.875, 2.9375, 3.625, 3.75]", GraphMetrics.computeValues(SAMPLE1, new DecayCentrality(0.5)).toString());
        assertEquals("{0.0=1, 1.71875=1, 2.125=1, 2.15625=1, 2.5625=1, 2.9375=1, 2.96875=1, 3.0625=1, 3.28125=1, 3.3125=1, 3.625=1, 3.75=2, 3.8125=1, 3.875=1, 4.625=1}", GraphMetrics.computeDistribution(SAMPLE1, new DecayCentrality(0.5)).toString());

        assertEquals(109.03125, new DecayCentrality(.5).getValue(SAMPLE2, 10), 1e-6);
        assertEquals(4, new DecayCentrality(.5).getValue(SAMPLE2, 20), 1e-6);
        assertEquals(4, new DecayCentrality(.5).getValue(SAMPLE2, 30), 1e-6);
    }

}
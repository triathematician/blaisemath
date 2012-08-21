/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bm.blaise.scio.graph;

import java.awt.Color;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Sample graph with node & edge weights depending on numeric properties of integer vertices.
 *
 * @author elisha
 */
public class PrimeNumberGraph extends GraphSupport<Integer> implements WeightedGraph<Integer, Integer>, ValuedGraph<Integer, Integer> {

    int min;

    Collection<Set<Integer>> cpts;

    public PrimeNumberGraph(final int n, int minWt) {
        super(new AbstractList<Integer>() {
            @Override public Integer get(int index) { return index + 2; }
            @Override public int size() { return n - 1; }
        }, false);
        this.min = minWt;
        cpts = GraphUtils.components(this, nodes);
    }

    public Color getColor(Integer x) {
        List<Integer> pp = factors(x);
        int min = pp.isEmpty() ? -1 : pp.get(0);
        switch (min) {
            case -1:
                return Color.magenta;
            case 2:
                return Color.black;
            case 3:
                return Color.blue;
            case 5:
                return Color.green;
            case 7:
                return Color.orange;
            case 11:
                return Color.red;
            case 13:
                return Color.black;
            default:
                return Color.gray;
        }
    }

    public Integer getValue(Integer x) {
        return factors(x).size();
    }

    public void setValue(Integer x, Integer value) {
    }

    public Integer getWeight(Integer x, Integer y) {
        List<Integer> c = common(x, y);
        return c.size() < min ? 0 : c.get(min - 1);
    }

    public void setWeight(Integer x, Integer y, Integer value) {
    }

    public Collection<Set<Integer>> components() {
        return cpts;
    }

    @Override
    public boolean adjacent(Integer x, Integer y) {
        return nCommon(x, y) >= min;
    }

    public Set<Integer> neighbors(Integer x) {
        Set<Integer> result = new TreeSet<Integer>();
        for (Integer i : nodes()) {
            if (adjacent(i, x)) {
                result.add(i);
            }
        }
        return result;
    }

    public int edgeCount() {
        int sum = 0;
        for (Integer i : nodes()) {
            sum += degree(i);
        }
        return sum / 2;
    }

    /** Cached list of no common factors */
    static TreeMap<Integer, TreeSet<Integer>> noCommon = new TreeMap<Integer, TreeSet<Integer>>();

    /** Cached list of common factors */
    static TreeMap<Integer, TreeMap<Integer, Integer>> inCommon = new TreeMap<Integer, TreeMap<Integer, Integer>>();

    /** Comptues number of common factors */
    public static int nCommon(int n1, int n2) {
        int min = Math.min(n1, n2);
        int max = Math.max(n1, n2);
        if (noCommon.containsKey(min) && noCommon.get(min).contains(max)) {
            return 0;
        } else if (inCommon.containsKey(min) && inCommon.get(min).containsKey(max)) {
            return inCommon.get(min).get(max);
        } else {
            return common(n1, n2).size();
        }
    }

    /** Computes common factors */
    public static List<Integer> common(int n1, int n2) {
        int min = Math.min(n1, n2);
        int max = Math.max(n1, n2);
        if (noCommon.containsKey(min) && noCommon.get(min).contains(max)) {
            return Collections.emptyList();
        }
        List<Integer> l1 = new ArrayList<Integer>(factors(n1));
        l1.retainAll(factors(n2));
        if (l1.isEmpty()) {
            if (!noCommon.containsKey(min)) {
                noCommon.put(min, new TreeSet<Integer>());
            }
            noCommon.get(min).add(max);
        } else {
            if (!inCommon.containsKey(min)) {
                inCommon.put(min, new TreeMap<Integer, Integer>());
            }
            inCommon.get(min).put(max, l1.size());
        }
        return l1;
    }

    /** Cached list of factors */
    static TreeMap<Integer, List<Integer>> factors = new TreeMap<Integer, List<Integer>>();

    /** Computes prime factors of an integer */
    public static List<Integer> factors(Integer n) {
        if (factors.containsKey(n)) {
            return factors.get(n);
        }
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 2; i <= n / 2; i++) {
            if (isPrime(i) && n % i == 0) {
                result.add(i);
            }
        }
        factors.put(n, result);
        return result;
    }

    /** Cached list of primes */
    static TreeSet<Integer> primes = new TreeSet<Integer>();

    /** Prime number test */
    public static boolean isPrime(Integer n) {
        if (primes.contains(n)) {
            return true;
        }
        for (int i = 2; i <= n / 2; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        primes.add(n);
        return true;
    }

}

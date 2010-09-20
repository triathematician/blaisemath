
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.GraphFactory;
import org.bm.blaise.scio.graph.metrics.AdditiveSubsetMetric;
import org.bm.blaise.scio.graph.metrics.CooperationSubsetMetric;
import org.bm.blaise.scio.graph.metrics.GraphMetrics;
import org.bm.blaise.scio.graph.metrics.SubsetMetric2;

/*
 * CoopNetAnalysis.java
 * Created Sep 20, 2010
 */

/**
 * Performs cooperation analyses on particular networks, in support of paper
 * on cooperation in social networks.
 *
 * @author Elisha Peterson
 */
public class CoopNetAnalysis {

    Graph<Integer> network_undirected1;
    Graph<Integer> network_undirected2;
    Graph<Integer> network_undirected3;
    Graph<Integer> network_directed;
    Graph<Integer> network_florentine;
    Graph<Integer> network_ikenet;

    CooperationSubsetMetric<Double> additive_degree_metric;
    SubsetMetric2 component_size_metric;

    public void initGraphs() {
        Integer[][] e1 = new Integer[][]{{1,2},{2,3},{3,1},{4,5}};
        network_undirected1 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5), Arrays.asList(e1));

        Integer[][] e2 = new Integer[][]{{1,2},{2,3},{3,1},{2,4},{4,5},{5,2}};
        network_undirected2 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5), Arrays.asList(e2));

        Integer[][] e3 = new Integer[][]{{1,2},{2,3},{2,7},{2,8},{3,4},{4,5},{5,6},{5,7},{7,8},{8,9}};
        network_undirected3 = GraphFactory.getGraph(false, Arrays.asList(1,2,3,4,5,6,7,8,9), Arrays.asList(e3));
    }

    public void initMetrics() {
        additive_degree_metric = new CooperationSubsetMetric<Double>(new AdditiveSubsetMetric(GraphMetrics.DEGREE));

    }

    /** @return list of all subsets of a given size */
    static List<List<Integer>> subsets(List<Integer> set, int size) {
        int n = set.size();
        ArrayList<List<Integer>> result = new ArrayList<List<Integer>>();
        if (size == 0) {
            result.add(new ArrayList<Integer>());
        } else if (size == n) {
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int i = 0; i < size; i++)
                list.add(set.get(i));
            result.add(list);
        } else {
            Integer last = set.get(set.size()-1);
            List<List<Integer>> zeroList = subsets(allButLast(set), size);
            result.addAll(zeroList);
            List<List<Integer>> oneList = subsets(allButLast(set), size-1);
            for (List<Integer> l : oneList) {
                ArrayList<Integer> temp = new ArrayList<Integer>(l);
                temp.add(last);
                result.add(temp);
            }
        }
        return result;
    }

    /** @return list consisting of all but last element of another list */
    static List<Integer> allButLast(final List<Integer> list) {
        return new AbstractList<Integer>(){
            @Override public Integer get(int index) { return list.get(index); }
            @Override public int size() { return list.size() - 1; }
        };
    }

    /** @return copy of list */
    List<Integer> copy(List<Integer> list) {
        return new ArrayList<Integer>(list);
    }

    public static void main(String[] args) {
        System.out.println(subsets(Arrays.asList(1,2,3,4,5), 3));
    }
}

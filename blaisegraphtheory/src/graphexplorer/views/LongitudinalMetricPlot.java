/*
 * LongitudinalMetricPlot.java
 * Created Aug 4, 2010
 */

package graphexplorer.views;

import graphexplorer.controller.LongitudinalGraphController;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bm.blaise.scio.graph.FilteredWeightedGraph;
import org.bm.blaise.scio.graph.Graph;
import org.bm.blaise.scio.graph.LongitudinalGraph;
import org.bm.blaise.scio.graph.WeightedGraph;
import org.bm.blaise.scio.graph.metrics.NodeMetric;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * Provides views of metrics over time; creates a line graph displaying the value
 * of the controller's special-node subset over the time frame specified by a
 * longitudinal graph & the controller's active metric.
 * @author Elisha Peterson
 */
public class LongitudinalMetricPlot
        implements PropertyChangeListener {

    /** The underlying controller */
    private LongitudinalGraphController gc;
    /** The main chart. */
    private JFreeChart chart;

    /** Construct with specified controller */
    public LongitudinalMetricPlot(LongitudinalGraphController gc) {
        this.gc = gc;
        gc.addPropertyChangeListener(this);
        initChart();
        updateChart();
    }

    /** Initializes the chart for the first time */
    void initChart() {
        NumberAxis xAxis = new NumberAxis("Time");
        xAxis.setAutoRangeIncludesZero(false);
        NumberAxis yAxis = new NumberAxis("Metric Value");

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator());

        XYPlot plot = new XYPlot(new DefaultXYDataset(), xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);

        chart = new JFreeChart(
                    null,//"Metric Value over Time",
                    JFreeChart.DEFAULT_TITLE_FONT,
                    plot, true);
    }

    /** Updates chart based on current controller */
    void updateChart() {        
        if (chart == null)
            initChart();

        XYPlot plot = (XYPlot) chart.getPlot();
        LongitudinalGraph lg = (LongitudinalGraph) gc.getBaseGraph();
        NodeMetric metric = gc.getMetric();
        Set set = gc.getHighlightNodes();

        if (lg == null || metric == null || set == null || set.isEmpty()) {
            plot.setDataset(new DefaultXYDataset());
            plot.getRangeAxis().setLabel("Value of Metric");
        } else {
            List<Double> times = lg.getTimes();
            ArrayList subset = new ArrayList(set); // ensures consistent ordering

            int n_subset = subset.size();
            int n_time = times.size();

            double[][][] dataArr = new double[n_subset][2][n_time];
            for (int i_time = 0; i_time < n_time; i_time++) {
                Graph g = lg.slice(times.get(i_time), true);
                if (gc.isFilterEnabled()) {
                    g = FilteredWeightedGraph.getInstance((WeightedGraph) g);
                    ((FilteredWeightedGraph)g).setThreshold(gc.getFilterThreshold());
                }
                List nodes = g.nodes();
                List values = metric.allValues(g);
                for (int i_subset = 0; i_subset < n_subset; i_subset++) {
                    int index = nodes.indexOf(subset.get(i_subset));
                    if (index != -1) {
                        dataArr[i_subset][0][i_time] = times.get(i_time);
                        dataArr[i_subset][1][i_time] = ((Number)values.get(index)).doubleValue();
                    }
                }
            }

            DefaultXYDataset dataset = new DefaultXYDataset();
            for (int i_subset = 0; i_subset < n_subset; i_subset++)
                dataset.addSeries(subset.get(i_subset).toString(), dataArr[i_subset]);
            plot.setDataset(dataset);
            plot.getRangeAxis().setLabel("Value of Metric " + gc.getMetric());
        }
    }

    /** @return chart */
    public JFreeChart getChart() {
        return chart;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(gc)) {
            String pn = evt.getPropertyName();
            if (pn.equals("primary") || pn.equals("metric") || pn.equals("subset") 
                    || pn.equals("time")
                    || pn.equals("filtered") || pn.equals("filter threshold"))
                updateChart();
        }
    }

}

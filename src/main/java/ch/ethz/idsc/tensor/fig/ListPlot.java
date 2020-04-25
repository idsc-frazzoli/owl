// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeriesCollection;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/ListPlot.html">ListPlot</a> */
public enum ListPlot {
  ;
  /** Hint: use
   * jFreeChart.draw(graphics, ...) to render list plot
   * 
   * @param visualSet
   * @return */
  public static JFreeChart of(VisualSet visualSet) {
    XYSeriesCollection xySeriesCollection = DatasetFactory.xySeriesCollection(visualSet);
    JFreeChart jFreeChart = ChartFactory.createXYLineChart( //
        visualSet.getPlotLabel(), //
        visualSet.getAxesLabelX(), //
        visualSet.getAxesLabelY(), //
        xySeriesCollection, PlotOrientation.VERTICAL, //
        visualSet.hasLegend(), // legend
        false, // tooltips
        false); // urls
    XYPlot xyPlot = jFreeChart.getXYPlot();
    XYItemRenderer xyItemRenderer = xyPlot.getRenderer();
    int limit = xySeriesCollection.getSeriesCount();
    for (int index = 0; index < limit; ++index) {
      xyItemRenderer.setSeriesPaint(index, visualSet.getVisualRow(index).getColor());
      xyItemRenderer.setSeriesStroke(index, visualSet.getVisualRow(index).getStroke());
    }
    return jFreeChart;
  }
}

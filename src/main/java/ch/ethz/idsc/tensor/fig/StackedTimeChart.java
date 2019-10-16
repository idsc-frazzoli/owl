// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import org.jfree.chart.JFreeChart;

public enum StackedTimeChart {
  ;
  public static JFreeChart of(VisualSet visualSet) {
    return TimeChart.of(visualSet, true);
  }
}

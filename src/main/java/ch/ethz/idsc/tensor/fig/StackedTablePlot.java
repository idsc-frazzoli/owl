// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import org.jfree.chart.JFreeChart;

public enum StackedTablePlot {
  ;
  /** @param visualSet
   * @return */
  public static JFreeChart of(VisualSet visualSet) {
    return JFreeChartFactory.fromXYTable(visualSet, true, DatasetFactory.categoryTableXYDataset(visualSet));
  }
}

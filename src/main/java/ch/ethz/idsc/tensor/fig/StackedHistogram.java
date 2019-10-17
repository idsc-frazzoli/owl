// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import org.jfree.chart.JFreeChart;

/** similar to {@link Histogram} but with bars stacked on top of each other
 * instead of next to each other */
public enum StackedHistogram {
  ;
  /** @param visualSet
   * @return */
  public static JFreeChart of(VisualSet visualSet) {
    return Histogram.of(visualSet, true);
  }
}

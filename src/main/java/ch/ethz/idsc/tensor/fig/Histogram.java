// code by gjoel, jph
package ch.ethz.idsc.tensor.fig;

import java.util.function.Function;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.tensor.Scalar;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Histogram.html">Histogram</a> */
public enum Histogram {
  ;
  /** @param visualSet
   * @return */
  public static JFreeChart of(VisualSet visualSet) {
    return of(visualSet, false);
  }

  /* package */ static JFreeChart of(VisualSet visualSet, boolean stacked) {
    return JFreeChartFactory.barChart(visualSet, stacked, Scalar::toString);
  }

  /** @param visualSet
   * @param naming for coordinates on x-axis
   * @return */
  public static JFreeChart of(VisualSet visualSet, Function<Scalar, String> naming) {
    return JFreeChartFactory.barChart(visualSet, false, naming);
  }
}

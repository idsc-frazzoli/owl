// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/* package */ enum StaticHelper {
  ;
  public static JFreeChart listPlot(Tensor deltas) {
    return listPlot(deltas, Range.of(0, deltas.length()));
  }

  public static JFreeChart listPlot(Tensor deltas, Tensor domain) {
    VisualSet visualSet = new VisualSet();
    int dims = deltas.get(0).length();
    for (int index = 0; index < dims; ++index)
      visualSet.add(domain, deltas.get(Tensor.ALL, index));
    return ListPlot.of(visualSet);
  }
}

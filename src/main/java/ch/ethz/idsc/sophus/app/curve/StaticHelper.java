// code by jph
package ch.ethz.idsc.sophus.app.curve;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;

/* package */ enum StaticHelper {
  ;
  static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 10, 10), Subdivide.of(0, 10, 10));

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

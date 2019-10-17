// code by jph
package ch.ethz.idsc.sophus.app.curve;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ enum StaticHelper {
  ;
  static final RenderInterface GRID_RENDER = new GridRender(Subdivide.of(0, 10, 10), Subdivide.of(0, 10, 10));

  static TensorUnaryOperator create(CurveSubdivision curveSubdivision, boolean cyclic) {
    return cyclic //
        ? curveSubdivision::cyclic
        : curveSubdivision::string;
  }

  /** @param control
   * @param levels
   * @param curveSubdivision
   * @param isDual
   * @param cyclic
   * @param midpointInterface
   * @return */
  public static Tensor refine( //
      Tensor control, int levels, CurveSubdivision curveSubdivision, //
      boolean isDual, boolean cyclic, MidpointInterface midpointInterface) {
    TensorUnaryOperator tensorUnaryOperator = create(curveSubdivision, cyclic);
    Tensor refined = control;
    for (int level = 0; level < levels; ++level) {
      Tensor prev = refined;
      refined = tensorUnaryOperator.apply(refined);
      if (isDual && //
          level % 2 == 1 && //
          !cyclic && //
          1 < control.length())
        refined = Join.of( //
            Tensors.of(midpointInterface.midpoint(control.get(0), prev.get(0))), //
            refined, //
            Tensors.of(midpointInterface.midpoint(Last.of(prev), Last.of(control))));
    }
    return refined;
  }

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

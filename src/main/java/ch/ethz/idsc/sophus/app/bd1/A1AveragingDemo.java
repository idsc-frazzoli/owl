// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Arrays;

import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;

/* package */ abstract class A1AveragingDemo extends AnAveragingDemo {
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Scalar MARGIN = RealScalar.of(2);

  public A1AveragingDemo(ManifoldDisplay geodesicDisplay) {
    super(Arrays.asList(geodesicDisplay));
  }

  final Tensor domain() {
    Tensor support = getControlPointsSe2().get(Tensor.ALL, 0).map(N.DOUBLE);
    Tensor subdiv = Subdivide.of( //
        support.stream().reduce(Min::of).get().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).get().add(MARGIN), 100).map(N.DOUBLE);
    Tensor predom = Join.of(subdiv, support);
    return Tensor.of(predom.stream().distinct().sorted());
  }
}

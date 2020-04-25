// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;

/* package */ abstract class B1KrigingDemo extends A1KrigingDemo {
  private static final Scalar MARGIN = RealScalar.of(2);

  public B1KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(geodesicDisplay);
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

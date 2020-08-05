// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.MixedLogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class R1BarycentricCoordinateDemo extends A1BarycentricCoordinateDemo {
  private static final Scalar MARGIN = RealScalar.of(2);

  public R1BarycentricCoordinateDemo() {
    super(MixedLogWeightings.scattered());
  }

  @Override
  Tensor domain(Tensor support) {
    return Subdivide.of( //
        support.stream().reduce(Min::of).get().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).get().add(MARGIN), 128).map(N.DOUBLE);
  }

  @Override
  Tensor lift(Scalar x) {
    return Tensors.of(x);
  }

  public static void main(String[] args) {
    new R1BarycentricCoordinateDemo().setVisible(1000, 800);
  }
}

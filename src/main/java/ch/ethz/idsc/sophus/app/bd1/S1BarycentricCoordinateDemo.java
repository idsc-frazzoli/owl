// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.num.Pi;

// TODO crashes when only 2 control points exist
/* package */ class S1BarycentricCoordinateDemo extends A1BarycentricCoordinateDemo {
  public S1BarycentricCoordinateDemo() {
    super(LogWeightings.list());
  }

  @Override
  Tensor domain(Tensor support) {
    return Subdivide.of(Pi.VALUE.negate(), Pi.VALUE, 128);
  }

  @Override
  Tensor lift(Scalar x) {
    return AngleVector.of(x);
  }

  public static void main(String[] args) {
    new S1BarycentricCoordinateDemo().setVisible(1000, 800);
  }
}

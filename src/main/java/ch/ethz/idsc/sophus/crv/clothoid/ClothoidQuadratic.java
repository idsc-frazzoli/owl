// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class ClothoidQuadratic implements ScalarUnaryOperator {
  private static final Scalar _3 = RealScalar.of(+3);
  private static final Scalar N4 = RealScalar.of(-4);
  // ---
  private final ScalarUnaryOperator series;

  public ClothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    Scalar c1 = b0.multiply(_3).add(bm.multiply(N4)).add(b1);
    Scalar c2 = b0.add(b1).subtract(bm).subtract(bm);
    series = Series.of(Tensors.of(b0, c1.negate(), c2.add(c2)));
  }

  @Override
  public Scalar apply(Scalar s) {
    return ComplexScalar.unit(angle(s));
  }

  /* package */ Scalar angle(Scalar s) {
    return series.apply(s);
  }
}

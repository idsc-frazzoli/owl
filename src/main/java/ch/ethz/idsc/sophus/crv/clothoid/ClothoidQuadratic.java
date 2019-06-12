// code by ureif
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class ClothoidQuadratic implements ScalarUnaryOperator {
  private static final Scalar N4 = RealScalar.of(-4);
  // ---
  private final Scalar b0;
  private final Scalar bm;
  private final Scalar b1;

  public ClothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    this.b0 = b0;
    this.bm = bm;
    this.b1 = b1;
  }

  @Override
  public Scalar apply(Scalar s) {
    return ComplexScalar.unit(angle(s));
  }

  /* package */ Scalar angle(Scalar s) {
    Scalar _s_1 = s.subtract(RealScalar.ONE);
    Scalar _2s_1 = s.add(_s_1);
    Scalar t1 = b0.multiply(_s_1).multiply(_2s_1);
    Scalar t2 = bm.multiply(N4).multiply(s).multiply(_s_1);
    Scalar t3 = b1.multiply(s).multiply(_2s_1);
    return t1.add(t2).add(t3);
  }
}

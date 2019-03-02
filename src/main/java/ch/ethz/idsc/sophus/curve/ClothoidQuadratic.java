// code by ureif
package ch.ethz.idsc.sophus.curve;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Exp;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class ClothoidQuadratic implements ScalarUnaryOperator {
  private final Scalar b0;
  private final Scalar b1;
  private final Scalar bm;

  public ClothoidQuadratic(Scalar b0, Scalar bm, Scalar b1) {
    this.b0 = b0;
    this.bm = bm;
    this.b1 = b1;
  }

  @Override
  public Scalar apply(Scalar s) {
    return Exp.FUNCTION.apply(angle(s).multiply(ComplexScalar.I));
  }

  /* package */ Scalar angle(Scalar s) {
    Scalar _s_1 = s.subtract(RealScalar.ONE);
    Scalar _1_s = RealScalar.ONE.subtract(s);
    Scalar _2s_1 = RealScalar.of(2).multiply(s).subtract(RealScalar.ONE);
    Scalar t1 = b0.multiply(_s_1).multiply(_2s_1);
    Scalar t2 = bm.multiply(RealScalar.of(4)).multiply(s).multiply(_1_s);
    Scalar t3 = b1.multiply(s).multiply(_2s_1);
    return t1.add(t2).add(t3);
  }
}

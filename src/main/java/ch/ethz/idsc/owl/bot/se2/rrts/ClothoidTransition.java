// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.LagrangeQuadraticD;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.Tolerance;
import ch.ethz.idsc.tensor.pdf.EqualizingDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.sca.Abs;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidTransition extends AbstractTransition {
  private static final Scalar _0 = RealScalar.of(0.0);
  private static final Scalar _1 = RealScalar.of(1.0);
  private static final int MAX_INTERVALS = 511;

  /** @param clothoidBuilder
   * @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @return */
  public static ClothoidTransition of(ClothoidBuilder clothoidBuilder, Tensor start, Tensor end) {
    Clothoid clothoid = clothoidBuilder.curve(start, end);
    return new ClothoidTransition(start, end, clothoid);
  }

  /** @param start
   * @param end
   * @param clothoid
   * @return */
  public static ClothoidTransition of(Tensor start, Tensor end, Clothoid clothoid) {
    return new ClothoidTransition(start, end, clothoid);
  }

  /***************************************************/
  private final Clothoid clothoid;

  private ClothoidTransition(Tensor start, Tensor end, Clothoid clothoid) {
    super(start, end, clothoid.length());
    this.clothoid = clothoid;
  }

  @Override // from Transition
  public Tensor sampled(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    Tensor uniform = Subdivide.of(_0, _1, //
        Math.max(1, Ceiling.intValueExact(length().divide(minResolution))));
    return Tensor.of(uniform.stream().skip(1).map(Scalar.class::cast).map(clothoid));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.intValueExact(length().divide(minResolution));
    Tensor samples = linearized(length().divide(RealScalar.of(steps)));
    return new TransitionWrap( //
        Drop.head(samples, 1), //
        ConstantArray.of(length().divide(RealScalar.of(samples.length())), samples.length() - 1));
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    if (lagrangeQuadraticD.isZero(Tolerance.CHOP))
      return Tensors.of(clothoid.apply(_0), clothoid.apply(_1));
    int intervals = Ceiling.intValueExact(clothoid.length().divide(minResolution));
    Tensor uniform = Subdivide.of(_0, _1, Math.min(Math.max(1, intervals), MAX_INTERVALS));
    InverseCDF inverseCDF = //
        (InverseCDF) EqualizingDistribution.fromUnscaledPDF(uniform.map(lagrangeQuadraticD).map(Abs.FUNCTION));
    Tensor inverse = uniform.map(inverseCDF::quantile).divide(DoubleScalar.of(uniform.length()));
    return inverse.map(clothoid);
  }

  public Clothoid clothoid() {
    return clothoid;
  }
}

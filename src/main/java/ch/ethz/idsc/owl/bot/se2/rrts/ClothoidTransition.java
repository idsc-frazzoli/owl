// code by gjoel, jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import ch.ethz.idsc.owl.rrts.adapter.AbstractTransition;
import ch.ethz.idsc.owl.rrts.core.TransitionWrap;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.LagrangeQuadraticD;
import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
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

  /** @param start of the form {px, py, p_angle}
   * @param end of the form {qx, qy, q_angle}
   * @return */
  public static ClothoidTransition of(Tensor start, Tensor end) {
    return new ClothoidTransition(start, end, Se2Clothoids.INSTANCE.curve(start, end));
  }

  /** @param clothoid
   * @param minResolution
   * @return */
  public static Tensor linearized(Clothoid clothoid, Scalar minResolution) {
    Sign.requirePositive(minResolution);
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    if (lagrangeQuadraticD.isZero(Tolerance.CHOP))
      return Tensors.of(clothoid.apply(_0), clothoid.apply(_1));
    int intervals = Ceiling.FUNCTION.apply(clothoid.length().divide(minResolution)).number().intValue();
    Tensor uniform = Subdivide.of(_0, _1, Math.min(Math.max(1, intervals), MAX_INTERVALS));
    InverseCDF inverseCDF = //
        (InverseCDF) EqualizingDistribution.fromUnscaledPDF(uniform.map(lagrangeQuadraticD).map(Abs.FUNCTION));
    Tensor inverse = uniform.map(inverseCDF::quantile).divide(DoubleScalar.of(uniform.length()));
    return inverse.map(clothoid);
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
        Math.max(1, Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue()));
    return Tensor.of(uniform.stream().skip(1).map(Scalar.class::cast).map(clothoid));
  }

  @Override // from Transition
  public TransitionWrap wrapped(Scalar minResolution) {
    Sign.requirePositive(minResolution);
    int steps = Ceiling.FUNCTION.apply(length().divide(minResolution)).number().intValue();
    Tensor samples = linearized(length().divide(RealScalar.of(steps)));
    return new TransitionWrap( //
        Drop.head(samples, 1), //
        ConstantArray.of(length().divide(RealScalar.of(samples.length())), samples.length() - 1));
  }

  @Override // from Transition
  public Tensor linearized(Scalar minResolution) {
    return linearized(clothoid, minResolution);
  }

  public Clothoid clothoid() {
    return clothoid;
  }
}

// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Objects;
import java.util.function.Predicate;

import ch.ethz.idsc.sophus.crv.clothoid.Clothoid;
import ch.ethz.idsc.sophus.crv.clothoid.Clothoid.Curvature;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;

public class ClothoidLengthCostFunction implements TensorScalarFunction {
  private final Predicate<Scalar> isCompliant;

  public ClothoidLengthCostFunction(Predicate<Scalar> isCompliant) {
    this.isCompliant = Objects.requireNonNull(isCompliant);
  }

  @Override
  public Scalar apply(Tensor xya) {
    Clothoid clothoid = new Clothoid(xya.map(Scalar::zero), xya);
    Curvature curvature = clothoid.new Curvature();
    if (isCompliant.test(curvature.head()) && //
        isCompliant.test(curvature.tail()))
      return clothoid.new Curve().length();
    // TODO JPH filter out via collision check, units
    return DoubleScalar.POSITIVE_INFINITY;
  }
}

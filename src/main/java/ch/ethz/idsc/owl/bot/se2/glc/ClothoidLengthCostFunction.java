// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Objects;
import java.util.function.Predicate;

import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.qty.Quantity;

/** only applied in {@link ClothoidPursuitControl} resp. {@link Se2Letter5Demo} */
/* package */ class ClothoidLengthCostFunction implements TensorScalarFunction {
  private final Predicate<Scalar> isCompliant;

  public ClothoidLengthCostFunction(Predicate<Scalar> isCompliant) {
    this.isCompliant = Objects.requireNonNull(isCompliant);
  }

  @Override
  public Scalar apply(Tensor xya) {
    Clothoid clothoid = ClothoidBuilders.SE2_ANALYTIC.curve(xya.map(Scalar::zero), xya);
    if (isCompliant.test(clothoid.curvature().absMax()))
      return clothoid.length();
    // TODO GJOEL filter out via collision check, units
    if (xya.Get(0) instanceof Quantity)
      return Quantity.of(DoubleScalar.POSITIVE_INFINITY, ((Quantity) xya.Get(0)).unit());
    return DoubleScalar.POSITIVE_INFINITY;
  }
}

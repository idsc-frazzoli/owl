// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.Objects;
import java.util.function.Predicate;

import ch.ethz.idsc.sophus.clt.Clothoid;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorScalarFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;

/** only applied in {@link ClothoidPursuitControl} resp. {@link Se2Letter5Demo} */
/* package */ class ClothoidLengthCostFunction implements TensorScalarFunction {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();
  // ---
  private final Predicate<Scalar> isCompliant;

  public ClothoidLengthCostFunction(Predicate<Scalar> isCompliant) {
    this.isCompliant = Objects.requireNonNull(isCompliant);
  }

  @Override
  public Scalar apply(Tensor xya) {
    Clothoid clothoid = CLOTHOID_BUILDER.curve(xya.map(Scalar::zero), xya);
    if (isCompliant.test(clothoid.curvature().absMax()))
      return clothoid.length();
    return Quantity.of(DoubleScalar.POSITIVE_INFINITY, QuantityUnit.of(xya.Get(0)));
  }
}

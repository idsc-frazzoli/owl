// code by gjoel
package ch.ethz.idsc.owl.bot.se2.glc;

import java.util.function.Predicate;

import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuit;
import ch.ethz.idsc.owl.math.pursuit.ClothoidPursuits;
import ch.ethz.idsc.owl.math.pursuit.GeodesicPursuitInterface;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.red.Norm;

public class ClothoidLengthCostFunction implements TensorScalarFunction {
  private final Predicate<Scalar> isCompliant;
  private final int refinement;

  public ClothoidLengthCostFunction(Predicate<Scalar> isCompliant, int refinement) {
    this.isCompliant = isCompliant;
    this.refinement = refinement;
  }

  @Override
  public Scalar apply(Tensor xya) {
    GeodesicPursuitInterface geodesicPursuitInterface = new ClothoidPursuit(xya);
    Tensor ratios = geodesicPursuitInterface.ratios();
    if (ratios.stream().map(Tensor::Get).allMatch(isCompliant))
      return curveLength(ClothoidPursuits.curve(xya, refinement)); // Norm._2.ofVector(Extract2D.FUNCTION.apply(vector));
    return DoubleScalar.POSITIVE_INFINITY;
  }

  /** @param curve geodesic
   * @return approximated length of curve */
  private static Scalar curveLength(Tensor curve) {
    Tensor curve_ = Tensor.of(curve.stream().map(Extract2D.FUNCTION));
    int n = curve_.length();
    return curve_.extract(1, n).subtract(curve_.extract(0, n - 1)).stream() //
        .map(Norm._2::ofVector) //
        .reduce(Scalar::add).get();
  }
}

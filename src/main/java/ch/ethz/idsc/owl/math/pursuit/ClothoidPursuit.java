// code by gjoel, jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.clt.LagrangeQuadraticD;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ClothoidPursuit implements PursuitInterface, Serializable {
  /** @param lookAhead trajectory point {px, py, pa} */
  public static PursuitInterface of(Tensor lookAhead) {
    return new ClothoidPursuit(lookAhead);
  }

  /***************************************************/
  /** first and last ratio/curvature in curve */
  private final LagrangeQuadraticD lagrangeQuadraticD;

  private ClothoidPursuit(Tensor lookAhead) {
    lagrangeQuadraticD = ClothoidBuilders.SE2_ANALYTIC.curve(lookAhead.map(Scalar::zero), lookAhead).curvature();
  }

  @Override // from PursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.of(lagrangeQuadraticD.head());
  }

  @Override // from PursuitInterface
  public Tensor ratios() {
    return Tensors.of( // all other ratios/curvatures lay between these two for reasonable inputs
        lagrangeQuadraticD.head(), //
        lagrangeQuadraticD.tail());
  }
}

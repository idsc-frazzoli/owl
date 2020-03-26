// code by gjoel, jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.crv.clothoid.Se2Clothoids;
import ch.ethz.idsc.sophus.math.HeadTailInterface;
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
  private final HeadTailInterface headTailInterface;

  private ClothoidPursuit(Tensor lookAhead) {
    headTailInterface = Se2Clothoids.INSTANCE.curve(lookAhead.map(Scalar::zero), lookAhead).curvature();
  }

  @Override // from PursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.of(headTailInterface.head());
  }

  @Override // from PursuitInterface
  public Tensor ratios() {
    return Tensors.of( // all other ratios/curvatures lay between these two for reasonable inputs
        headTailInterface.head(), //
        headTailInterface.tail());
  }
}

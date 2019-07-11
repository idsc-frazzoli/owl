// code by gjoel, jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.sophus.crv.clothoid.ClothoidTerminalRatios;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ClothoidPursuit implements GeodesicPursuitInterface, Serializable {
  /** first and last ratio/curvature in curve */
  private final ClothoidTerminalRatios clothoidTerminalRatios;

  /** @param lookAhead trajectory point {px, py, pa} */
  public ClothoidPursuit(Tensor lookAhead) {
    clothoidTerminalRatios = ClothoidTerminalRatios.of(lookAhead.map(Scalar::zero), lookAhead);
  }

  @Override // from GeodesicPursuitInterface
  public Tensor ratios() {
    return Tensors.of( // all other ratios/curvatures lay between these two for reasonable inputs
        clothoidTerminalRatios.head(), //
        clothoidTerminalRatios.tail());
  }

  @Override // from GeodesicPursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.of(clothoidTerminalRatios.head());
  }
}

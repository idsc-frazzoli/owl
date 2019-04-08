// code by jph
package ch.ethz.idsc.owl.math.planar;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum VoidPursuit implements GeodesicPursuitInterface {
  INSTANCE;
  // ---
  @Override // from GeodesicPursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.empty();
  }

  @Override // from GeodesicPursuitInterface
  public Tensor ratios() {
    return Tensors.empty();
  }

  @Override // from GeodesicPursuitInterface
  public Tensor curve() {
    return Tensors.empty();
  }
}

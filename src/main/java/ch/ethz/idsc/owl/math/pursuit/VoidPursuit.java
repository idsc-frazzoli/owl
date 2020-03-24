// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public enum VoidPursuit implements PursuitInterface {
  INSTANCE;

  @Override // from PursuitInterface
  public Optional<Scalar> firstRatio() {
    return Optional.empty();
  }

  @Override // from PursuitInterface
  public Tensor ratios() {
    return Tensors.empty();
  }
}

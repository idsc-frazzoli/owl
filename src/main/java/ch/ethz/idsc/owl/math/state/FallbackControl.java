// code by jph
package ch.ethz.idsc.owl.math.state;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

public class FallbackControl implements EntityControl {
  private final Tensor fallback;

  /** @param fallback control */
  public FallbackControl(Tensor fallback) {
    this.fallback = N.DOUBLE.of(fallback).unmodifiable();
  }

  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    return Optional.of(fallback);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}

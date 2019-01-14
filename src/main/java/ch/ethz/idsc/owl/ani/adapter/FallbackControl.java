// code by jph
package ch.ethz.idsc.owl.ani.adapter;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

public class FallbackControl implements EntityControl, Serializable {
  private final Tensor fallback;

  /** @param fallback control */
  public FallbackControl(Tensor fallback) {
    this.fallback = N.DOUBLE.of(fallback).unmodifiable();
  }

  @Override // from EntityControl
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    return Optional.of(fallback);
  }

  @Override // from EntityControl
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}

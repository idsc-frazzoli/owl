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
  /** @param fallback control */
  public static EntityControl of(Tensor fallback) {
    return new FallbackControl(N.DOUBLE.of(fallback).unmodifiable());
  }

  /***************************************************/
  private final Tensor fallback;

  private FallbackControl(Tensor fallback) {
    this.fallback = fallback;
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

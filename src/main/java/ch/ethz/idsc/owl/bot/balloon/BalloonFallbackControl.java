// code by jph, astoll
package ch.ethz.idsc.owl.bot.balloon;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO ANDRE make generic by passing in scalar to constructor 
/* package */ class BalloonFallbackControl implements EntityControl, Serializable {
  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    return Optional.of(Tensors.vector(0));
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}

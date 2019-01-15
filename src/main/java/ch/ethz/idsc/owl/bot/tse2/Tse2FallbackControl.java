// code by jph
package ch.ethz.idsc.owl.bot.tse2;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

public class Tse2FallbackControl implements EntityControl, Serializable {
  private final Scalar magnitude;

  public Tse2FallbackControl(Scalar magnitude) {
    this.magnitude = magnitude;
  }

  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    Scalar vx = tail.state().Get(Tse2StateSpaceModel.STATE_INDEX_VEL);
    return Optional.of(Tensors.of(RealScalar.ZERO, Sign.FUNCTION.apply(vx).multiply(magnitude).negate()));
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}

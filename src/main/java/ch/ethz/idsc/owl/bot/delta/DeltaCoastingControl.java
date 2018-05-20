// code by jph
package ch.ethz.idsc.owl.bot.delta;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.math.state.EntityControl;
import ch.ethz.idsc.owl.math.state.ProviderRank;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

public class DeltaCoastingControl implements EntityControl, Serializable {
  private final ImageGradientInterpolation imageGradientInterpolation;
  private final Scalar u_norm;

  public DeltaCoastingControl(ImageGradientInterpolation imageGradientInterpolation, Scalar u_norm) {
    this.imageGradientInterpolation = imageGradientInterpolation;
    this.u_norm = u_norm;
  }

  @Override
  public Optional<Tensor> control(StateTime tail, Scalar now) {
    Tensor u = imageGradientInterpolation.get(tail.state());
    Scalar norm = Norm._2.ofVector(u);
    if (Scalars.lessThan(u_norm, norm))
      u = u.multiply(u_norm).divide(norm);
    return Optional.of(u.negate());
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }
}

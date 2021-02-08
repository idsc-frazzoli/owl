// code by jph and jl
package ch.ethz.idsc.owl.bot.delta;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.bot.r2.ImageGradientInterpolation;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.tensor.Tensor;

/** an upper bound of the speed of an entity in the river delta is
 * imageGradient.maxNormGradient() + |u_max|
 * 
 * | f(x_1, u) - f(x_2, u) | <= L | x_1 - x_2 |
 * Lipschitz L == imageGradientInterpolation.maxNormGradient() */
/* package */ class DeltaStateSpaceModel implements StateSpaceModel, Serializable {
  // ---
  private final ImageGradientInterpolation imageGradientInterpolation;

  /** @param imageGradientInterpolation
   * @param maxInput positive */
  public DeltaStateSpaceModel(ImageGradientInterpolation imageGradientInterpolation) {
    this.imageGradientInterpolation = Objects.requireNonNull(imageGradientInterpolation);
  }

  @Override // from StateSpaceModel
  public Tensor f(Tensor x, Tensor u) {
    return imageGradientInterpolation.get(x).add(u);
  }
}

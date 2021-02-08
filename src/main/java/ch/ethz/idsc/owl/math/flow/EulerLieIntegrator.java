// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieIntegrator;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class EulerLieIntegrator implements Integrator, LieIntegrator, Serializable {
  /** @param lieGroup
   * @param exponential
   * @return */
  public static Integrator of(LieGroup lieGroup, Exponential exponential) {
    return new EulerLieIntegrator( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(exponential));
  }

  /***************************************************/
  private final LieGroup lieGroup;
  private final Exponential exponential;

  private EulerLieIntegrator(LieGroup lieGroup, Exponential exponential) {
    this.lieGroup = lieGroup;
    this.exponential = exponential;
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x, Tensor u, Scalar h) {
    return spin(x, stateSpaceModel.f(x, u).multiply(h));
  }

  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor v) {
    return lieGroup.element(g).combine(exponential.exp(v));
  }
}

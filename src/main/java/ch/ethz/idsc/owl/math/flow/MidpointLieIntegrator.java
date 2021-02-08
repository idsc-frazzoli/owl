// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class MidpointLieIntegrator implements Integrator, Serializable {
  /** @param lieGroup
   * @param exponential
   * @return */
  public static Integrator of(LieGroup lieGroup, Exponential exponential) {
    return new MidpointLieIntegrator( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(exponential));
  }

  /***************************************************/
  private final LieGroup lieGroup;
  private final Exponential exponential;

  private MidpointLieIntegrator(LieGroup lieGroup, Exponential exponential) {
    this.lieGroup = lieGroup;
    this.exponential = exponential;
  }

  @Override // from Integrator
  public Tensor step(StateSpaceModel stateSpaceModel, Tensor x0, Tensor u, Scalar _2h
  // Flow flow, Tensor x0, Scalar _2h
  ) {
    Scalar h = _2h.multiply(RationalScalar.HALF);
    Tensor xm = lieGroup.element(x0).combine(exponential.exp(stateSpaceModel.f(x0, u).multiply(h)));
    return /**/ lieGroup.element(x0).combine(exponential.exp(stateSpaceModel.f(xm, u).multiply(_2h))); // 2h
  }
}

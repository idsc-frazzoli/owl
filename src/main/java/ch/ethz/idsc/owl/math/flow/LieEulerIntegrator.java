// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class LieEulerIntegrator implements Integrator, Serializable {
  public static Integrator of(LieGroup lieGroup, LieExponential lieExponential) {
    return new LieEulerIntegrator( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(lieExponential));
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  private LieEulerIntegrator(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    return lieGroup.element(x).combine(lieExponential.exp(flow.at(x).multiply(h)));
  }
}

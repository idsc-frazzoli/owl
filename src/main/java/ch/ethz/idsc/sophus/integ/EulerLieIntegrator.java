// code by jph
package ch.ethz.idsc.sophus.integ;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class EulerLieIntegrator implements Integrator, LieIntegrator, Serializable {
  public static Integrator of(LieGroup lieGroup, LieExponential lieExponential) {
    return new EulerLieIntegrator( //
        Objects.requireNonNull(lieGroup), //
        Objects.requireNonNull(lieExponential));
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;

  private EulerLieIntegrator(LieGroup lieGroup, LieExponential lieExponential) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
  }

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    return spin(x, flow.at(x).multiply(h));
  }

  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor v) {
    return lieGroup.element(g).combine(lieExponential.exp(v));
  }
}

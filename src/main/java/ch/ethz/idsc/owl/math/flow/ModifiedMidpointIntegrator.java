// code by jph
package ch.ethz.idsc.owl.math.flow;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Integers;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** Numerical Recipes 3rd Edition Section 17.3.1 */
public class ModifiedMidpointIntegrator implements Integrator, Serializable {
  /** @param n strictly positive
   * @return */
  public static Integrator of(int n) {
    return new ModifiedMidpointIntegrator(Integers.requirePositive(n));
  }

  // ---
  private final int n;

  private ModifiedMidpointIntegrator(int n) {
    this.n = n;
  }

  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar H) {
    Scalar h = H.divide(RealScalar.of(n));
    Tensor prev = x;
    Tensor curr = x.add(flow.at(x).multiply(h));
    for (int m = 1; m < n; ++m) {
      Tensor next = prev.add(flow.at(curr).multiply(h.add(h)));
      prev = curr;
      curr = next;
    }
    return prev.add(curr).add(flow.at(curr).multiply(h)).multiply(RationalScalar.HALF);
  }
}

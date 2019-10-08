// code by jph
package ch.ethz.idsc.owl.bot.rn;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** Exact integrator for polynomial equations of the model
 * state = {position, velocity}
 * control = {acceleration}
 * 
 * The acceleration is assumed to be constant during integration step.
 * 
 * DSolve[{v'[h] == a, p'[h] == v[h], v[0] == v0, p[0] == p0}, {p[h], v[h]}, h]
 * results in
 * {p[h] -> (a h^2)/2 + p0 + h v0, v[h] -> a h + v0} */
public enum R1Integrator implements Integrator {
  INSTANCE;
  // ---
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor x, Scalar h) {
    return direct(x, flow.getU().Get(0), h);
  }

  /** @param x vector of the form {position, velocity}
   * @param a acceleration
   * @param h step size
   * @return vector of length 2 */
  public static Tensor direct(Tensor x, Scalar a, Scalar h) {
    Scalar p0 = x.Get(0);
    Scalar v0 = x.Get(1);
    Scalar a2 = a.multiply(RationalScalar.HALF);
    return Tensors.of( //
        a2.multiply(h).add(v0).multiply(h).add(p0), // Series.of(Tensors.of(p0, v0, a2)).apply(h),
        a.multiply(h).add(v0));
  }
}

// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.LieIntegrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta4Integrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** exact integration of flow using matrix exponential and logarithm.
 * states are encoded in the default coordinates of the se2 lie-algebra.
 * 
 * Important: u is assumed to be of the form u == {vx, 0, rate}
 * 
 * Se2Integrator is approximately
 * 3x faster than {@link RungeKutta4Integrator}
 * 11x faster than {@link RungeKutta45Integrator} */
public enum Se2CarIntegrator implements Integrator, LieIntegrator {
  INSTANCE;
  // ---
  /** Parameter description:
   * g in SE2
   * h in R */
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor g, Scalar h) {
    // u is assumed to be of the form u == {vx, 0, rate}
    return spin(g, flow.getU().multiply(h));
  }

  /** function integrates the special case where the y-component of x
   * is constrained to equal 0.
   * 
   * @param g == {px, py, alpha}
   * @param x == {vx, 0, beta}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    Scalar al = g.Get(2);
    Scalar be = x.Get(2);
    if (Scalars.isZero(be))
      return g.extract(0, 2).add(RotationMatrix.of(al).dot(x.extract(0, 2))).append(al);
    Scalar ra = al.add(be);
    Scalar sd = Sin.FUNCTION.apply(ra).subtract(Sin.FUNCTION.apply(al));
    Scalar cd = Cos.FUNCTION.apply(ra).subtract(Cos.FUNCTION.apply(al));
    Scalar dv = x.Get(0).divide(be);
    return Tensors.of( //
        g.Get(0).add(sd.multiply(dv)), //
        g.Get(1).subtract(cd.multiply(dv)), //
        ra);
  }
}

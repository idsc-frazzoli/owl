// code by jph
package ch.ethz.idsc.owl.math.map;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.LieIntegrator;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;

/** exact integration of flow using matrix exponential and logarithm.
 * states are encoded in the default coordinates of the se2 lie-algebra. */
public enum Se2Integrator implements Integrator, LieIntegrator {
  INSTANCE;
  // ---
  /** Parameter description:
   * g in SE2
   * h in R */
  @Override // from Integrator
  public Tensor step(Flow flow, Tensor g, Scalar h) {
    return spin(g, flow.getU().multiply(h));
  }

  /** @param g == {px, py, alpha}
   * @param x == {vx, vy, beta}
   * @return g . exp x */
  @Override // from LieIntegrator
  public Tensor spin(Tensor g, Tensor x) {
    Scalar al = g.Get(2);
    Scalar be = x.Get(2);
    if (Scalars.isZero(be))
      return g.extract(0, 2).add(RotationMatrix.of(al).dot(x.extract(0, 2))).append(al);
    Scalar px = g.Get(0);
    Scalar py = g.Get(1);
    Scalar vx = x.Get(0);
    Scalar vy = x.Get(1);
    Scalar ra = al.add(be);
    Scalar cd = Cos.FUNCTION.apply(ra).subtract(Cos.FUNCTION.apply(al));
    Scalar sd = Sin.FUNCTION.apply(ra).subtract(Sin.FUNCTION.apply(al));
    return Tensors.of( //
        px.add(sd.multiply(vx).add(cd.multiply(vy)).divide(be)), //
        py.add(sd.multiply(vy).subtract(cd.multiply(vx)).divide(be)), //
        ra);
  }
}

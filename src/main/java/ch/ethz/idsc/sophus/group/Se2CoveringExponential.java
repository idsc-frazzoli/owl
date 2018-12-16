// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Cos;
import ch.ethz.idsc.tensor.sca.Sin;
import ch.ethz.idsc.tensor.sca.Tan;

/** References:
 * http://vixra.org/abs/1807.0463
 * https://www.youtube.com/watch?v=2vDciaUgL4E */
public enum Se2CoveringExponential implements LieExponential {
  INSTANCE;
  // ---
  /** maps a vector x from the Lie-algebra se2 to a vector of the Lie-group SE2
   * 
   * @param x element in the se2 Lie-algebra of the form {vx, vy, beta}
   * @return element g in SE2 as vector with coordinates of g == exp x */
  @Override // from LieExponential
  public Tensor exp(Tensor x) {
    Scalar be = x.Get(2);
    if (Scalars.isZero(be))
      return x.copy();
    Scalar vx = x.Get(0);
    Scalar vy = x.Get(1);
    Scalar cd = Cos.FUNCTION.apply(be).subtract(RealScalar.ONE);
    Scalar sd = Sin.FUNCTION.apply(be);
    return Tensors.of( //
        sd.multiply(vx).add(cd.multiply(vy)).divide(be), //
        sd.multiply(vy).subtract(cd.multiply(vx)).divide(be), //
        be);
  }

  /** @param g element in the SE2 Lie group of the form {px, py, beta}
   * @return element x in the se2 Lie algebra with x == log g, and g == exp x */
  @Override // from LieExponential
  public Tensor log(Tensor g) {
    final Scalar be = g.Get(2);
    if (Scalars.isZero(be))
      return g.copy();
    Scalar x = g.Get(0);
    Scalar y = g.Get(1);
    Scalar be2 = be.divide(RealScalar.of(2));
    Scalar tan = Tan.FUNCTION.apply(be2);
    return Tensors.of( //
        y.add(x.divide(tan)).multiply(be2), //
        y.divide(tan).subtract(x).multiply(be2), //
        be);
  }
}

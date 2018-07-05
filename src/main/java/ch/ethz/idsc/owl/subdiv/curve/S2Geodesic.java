// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Sin;

/** geodesic on 2-dimensional sphere embedded in R^3
 * 
 * https://en.wikipedia.org/wiki/N-sphere */
public enum S2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  /** p and q are vectors of length 3 with unit length */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Tensor c = Cross.of(p, q);
    Scalar a = ArcCos.FUNCTION.apply(p.dot(q).Get());
    if (Scalars.isZero(a))
      return p;
    Scalar factor = scalar.multiply(a).divide(Sin.FUNCTION.apply(a));
    return MatrixExp.of(Cross.of(c.multiply(factor))).dot(p);
  }
}

// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Sin;

/** geodesic on 2-dimensional sphere embedded in R^3
 * 
 * https://en.wikipedia.org/wiki/N-sphere */
public enum S2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  /** p and q are vectors of length 3 with unit length
   * 
   * Careful: function does not check length of input vectors! */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Scalar a = ArcCos.FUNCTION.apply(p.dot(q).Get()); // complex number if |p.q| > 1
    Scalar sina = Sin.FUNCTION.apply(a);
    if (Scalars.isZero(sina)) // when p == q or p == -q
      return p;
    Scalar factor = scalar.multiply(a).divide(sina);
    return Rodrigues.exp(Cross.of(p, q).multiply(factor)).dot(p);
  }
}

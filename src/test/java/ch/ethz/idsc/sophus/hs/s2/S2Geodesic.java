// code by jph
package ch.ethz.idsc.sophus.hs.s2;

import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Sin;

/** geodesic on 2-dimensional sphere embedded in R^3
 * 
 * https://en.wikipedia.org/wiki/N-sphere
 * 
 * superseded by {@link SnGeodesic} */
public enum S2Geodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  @Override // from TensorGeodesic
  public ScalarTensorFunction curve(Tensor p, Tensor q) {
    Scalar a = ArcCos.FUNCTION.apply(p.dot(q).Get()); // complex number if |p.q| > 1
    Scalar sina = Sin.FUNCTION.apply(a);
    if (Scalars.isZero(sina)) // when p == q or p == -q
      return scalar -> p.copy();
    Scalar prod = a.divide(sina);
    Tensor cross = Cross.of(p, q);
    return scalar -> Rodrigues.exp(cross.multiply(scalar).multiply(prod)).dot(p);
  }

  /** p and q are vectors of length 3 with unit length
   * 
   * Careful: function does not check length of input vectors! */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    return curve(p, q).apply(scalar);
  }
}

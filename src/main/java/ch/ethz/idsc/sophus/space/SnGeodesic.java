// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.ArcCos;
import ch.ethz.idsc.tensor.sca.Sin;

/** geodesic on n-dimensional sphere embedded in R^(n+1)
 * 
 * implementation is based on the function "slerp" taken from
 * "Freeform Curves on Spheres of Arbitrary Dimension"
 * by Scott Schaefer and Ron Goldman, page 2 */
public enum SnGeodesic implements GeodesicInterface {
  INSTANCE;
  // ---
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  /** p and q are vectors of length 3 with unit length
   * 
   * Careful: function does not check length of input vectors! */
  @Override // from GeodesicInterface
  public Tensor split(Tensor p, Tensor q, Scalar scalar) {
    Scalar a = ArcCos.FUNCTION.apply(p.dot(q).Get()); // complex number if |p.q| > 1
    if (Scalars.isZero(a)) // when p == q or p == -q
      return p.copy();
    return NORMALIZE.apply(Tensors.of( //
        Sin.FUNCTION.apply(a.multiply(RealScalar.ONE.subtract(scalar))), //
        Sin.FUNCTION.apply(a.multiply(scalar))).dot(Tensors.of(p, q)));
  }
}

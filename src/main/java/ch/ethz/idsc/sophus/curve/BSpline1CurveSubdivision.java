// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** linear B-spline
 * 
 * the scheme interpolates the control points
 * 
 * Dyn/Sharon 2014 p.14 show that the contractivity factor is mu = 1/2 */
public class BSpline1CurveSubdivision implements CurveSubdivision, Serializable {
  protected final GeodesicInterface geodesicInterface;

  public BSpline1CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    return string(tensor).append(center(Last.of(tensor), tensor.get(0)));
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (Tensors.isEmpty(tensor))
      return Tensors.empty();
    Tensor curve = tensor.extract(0, 1);
    Tensor p = tensor.get(0);
    for (int index = 1; index < tensor.length(); ++index) {
      Tensor q = tensor.get(index);
      curve.append(center(p, q)).append(p = q);
    }
    return curve;
  }

  /** @param p
   * @param q
   * @return point between p and q */
  protected final Tensor center(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, RationalScalar.HALF);
  }
}

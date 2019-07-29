// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Iterator;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/** linear B-spline
 * 
 * the scheme interpolates the control points
 * 
 * Dyn/Sharon 2014 p.14 show that the contractivity factor is mu = 1/2 */
public abstract class AbstractBSpline1CurveSubdivision implements CurveSubdivision, MidpointInterface, Serializable {
  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    int length = tensor.length();
    if (1 < length)
      return stringNonEmpty(tensor).append(midpoint(Last.of(tensor), tensor.get(0)));
    ScalarQ.thenThrow(tensor);
    return tensor.copy();
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    int length = tensor.length();
    if (1 < length)
      return stringNonEmpty(tensor);
    ScalarQ.thenThrow(tensor);
    return tensor.copy();
  }

  private Tensor stringNonEmpty(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor p = iterator.next();
    curve.append(p);
    while (iterator.hasNext()) {
      Tensor q = iterator.next();
      curve.append(midpoint(p, q)).append(p = q);
    }
    return curve;
  }
}

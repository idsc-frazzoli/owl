// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Iterator;

import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** base class for B-Spline degree 2 curve subdivision
 * Chaikin 1965 */
public abstract class AbstractBSpline2CurveSubdivision implements CurveSubdivision, Serializable {
  @Override // from CurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    return refine(protected_string(tensor), Last.of(tensor), tensor.get(0));
  }

  // Hint: curve contracts at the sides
  @Override // from CurveSubdivision
  public final Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    return protected_string(tensor);
  }

  private Tensor protected_string(Tensor tensor) {
    int length = tensor.length();
    Tensor curve = Tensors.reserve(2 * length); // allocation for cyclic case
    Iterator<Tensor> iterator = tensor.iterator();
    Tensor p = iterator.next();
    while (iterator.hasNext()) {
      Tensor q = iterator.next();
      refine(curve, p, q);
      p = q;
    }
    return curve;
  }

  /** @param curve
   * @param p
   * @param q
   * @return curve with points [p, q]1/4 and [p, q]3/4 appended */
  protected abstract Tensor refine(Tensor curve, Tensor p, Tensor q);
}

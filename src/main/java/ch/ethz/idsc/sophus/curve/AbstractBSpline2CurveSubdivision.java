// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

/** base class for B-Spline degree 2 curve subdivision
 * Chaikin 1965 */
public abstract class AbstractBSpline2CurveSubdivision implements CurveSubdivision, Serializable {
  protected final GeodesicInterface geodesicInterface;

  public AbstractBSpline2CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    return refine(string(tensor), Last.of(tensor), tensor.get(0));
  }

  // Hint: curve contracts at the sides
  @Override // from CurveSubdivision
  public final Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor curve = Tensors.empty();
    Tensor p = tensor.get(0);
    for (int index = 1; index < length; ++index) {
      Tensor q = tensor.get(index);
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

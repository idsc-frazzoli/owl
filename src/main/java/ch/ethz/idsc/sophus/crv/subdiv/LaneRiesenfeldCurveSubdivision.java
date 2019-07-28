// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

/** TODO JPH reference */
public class LaneRiesenfeldCurveSubdivision implements CurveSubdivision, Serializable {
  /** @param midpointInterface
   * @param degree strictly positive
   * @return */
  public static CurveSubdivision of(MidpointInterface midpointInterface, int degree) {
    if (degree < 1)
      throw new IllegalArgumentException("" + degree);
    return new LaneRiesenfeldCurveSubdivision(Objects.requireNonNull(midpointInterface), degree);
  }

  // ---
  /** linear subdivision */
  private final BSpline1CurveSubdivision bSpline1CurveSubdivision;
  private final int degree;

  // TODO JPH OWL 049 make private
  public LaneRiesenfeldCurveSubdivision(MidpointInterface midpointInterface, int degree) {
    bSpline1CurveSubdivision = new BSpline1CurveSubdivision(midpointInterface);
    this.degree = degree;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor value = bSpline1CurveSubdivision.cyclic(tensor);
    for (int count = 2; count <= degree; ++count) {
      if (Tensors.isEmpty(value))
        return value;
      boolean odd = count % 2 == 1;
      Tensor queue = Unprotect.empty(value.length());
      if (odd) {
        Tensor p = Last.of(value);
        for (int index = 0; index < value.length(); ++index) {
          Tensor q = value.get(index);
          queue.append(bSpline1CurveSubdivision.midpoint(p, q));
          p = q;
        }
      } else {
        Tensor p = value.get(0);
        for (int index = 1; index <= value.length(); ++index) {
          Tensor q = value.get(index % value.length());
          queue.append(bSpline1CurveSubdivision.midpoint(p, q));
          p = q;
        }
      }
      tensor = value;
      value = queue;
    }
    return value;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Tensor value = bSpline1CurveSubdivision.string(tensor);
    for (int count = 2; count <= degree; ++count) {
      if (Tensors.isEmpty(value))
        return value;
      boolean odd = count % 2 == 1;
      Tensor queue = Unprotect.empty(value.length() + 1);
      if (odd)
        queue.append(tensor.get(0));
      Tensor p = value.get(0);
      for (int index = 1; index < value.length(); ++index)
        queue.append(bSpline1CurveSubdivision.midpoint(p, p = value.get(index)));
      if (odd)
        queue.append(Last.of(tensor));
      tensor = value;
      value = queue;
    }
    return value;
  }
}

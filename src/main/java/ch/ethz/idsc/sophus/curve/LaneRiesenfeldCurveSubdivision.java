// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Last;

public class LaneRiesenfeldCurveSubdivision implements CurveSubdivision, Serializable {
  /** linear subdivision */
  private final BSpline1CurveSubdivision bSpline1CurveSubdivision;
  private final int degree;

  public LaneRiesenfeldCurveSubdivision(GeodesicInterface geodesicInterface, int degree) {
    bSpline1CurveSubdivision = new BSpline1CurveSubdivision(geodesicInterface);
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
          queue.append(bSpline1CurveSubdivision.center(p, q));
          p = q;
        }
      } else {
        Tensor p = value.get(0);
        for (int index = 1; index <= value.length(); ++index) {
          Tensor q = value.get(index % value.length());
          queue.append(bSpline1CurveSubdivision.center(p, q));
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
        queue.append(bSpline1CurveSubdivision.center(p, p = value.get(index)));
      if (odd)
        queue.append(Last.of(tensor));
      tensor = value;
      value = queue;
    }
    return value;
  }
}

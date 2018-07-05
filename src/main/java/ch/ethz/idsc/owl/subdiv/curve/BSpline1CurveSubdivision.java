// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** linear subdivision
 * 
 * the scheme interpolates the control points */
public class BSpline1CurveSubdivision implements CurveSubdivision, Serializable {
  private final GeodesicInterface geodesicInterface;

  public BSpline1CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get(index);
      Tensor q = tensor.get((index + 1) % tensor.length());
      curve.append(p);
      curve.append(geodesicInterface.split(p, q, RationalScalar.HALF));
    }
    return curve;
  }

  public Tensor string(Tensor tensor) {
    Tensor curve = Tensors.empty();
    int last = tensor.length() - 1;
    for (int index = 0; index < last; ++index) {
      Tensor p = tensor.get(index);
      Tensor q = tensor.get(index + 1);
      curve.append(p);
      curve.append(geodesicInterface.split(p, q, RationalScalar.HALF));
    }
    curve.append(tensor.get(last));
    return curve;
  }
}

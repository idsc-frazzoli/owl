// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class BSpline2CurveSubdivision implements CurveSubdivision, Serializable {
  private final GeodesicInterface geodesicInterface;

  public BSpline2CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get(index);
      Tensor q = tensor.get((index + 1) % tensor.length());
      curve.append(geodesicInterface.split(p, q, RationalScalar.of(1, 4)));
      curve.append(geodesicInterface.split(p, q, RationalScalar.of(3, 4)));
    }
    return curve;
  }
}

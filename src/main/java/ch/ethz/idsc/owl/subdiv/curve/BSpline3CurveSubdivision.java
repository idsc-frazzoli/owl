// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class BSpline3CurveSubdivision implements CurveSubdivision {
  private final GeodesicInterface geodesicInterface;

  public BSpline3CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor pq = geodesicInterface.split(p, q, RationalScalar.of(3, 4));
      Tensor qr = geodesicInterface.split(q, r, RationalScalar.of(1, 4));
      curve.append(geodesicInterface.split(pq, qr, RationalScalar.of(1, 2)));
      curve.append(geodesicInterface.split(q, r, RationalScalar.of(1, 2)));
    }
    return curve;
  }
}

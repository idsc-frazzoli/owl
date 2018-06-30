// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class FourPointCurveSubdivision implements CurveSubdivision, Serializable {
  private final static Scalar WEIGHTA = RationalScalar.of(+9, 8);
  private final static Scalar WEIGHTB = RationalScalar.of(-1, 8);
  // ---
  private final GeodesicInterface geodesicInterface;

  public FourPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor t = tensor.get((index + 2) % tensor.length());
      Tensor pq = geodesicInterface.split(p, q, WEIGHTA);
      Tensor rt = geodesicInterface.split(r, t, WEIGHTB);
      curve.append(q);
      curve.append(geodesicInterface.split(pq, rt, RationalScalar.HALF));
    }
    return curve;
  }
}

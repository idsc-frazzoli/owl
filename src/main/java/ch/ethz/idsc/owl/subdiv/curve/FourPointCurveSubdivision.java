// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class FourPointCurveSubdivision implements CurveSubdivision, Serializable {
  private final static Scalar P9_8 = RationalScalar.of(+9, 8);
  private final static Scalar N1_8 = RationalScalar.of(-1, 8);
  // ---
  private final static Scalar P3_8 = RationalScalar.of(+3, 8);
  private final static Scalar P6_8 = RationalScalar.of(+6, 8);
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
      Tensor pq = geodesicInterface.split(p, q, P9_8);
      Tensor rt = geodesicInterface.split(r, t, N1_8);
      curve.append(q);
      curve.append(geodesicInterface.split(pq, rt, RationalScalar.HALF));
    }
    return curve;
  }

  // FIXME implementation is incorrect
  public Tensor string(Tensor tensor) {
    Tensor curve = Tensors.empty();
    for (int index = 1; index < tensor.length() - 1; ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor t = tensor.get((index + 2) % tensor.length());
      Tensor pq = geodesicInterface.split(p, q, P3_8);
      Tensor rt = geodesicInterface.split(r, t, P6_8);
      curve.append(q);
      curve.append(geodesicInterface.split(pq, rt, RationalScalar.HALF));
    }
    return curve;
  }
}

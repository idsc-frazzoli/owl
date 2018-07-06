// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class BSpline3CurveSubdivision implements CurveSubdivision, Serializable {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _2_4 = RationalScalar.of(2, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
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
      Tensor pq = geodesicInterface.split(p, q, _3_4);
      Tensor qr = geodesicInterface.split(q, r, _1_4);
      curve.append(geodesicInterface.split(pq, qr, _2_4));
      curve.append(geodesicInterface.split(q, r, _2_4));
    }
    return curve;
  }

  public Tensor string(Tensor tensor) {
    Tensor curve = Tensors.empty();
    {
      Tensor q = tensor.get(0);
      Tensor r = tensor.get(1);
      curve.append(q);
      curve.append(geodesicInterface.split(q, r, _2_4));
    }
    int last = tensor.length() - 1;
    for (int index = 1; index < last; /* nothing */ ) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(++index);
      Tensor pq = geodesicInterface.split(p, q, _3_4);
      Tensor qr = geodesicInterface.split(q, r, _1_4);
      curve.append(geodesicInterface.split(pq, qr, _2_4));
      curve.append(geodesicInterface.split(q, r, _2_4));
    }
    curve.append(tensor.get(last));
    return curve;
  }
}

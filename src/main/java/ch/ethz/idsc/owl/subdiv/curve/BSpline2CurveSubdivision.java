// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;

public class BSpline2CurveSubdivision implements CurveSubdivision, Serializable {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  private final GeodesicInterface geodesicInterface;

  public BSpline2CurveSubdivision(GeodesicInterface geodesicInterface) {
    this.geodesicInterface = geodesicInterface;
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    Tensor curve = string(tensor);
    Tensor p = Last.of(tensor);
    Tensor q = tensor.get(0);
    return curve.append(lo(p, q)).append(hi(p, q));
  }

  // Hint: curve contracts at the sides
  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    switch (tensor.length()) {
    case 1:
      return tensor.copy();
    }
    Tensor curve = Tensors.empty();
    int last = tensor.length() - 1;
    for (int index = 0; index < last; /* nothing */ ) {
      Tensor p = tensor.get(index);
      Tensor q = tensor.get(++index);
      curve.append(lo(p, q)).append(hi(p, q));
    }
    return curve;
  }

  private Tensor lo(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, _1_4);
  }

  private Tensor hi(Tensor p, Tensor q) {
    return geodesicInterface.split(p, q, _3_4);
  }
}

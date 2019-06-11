// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;

public class EightPointCurveSubdivision extends BSpline1CurveSubdivision {
  private static final Scalar PQ = RationalScalar.of(49, 44);
  private static final Scalar _R = RationalScalar.of(245, 201);
  private static final Scalar _S = RationalScalar.of(1225, 1024);
  // ---

  public EightPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    super(geodesicInterface);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    for (int index = 0; index < length; ++index) {
      int first = Math.floorMod(index - 3, length);
      Tensor p = tensor.get((first + 0) % length);
      Tensor q = tensor.get((first + 1) % length);
      Tensor r = tensor.get((first + 2) % length);
      Tensor s = tensor.get(index);
      Tensor t = tensor.get((first + 4) % length);
      Tensor u = tensor.get((first + 5) % length);
      Tensor v = tensor.get((first + 6) % length);
      Tensor w = tensor.get((first + 7) % length);
      curve.append(s).append(center(p, q, r, s, t, u, v, w));
    }
    return curve;
  }

  private Tensor center(Tensor p, Tensor q, Tensor r, Tensor s, Tensor t, Tensor u, Tensor v, Tensor w) {
    Tensor pq = geodesicInterface.split(p, q, PQ);
    Tensor _r = geodesicInterface.split(pq, r, _R);
    Tensor _s = geodesicInterface.split(_r, s, _S);
    // ---
    Tensor wv = geodesicInterface.split(w, v, PQ);
    Tensor _u = geodesicInterface.split(wv, u, _R);
    Tensor _t = geodesicInterface.split(_u, t, _S);
    return midpoint(_s, _t);
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    throw new UnsupportedOperationException();
  }
}

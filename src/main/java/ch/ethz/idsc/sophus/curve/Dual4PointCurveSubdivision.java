// code by jph
package ch.ethz.idsc.sophus.curve;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** dual scheme */
public class Dual4PointCurveSubdivision implements CurveSubdivision, Serializable {
  private final GeodesicInterface geodesicInterface;
  private final Scalar lo_pq;
  private final Scalar lo_rs;
  private final Scalar lo_pqrs;
  private final Scalar hi_pq;
  private final Scalar hi_rs;
  private final Scalar hi_pqrs;

  /** @param geodesicInterface non-null
   * @param pq_f
   * @param rs_f
   * @param pqrs */
  public Dual4PointCurveSubdivision( //
      GeodesicInterface geodesicInterface, //
      Scalar pq_f, Scalar rs_f, Scalar pqrs) {
    this.geodesicInterface = Objects.requireNonNull(geodesicInterface);
    this.lo_pq = pq_f;
    this.lo_rs = rs_f;
    this.lo_pqrs = pqrs;
    hi_pq = RealScalar.ONE.subtract(lo_rs);
    hi_rs = RealScalar.ONE.subtract(lo_pq);
    hi_pqrs = RealScalar.ONE.subtract(lo_pqrs);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    Tensor curve = Unprotect.empty(2 * length);
    for (int index = 0; index < length; ++index) {
      Tensor p = tensor.get((index - 1 + length) % length);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % length);
      Tensor s = tensor.get((index + 2) % length);
      ScalarTensorFunction c_pq = geodesicInterface.curve(p, q);
      ScalarTensorFunction c_rs = geodesicInterface.curve(r, s);
      curve.append(lo(c_pq, c_rs)).append(hi(c_pq, c_rs));
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    throw new UnsupportedOperationException();
  }

  // @return point between q and r but more towards q
  private Tensor lo(ScalarTensorFunction c_pq, ScalarTensorFunction c_rs) {
    Tensor pq = c_pq.apply(lo_pq);
    Tensor rs = c_rs.apply(lo_rs);
    return geodesicInterface.split(pq, rs, lo_pqrs);
  }

  // @return point between q and r but more towards r
  private Tensor hi(ScalarTensorFunction c_pq, ScalarTensorFunction c_rs) {
    Tensor pq = c_pq.apply(hi_pq);
    Tensor rs = c_rs.apply(hi_rs);
    return geodesicInterface.split(pq, rs, hi_pqrs);
  }
}

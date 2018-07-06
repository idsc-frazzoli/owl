// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class FourPointCurveSubdivision implements CurveSubdivision, Serializable {
  private final static Scalar N1_8 = RationalScalar.of(-1, 8);
  private final static Scalar P9_8 = RationalScalar.of(+9, 8);
  private final static Scalar N1_4 = RationalScalar.of(-1, 4);
  private final static Scalar P1_4 = RationalScalar.of(+1, 4);
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
      curve.append(q).append(center(p, q, r, t));
    }
    return curve;
  }

  @Override
  public Tensor string(Tensor tensor) {
    Tensor curve = Tensors.empty();
    {
      Tensor p = tensor.get(0);
      Tensor q = tensor.get(1);
      Tensor r = tensor.get(2);
      curve.append(p).append(triple(p, q, r));
    }
    int last = tensor.length() - 2;
    for (int index = 1; index < last; ++index) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(index + 1);
      Tensor t = tensor.get(index + 2);
      curve.append(q).append(center(p, q, r, t));
    }
    {
      Tensor p = tensor.get(last + 1);
      Tensor q = tensor.get(last);
      Tensor r = tensor.get(last - 1);
      curve.append(q).append(triple(p, q, r)).append(p);
    }
    return curve;
  }

  /** @param p
   * @param q
   * @param r
   * @param t
   * @return point between q and r */
  private Tensor center(Tensor p, Tensor q, Tensor r, Tensor t) {
    Tensor pq = geodesicInterface.split(p, q, P9_8);
    Tensor rt = geodesicInterface.split(r, t, N1_8);
    return geodesicInterface.split(pq, rt, RationalScalar.HALF);
  }

  /** @param p
   * @param q
   * @param r
   * @return point between p and q */
  private Tensor triple(Tensor p, Tensor q, Tensor r) {
    Tensor pq = geodesicInterface.split(p, q, P1_4);
    Tensor rt = geodesicInterface.split(q, r, N1_4);
    return geodesicInterface.split(pq, rt, RationalScalar.HALF);
  }
}

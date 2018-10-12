// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** C1 interpolatory four-point scheme
 * Dubuc 1986, Dyn/Gregory/Levin 1987
 * 
 * Dyn/Sharon 2014 p.14 show that for general omega the contractivity is mu = 4 * omega + 1/2
 * 
 * for the important case omega = 1/16 the contractivity factor is mu = 3/4 */
public class FourPointCurveSubdivision extends BSpline1CurveSubdivision {
  private final static Scalar P1_16 = RationalScalar.of(1, 16);
  private final static Scalar N1_4 = RationalScalar.of(-1, 4);
  private final static Scalar P1_4 = RationalScalar.of(+1, 4);
  // ---
  private final Scalar lambda;

  public FourPointCurveSubdivision(GeodesicInterface geodesicInterface, Scalar omega) {
    super(geodesicInterface);
    lambda = omega.add(omega).add(RealScalar.ONE);
  }

  /** standard four point scheme with omega = 1/16
   * 
   * @param geodesicInterface */
  public FourPointCurveSubdivision(GeodesicInterface geodesicInterface) {
    this(geodesicInterface, P1_16);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    Tensor curve = Tensors.empty();
    for (int index = 0; index < tensor.length(); ++index) {
      Tensor p = tensor.get((index - 1 + tensor.length()) % tensor.length());
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % tensor.length());
      Tensor s = tensor.get((index + 2) % tensor.length());
      curve.append(q).append(center(p, q, r, s));
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    if (tensor.length() < 3)
      return new BSpline3CurveSubdivision(geodesicInterface).string(tensor);
    // ---
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
      Tensor s = tensor.get(index + 2);
      curve.append(q).append(center(p, q, r, s));
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
   * @param s
   * @return point between q and r */
  Tensor center(Tensor p, Tensor q, Tensor r, Tensor s) {
    return center( //
        geodesicInterface.split(p, q, lambda), //
        geodesicInterface.split(s, r, lambda));
  }

  /** @param p
   * @param q
   * @param r
   * @return point between p and q */
  Tensor triple(Tensor p, Tensor q, Tensor r) {
    return center( //
        geodesicInterface.split(p, q, P1_4), //
        geodesicInterface.split(q, r, N1_4));
  }
}

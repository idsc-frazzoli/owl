// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** C1 interpolatory four-point scheme
 * Dubuc 1986, Dyn/Gregory/Levin 1987
 * 
 * <pre>
 * weights = {-1, 9, 9, -1} / 16
 * weights = {-omega, 1/2+omega, 1/2+omega, -omega} (generalized)
 * </pre>
 * 
 * Dyn/Sharon 2014 p.14 show that for general omega the contractivity is mu = 4 * omega + 1/2
 * 
 * for the important case omega = 1/16 the contractivity factor is mu = 3/4 */
public class FourPointCurveSubdivision extends BSpline1CurveSubdivision {
  private final static Scalar P1_16 = RationalScalar.of(1, 16);
  private final static Scalar N1_4 = RationalScalar.of(-1, 4);
  private final static Scalar P1_4 = RationalScalar.of(+1, 4);
  private final static Scalar P5_4 = RationalScalar.of(+5, 4);
  private final static Scalar P3_4 = RationalScalar.of(+3, 4);
  // ---
  protected final SplitInterface splitInterface;
  private final Scalar lambda;
  private final Scalar _1_lam;

  public FourPointCurveSubdivision(SplitInterface splitInterface, Scalar omega) {
    super(splitInterface);
    this.splitInterface = splitInterface;
    Scalar two_omega = omega.add(omega);
    _1_lam = two_omega.negate();
    lambda = two_omega.add(RealScalar.ONE);
  }

  /** standard four point scheme with omega = 1/16
   * 
   * @param splitInterface */
  public FourPointCurveSubdivision(SplitInterface splitInterface) {
    this(splitInterface, P1_16);
  }

  @Override // from CurveSubdivision
  public Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    Tensor curve = Tensors.reserve(2 * length);
    for (int index = 0; index < length; ++index) {
      Tensor p = tensor.get((index - 1 + length) % length);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get((index + 1) % length);
      Tensor s = tensor.get((index + 2) % length);
      curve.append(q).append(center(p, q, r, s));
    }
    return curve;
  }

  @Override // from CurveSubdivision
  public Tensor string(Tensor tensor) {
    int length = tensor.length();
    if (length < 3)
      return new BSpline3CurveSubdivision(splitInterface).string(tensor);
    // ---
    Tensor curve = Tensors.reserve(2 * length);
    {
      Tensor p = tensor.get(0);
      Tensor q = tensor.get(1);
      Tensor r = tensor.get(2);
      curve.append(p).append(triple_lo(p, q, r));
    }
    int last = length - 2;
    for (int index = 1; index < last; ++index) {
      Tensor p = tensor.get(index - 1);
      Tensor q = tensor.get(index);
      Tensor r = tensor.get(index + 1);
      Tensor s = tensor.get(index + 2);
      curve.append(q).append(center(p, q, r, s));
    }
    {
      Tensor p = tensor.get(last - 1);
      Tensor q = tensor.get(last);
      Tensor r = tensor.get(last + 1);
      curve.append(q).append(triple_hi(p, q, r)).append(r);
    }
    return curve;
  }

  /** @param p
   * @param q
   * @param r
   * @param s
   * @return point between q and r */
  Tensor center(Tensor p, Tensor q, Tensor r, Tensor s) {
    return midpoint( //
        splitInterface.split(p, q, lambda), //
        splitInterface.split(r, s, _1_lam));
  }

  /** @param p
   * @param q
   * @param r
   * @return point between p and q */
  Tensor triple_lo(Tensor p, Tensor q, Tensor r) {
    return midpoint( //
        splitInterface.split(p, q, P1_4), //
        splitInterface.split(q, r, N1_4));
  }

  /** @param p
   * @param q
   * @param r
   * @return point between q and r */
  Tensor triple_hi(Tensor p, Tensor q, Tensor r) {
    return midpoint( //
        splitInterface.split(p, q, P5_4), //
        splitInterface.split(q, r, P3_4));
  }
}

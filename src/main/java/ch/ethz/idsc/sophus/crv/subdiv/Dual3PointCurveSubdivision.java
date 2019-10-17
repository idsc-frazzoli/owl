// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import java.io.Serializable;
import java.util.Objects;

import ch.ethz.idsc.sophus.math.Nocopy;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.BinaryAverage;

/** dual scheme
 * 
 * Chaikin's rule is used for the generation of the first and last point */
public abstract class Dual3PointCurveSubdivision implements CurveSubdivision, Serializable {
  private static final Scalar _1_4 = RationalScalar.of(1, 4);
  private static final Scalar _3_4 = RationalScalar.of(3, 4);
  // ---
  protected final BinaryAverage binaryAverage;

  /** @param binaryAverage non-null */
  public Dual3PointCurveSubdivision(BinaryAverage binaryAverage) {
    this.binaryAverage = Objects.requireNonNull(binaryAverage);
  }

  @Override // from CurveSubdivision
  public final Tensor cyclic(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Nocopy curve = new Nocopy(2 * length);
    Tensor p = Last.of(tensor);
    Tensor q = tensor.get(0);
    for (int index = 1; index <= length; ++index) {
      Tensor r = tensor.get(index % length);
      curve.append(lo(p, q, r)).append(hi(p, p = q, q = r));
    }
    return curve.tensor();
  }

  @Override // from CurveSubdivision
  public final Tensor string(Tensor tensor) {
    ScalarQ.thenThrow(tensor);
    int length = tensor.length();
    if (length < 2)
      return tensor.copy();
    Nocopy curve = new Nocopy(2 * length);
    Tensor p = tensor.get(0);
    Tensor q = tensor.get(1);
    curve.append(lo(p, q)); // Chaikin's rule
    for (int index = 2; index < length; ++index) {
      Tensor r = tensor.get(index);
      curve.append(lo(p, q, r)).append(hi(p, p = q, q = r));
    }
    curve.append(hi(p, q)); // Chaikin's rule
    return curve.tensor();
  }

  /** @param p
   * @param q
   * @param r
   * @return point between p and q but more towards q */
  protected abstract Tensor lo(Tensor p, Tensor q, Tensor r);

  /** @param p
   * @param q
   * @param r
   * @return point between q and r but more towards q */
  protected abstract Tensor hi(Tensor p, Tensor q, Tensor r);

  // point between p and q but more towards p
  private Tensor lo(Tensor p, Tensor q) {
    return binaryAverage.split(p, q, _1_4);
  }

  // point between p and q but more towards q
  private Tensor hi(Tensor p, Tensor q) {
    return binaryAverage.split(p, q, _3_4);
  }
}

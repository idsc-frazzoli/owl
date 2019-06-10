// code by ob, jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.LinearSolve;

/* package */ class Se2Skew {
  /** @param xya element in SE(2)
   * @param weight
   * @return */
  public static Se2Skew of(Tensor xya, Scalar weight) {
    Scalar angle = xya.Get(2).negate();
    Tensor so2Skew = So2Skew.of(angle, weight);
    return new Se2Skew(so2Skew, so2Skew.dot(RotationMatrix.of(angle).dot(xya.extract(0, 2))));
  }

  // ---
  /** matrix with dimensions 2 x 2 */
  private final Tensor lhs;
  /** vector of length 2 */
  private final Tensor rhs;

  private Se2Skew(Tensor lhs, Tensor rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  public Se2Skew add(Se2Skew se2Decomp) {
    return new Se2Skew(lhs.add(se2Decomp.lhs), rhs.add(se2Decomp.rhs));
  }

  public Tensor solve() {
    return LinearSolve.of(lhs, rhs);
  }
}
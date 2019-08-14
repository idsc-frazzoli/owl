// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.qty.QuaternionToRotationMatrix;

public enum Se3Matrix {
  ;
  private static final Tensor UNIT3 = UnitVector.of(4, 3);

  /** @param R orthogonal matrix of dimensions 3 x 3
   * @param t vector of length at least 3
   * @return matrix of dimensions 4 x 4
   * @throws Exception if first 3 entries of t are not scalars
   * @see QuaternionToRotationMatrix */
  public static Tensor of(Tensor R, Tensor t) {
    return Unprotect.byRef( //
        R.get(0).append(t.Get(0)), //
        R.get(1).append(t.Get(1)), //
        R.get(2).append(t.Get(2)), //
        UNIT3.copy());
  }

  /** @param matrix of dimensions 4 x 4
   * @return 3 x 3 rotation matrix that is part of given matrix */
  public static Tensor rotation(Tensor matrix) {
    return Tensor.of(matrix.stream().limit(3).map(row -> row.extract(0, 3)));
  }

  /** @param matrix of dimensions 4 x 4
   * @return vector of length 3 that is part of given matrix */
  public static Tensor translation(Tensor matrix) {
    return Tensor.of(matrix.stream().limit(3).map(row -> row.get(3)));
  }
}

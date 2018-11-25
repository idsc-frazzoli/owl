// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.UnitVector;

public enum Se3Utils {
  ;
  private static final Tensor UNIT3 = UnitVector.of(4, 3);

  /** @param R 3x3 orthogonal matrix
   * @param t vector of length 3
   * @return */
  public static Tensor toMatrix4x4(Tensor R, Tensor t) {
    return Tensors.of( //
        Join.of(R.get(0), t.extract(0, 1)), //
        Join.of(R.get(1), t.extract(1, 2)), //
        Join.of(R.get(2), t.extract(2, 3)), //
        UNIT3);
  }

  /** @param matrix of dimensions 4 x 4
   * @return 3x3 rotation matrix that is part of given matrix */
  public static Tensor rotation(Tensor matrix) {
    return Tensor.of(matrix.stream().limit(3).map(row -> row.extract(0, 3)));
  }

  /** @param matrix of dimensions 4 x 4
   * @return vector of length 3 that is part of given matrix */
  public static Tensor translation(Tensor matrix) {
    return Tensor.of(matrix.stream().limit(3).map(row -> row.get(3)));
  }
}

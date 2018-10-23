// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

// TODO test and use in more places
public enum Se3Utils {
  ;
  public static Tensor of(Tensor R, Tensor Vu) {
    return Tensors.of( //
        Join.of(R.get(0), Vu.extract(0, 1)), //
        Join.of(R.get(1), Vu.extract(1, 2)), //
        Join.of(R.get(2), Vu.extract(2, 3)), //
        Tensors.vector(0, 0, 0, 1));
  }

  /** @param g 4x4
   * @return */
  public static Tensor rotation(Tensor g) {
    return Tensor.of(g.extract(0, 3).stream().map(row -> row.extract(0, 3)));
  }
}

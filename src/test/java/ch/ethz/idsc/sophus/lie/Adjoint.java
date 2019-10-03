// code by jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Transpose;

/** Ad[X * Y] == Ad[X] . Ad[Y] */
public class Adjoint {
  private final LieGroup lieGroup;
  private final Tensor vectors;

  public Adjoint(LieGroup lieGroup, Tensor vectors) {
    this.lieGroup = lieGroup;
    this.vectors = vectors;
  }

  public Tensor matrix(Tensor tensor) {
    return Transpose.of(Tensor.of(vectors.stream().map(lieGroup.element(tensor)::adjoint)));
  }
}

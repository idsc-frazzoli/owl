// code by jph
package ch.ethz.idsc.owl.math.planar;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.NormalizeUnlessZero;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

public enum Normal2D {
  ;
  private static final TensorUnaryOperator NORMALIZE = NormalizeUnlessZero.with(Norm._2);
  private static final Tensor ZEROS = Array.zeros(2);

  /** @param points of the form {{p1x, p1y}, {p2x, p2y}, ..., {pNx, pNy}}
   * @return matrix of the form {{n1x, n1y}, {n2x, n2y}, ..., {nNx, nNy}} */
  public static Tensor string(Tensor points) {
    Tensor normal = Tensors.empty();
    int length = points.length();
    if (2 < length) {
      Tensor a = points.get(0);
      Tensor b = points.get(1);
      normal.append(process(b.subtract(a)));
    } else //
    if (0 < length)
      normal.append(ZEROS);
    for (int index = 1; index < length - 1; ++index) {
      Tensor a = points.get(index - 1);
      Tensor c = points.get(index + 1);
      normal.append(process(c.subtract(a)));
    }
    if (2 < length) {
      Tensor b = points.get(length - 2);
      Tensor c = points.get(length - 1);
      normal.append(process(c.subtract(b)));
    } else //
    if (1 < length)
      normal.append(ZEROS);
    return normal;
  }

  private static Tensor process(Tensor tangent) {
    return NORMALIZE.apply(Cross2D.of(tangent));
  }
}

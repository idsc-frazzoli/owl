// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
/* package */ abstract class MovingDomain2D {
  private final Tensor origin;
  final Tensor domain;
  final Tensor[][] weights;
  /* for visualization only */
  private Tensor _wgs = null;

  /** @param origin reference control points that will be associated to given targets
   * @param tensorUnaryOperator
   * @param domain */
  public MovingDomain2D(Tensor origin, TensorUnaryOperator tensorUnaryOperator, Tensor domain) {
    this.origin = origin;
    this.domain = domain;
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    weights = new Tensor[rows][cols];
    for (int cx = 0; cx < rows; ++cx)
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = domain.get(cx, cy);
        weights[cx][cy] = tensorUnaryOperator.apply(point);
      }
  }

  public final Tensor origin() {
    return origin;
  }

  public abstract Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean);

  /** @return array of weights for visualization */
  public final Tensor arrayReshape_weights() {
    if (Objects.isNull(_wgs)) {
      int rows = weights.length;
      int cols = weights[0].length;
      Tensor wgs = Tensors.matrix((i, j) -> weights[i][j], rows, cols);
      List<Integer> dims = Dimensions.of(wgs);
      _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    }
    return _wgs;
  }
}

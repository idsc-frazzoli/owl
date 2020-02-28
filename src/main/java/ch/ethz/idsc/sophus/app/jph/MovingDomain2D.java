// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Transpose;

/** Reference:
 * "Weighted Averages on Surfaces"
 * by Daniele Panozzo, Ilya Baran, Olga Diamanti, Olga Sorkine-Hornung */
class MovingDomain2D {
  final Tensor origin;
  final Tensor domain;
  final Tensor[][] weights;
  private final Tensor _wgs;

  public MovingDomain2D(Tensor origin, BarycentricCoordinate barycentricCoordinate, Tensor domain) {
    this.origin = origin;
    this.domain = domain;
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    weights = new Tensor[rows][cols];
    for (int cx = 0; cx < rows; ++cx) {
      for (int cy = 0; cy < cols; ++cy) {
        Tensor point = domain.get(cx, cy);
        weights[cx][cy] = barycentricCoordinate.weights(origin, point);
      }
    }
    {
      Tensor wgs = Tensors.matrix((i, j) -> weights[i][j], rows, cols);
      List<Integer> dims = Dimensions.of(wgs);
      _wgs = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
    }
  }

  public Tensor origin() {
    return origin;
  }

  public Tensor[][] forward(Tensor target, BiinvariantMean biinvariantMean) {
    int rows = domain.length();
    int cols = Unprotect.dimension1(domain);
    Tensor[][] array = new Tensor[rows][cols];
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cx][cy] = biinvariantMean.mean(target, weights[cx][cy]);
    });
    return array;
  }

  public Tensor weights() {
    return _wgs;
  }
}

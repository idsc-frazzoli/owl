// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** GeodesicCenter projects a sequence of points to their geodesic center
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class Se2BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(SmoothingKernel smoothingKernel) {
    return new Se2BiinvariantMeanCenter(Objects.requireNonNull(smoothingKernel));
  }

  // ---
  private final SmoothingKernel smoothingKernel;

  private Se2BiinvariantMeanCenter(SmoothingKernel smoothingKernel) {
    this.smoothingKernel = smoothingKernel;
  }

  private Tensor weights(int radius) {
    Tensor weights = Tensors.empty();
    for (int index = 0; index < (2 * radius + 1); ++index) {
      weights.append(smoothingKernel.apply(RealScalar.of(index / (2 * radius))));
    }
    return null;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    System.out.println(tensor.length());
    if (tensor.get(0).length() == -1) {
      return tensor;
    } else {
      int radius = tensor.length();
      Tensor weights = weights(radius);
      return Se2BiinvariantMean.INSTANCE.mean(tensor, weights);
    }
  }
}

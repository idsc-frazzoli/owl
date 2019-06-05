// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Total;

/** Se2BiinvariantMeanCenter projects a sequence of points to their barycenter
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class Se2BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param smoothingKernel
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(SmoothingKernel smoothingKernel) {
    return new Se2BiinvariantMeanCenter(Objects.requireNonNull(smoothingKernel));
  }

  // ---
  private final SmoothingKernel smoothingKernel;

  private Se2BiinvariantMeanCenter(SmoothingKernel smoothingKernel) {
    this.smoothingKernel = smoothingKernel;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int radius = (tensor.length() - 1) / 2;
    WindowCenterSampler windowCenterSampler = new WindowCenterSampler(smoothingKernel);
    if (tensor.get(0).length() == -1) {
      return tensor;
    } else if (radius == 0) {
      return tensor.get(0);
    } else {
      Tensor weights = windowCenterSampler.apply(radius);
      weights = weights.divide(Total.ofVector(weights));
      return Se2BiinvariantMean.INSTANCE.mean(tensor, weights);
    }
  }
}

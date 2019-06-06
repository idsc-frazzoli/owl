// code by ob
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** Se2BiinvariantMeanCenter projects a sequence of points to their barycenter
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param biinvariantMeanInterface non-null
   * @param smoothingKernel non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMeanInterface biinvariantMeanInterface, SmoothingKernel smoothingKernel) {
    return new BiinvariantMeanCenter(Objects.requireNonNull(biinvariantMeanInterface), smoothingKernel);
  }

  // ---
  private final BiinvariantMeanInterface biinvariantMeanInterface;
  private final WindowCenterSampler windowCenterSampler;

  private BiinvariantMeanCenter(BiinvariantMeanInterface biinvariantMeanInterface, SmoothingKernel smoothingKernel) {
    this.biinvariantMeanInterface = biinvariantMeanInterface;
    windowCenterSampler = new WindowCenterSampler(smoothingKernel);
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int extent = (tensor.length() - 1) / 2;
    return biinvariantMeanInterface.mean(tensor, windowCenterSampler.apply(extent));
  }
}

// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a sequence of points to their barycenter
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param biinvariantMeanInterface non-null
   * @param windowFunction non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMeanInterface biinvariantMeanInterface, ScalarUnaryOperator windowFunction) {
    return new BiinvariantMeanCenter( //
        Objects.requireNonNull(biinvariantMeanInterface), //
        new WindowCenterSampler(windowFunction));
  }

  // ---
  private final BiinvariantMeanInterface biinvariantMeanInterface;
  private final IntegerTensorFunction integerTensorFunction;

  private BiinvariantMeanCenter(BiinvariantMeanInterface biinvariantMeanInterface, IntegerTensorFunction integerTensorFunction) {
    this.biinvariantMeanInterface = biinvariantMeanInterface;
    this.integerTensorFunction = integerTensorFunction;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int extent = (tensor.length() - 1) / 2;
    return biinvariantMeanInterface.mean(tensor, integerTensorFunction.apply(extent));
  }
}

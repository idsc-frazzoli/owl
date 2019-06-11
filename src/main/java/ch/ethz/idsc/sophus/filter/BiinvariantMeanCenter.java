// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.MemoFunction;
import ch.ethz.idsc.sophus.math.win.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a sequence of points to their barycenter
 * with each point weighted as provided by an external function.
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, Function<Integer, Tensor> function) {
    return new BiinvariantMeanCenter(Objects.requireNonNull(biinvariantMean), MemoFunction.wrap(function));
  }

  /** @param biinvariantMean non-null
   * @param windowFunction non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, ScalarUnaryOperator windowFunction) {
    return new BiinvariantMeanCenter(Objects.requireNonNull(biinvariantMean), WindowCenterSampler.of(windowFunction));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final Function<Integer, Tensor> function;

  private BiinvariantMeanCenter(BiinvariantMean biinvariantMean, Function<Integer, Tensor> function) {
    this.biinvariantMean = biinvariantMean;
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int extent = (tensor.length() - 1) / 2;
    return biinvariantMean.mean(tensor, function.apply(extent));
  }
}

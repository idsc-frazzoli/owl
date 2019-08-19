// code by ob, jph
package ch.ethz.idsc.sophus.flt.bm;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.UniformWindowSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a sequence of points to their barycenter
 * with each point weighted as provided by an external function. */
public class BiinvariantMeanCenter implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, Function<Integer, Tensor> function) {
    // TODO JPH test coverage
    return new BiinvariantMeanCenter(Objects.requireNonNull(biinvariantMean), MemoFunction.wrap(function));
  }

  /** @param biinvariantMean non-null
   * @param windowFunction non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, ScalarUnaryOperator windowFunction) {
    return new BiinvariantMeanCenter(Objects.requireNonNull(biinvariantMean), UniformWindowSampler.of(windowFunction));
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
    return biinvariantMean.mean(tensor, function.apply(tensor.length()));
  }
}

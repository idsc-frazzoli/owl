// code by ob, jph
package ch.ethz.idsc.sophus.filter.bm;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class BiinvariantMeanExtrapolation implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, Function<Integer, Tensor> function) {
    return new BiinvariantMeanExtrapolation( //
        Objects.requireNonNull(biinvariantMean), //
        Objects.requireNonNull(function));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final Function<Integer, Tensor> function;

  /* package */ BiinvariantMeanExtrapolation( //
      BiinvariantMean biinvariantMean, Function<Integer, Tensor> function) {
    this.biinvariantMean = biinvariantMean;
    this.function = function;
  }

  @Override
  public Tensor apply(Tensor sequence) {
    return biinvariantMean.mean(sequence, function.apply(sequence.length()));
  }
}
// code by ob, jph
package ch.ethz.idsc.sophus.filter.bm;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.sophus.math.win.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class BiinvariantMeanExtrapolation implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of( //
      BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel) {
    return new BiinvariantMeanExtrapolation( //
        Objects.requireNonNull(biinvariantMean), //
        Objects.requireNonNull(smoothingKernel));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final Function<Integer, Tensor> windowSideSampler;

  /* package */ BiinvariantMeanExtrapolation( //
      BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel) {
    this.biinvariantMean = biinvariantMean;
    windowSideSampler = WindowSideSampler.of(smoothingKernel);
  }

  // Assumes uniformly sampled signal!
  // TODO OB: refactor for better overview
  private static Tensor extrapolatoryWeights(Tensor weights) {
    AffineQ.require(weights);
    Tensor chronological = Tensors.empty();
    for (int index = 0; index < weights.length(); ++index)
      chronological.append(RealScalar.of(index));
    Scalar l = RealScalar.of(weights.length()).subtract(weights.dot(chronological)).reciprocal();
    Tensor extrapolatoryWeights = Tensors.empty();
    for (int index = 0; index < weights.length() - 1; ++index)
      extrapolatoryWeights.append(weights.Get(index).negate().multiply(l));
    extrapolatoryWeights.append(RealScalar.ONE.add(l).subtract(l.multiply(weights.Get(weights.length() - 1))));
    return extrapolatoryWeights;
  }

  @Override
  public Tensor apply(Tensor sequence) {
    return biinvariantMean.mean(sequence, extrapolatoryWeights(windowSideSampler.apply(sequence.length() - 1)));
  }
}
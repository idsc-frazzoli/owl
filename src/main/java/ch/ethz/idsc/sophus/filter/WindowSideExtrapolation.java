// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.sophus.math.win.WindowSideSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class WindowSideExtrapolation implements Function<Integer, Tensor>, Serializable {
  /** @param function non-null
   * @return
   * @throws Exception if either input parameter is null */
  public static Function<Integer, Tensor> of(ScalarUnaryOperator smoothingKernel) {
    return MemoFunction.wrap(new WindowSideExtrapolation(smoothingKernel));
  }

  // ---
  private final Function<Integer, Tensor> windowSideSampler;

  /* package */ WindowSideExtrapolation(ScalarUnaryOperator smoothingKernel) {
    windowSideSampler = WindowSideSampler.of(smoothingKernel);
  }

  // Assumes uniformly sampled signal!
  // TODO OB: refactor for better overview
  @Override
  public Tensor apply(Integer t) {
    Tensor weights = windowSideSampler.apply(t - 1);
    AffineQ.require(weights);
    Tensor chronological = Tensors.empty();
    for (int index = 0; index < weights.length(); ++index)
      chronological.append(RealScalar.of(index));
    Scalar l = RealScalar.of(weights.length()).subtract(weights.dot(chronological)).reciprocal();
    Tensor extrapolatoryWeights = Tensors.empty();
    for (int index = 0; index < weights.length() - 1; ++index)
      extrapolatoryWeights.append(weights.Get(index).negate().multiply(l));
    extrapolatoryWeights.append(RealScalar.ONE.add(l).subtract(l.multiply(weights.Get(weights.length() - 1))));
    return extrapolatoryWeights.unmodifiable();
  }
}
// code by ob, jph
package ch.ethz.idsc.sophus.flt;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.sophus.math.win.HalfWindowSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Range;
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
  private final Function<Integer, Tensor> halfWindowSampler;

  /* package */ WindowSideExtrapolation(ScalarUnaryOperator smoothingKernel) {
    halfWindowSampler = HalfWindowSampler.of(smoothingKernel);
  }

  // Assumes uniformly sampled signal!
  @Override
  public Tensor apply(Integer t) {
    Tensor weights = halfWindowSampler.apply(t);
    Tensor chronological = Range.of(0, weights.length());
    Scalar distance = RealScalar.of(weights.length() - 1).subtract(weights.dot(chronological));
    Tensor extrapolatory = weights.extract(0, weights.length() - 1).negate();
    extrapolatory.append(RealScalar.ONE.subtract(Last.of(weights)).add(distance));
    return extrapolatory.divide(distance).unmodifiable();
  }
}
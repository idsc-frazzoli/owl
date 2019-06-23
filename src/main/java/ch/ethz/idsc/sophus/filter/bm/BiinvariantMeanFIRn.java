// code by ob, jph
package ch.ethz.idsc.sophus.filter.bm;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.math.win.AffineQ;
import ch.ethz.idsc.sophus.math.win.WindowSideSampler;
import ch.ethz.idsc.sophus.util.BoundedLinkedList;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class BiinvariantMeanFIRn implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of( //
      SplitInterface splitInterface, BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new BiinvariantMeanFIRn( //
        splitInterface, //
        Objects.requireNonNull(biinvariantMean), //
        Objects.requireNonNull(smoothingKernel), radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final SplitInterface splitInterface;
  private final BiinvariantMean biinvariantMean;
  private final Function<Integer, Tensor> windowSideSampler;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ BiinvariantMeanFIRn( //
      SplitInterface splitInterface, BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.splitInterface = splitInterface;
    this.biinvariantMean = biinvariantMean;
    windowSideSampler = WindowSideSampler.of(smoothingKernel);
    this.alpha = alpha;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
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
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : biinvariantMean.mean(Tensor.of(boundedLinkedList.stream()), extrapolatoryWeights(windowSideSampler.apply(boundedLinkedList.size() - 1)));
    boundedLinkedList.add(x);
    return splitInterface.split(value, x, alpha);
  }
}
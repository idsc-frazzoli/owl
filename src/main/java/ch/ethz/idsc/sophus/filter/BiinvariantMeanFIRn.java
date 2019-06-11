// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
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
  public static TensorUnaryOperator of(BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new BiinvariantMeanFIRn(//
        Objects.requireNonNull(biinvariantMean), //
        Objects.requireNonNull(smoothingKernel), radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final BiinvariantMean biinvariantMean;
  private final ScalarUnaryOperator smoothingKernel;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ BiinvariantMeanFIRn(BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.biinvariantMean = biinvariantMean;
    this.smoothingKernel = smoothingKernel;
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
    Scalar distance = RealScalar.of(weights.length() - 1).subtract(weights.dot(chronological));
    Tensor extrapolatoryWeights = Tensors.empty();
    for (int index = 0; index < weights.length() - 1; ++index)
      extrapolatoryWeights.append(weights.Get(index).negate().divide(distance));
    extrapolatoryWeights.append(distance.reciprocal().multiply(RealScalar.ONE.subtract(weights.Get(weights.length() - 1))).add(RealScalar.ONE));
    return extrapolatoryWeights;
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : biinvariantMean.mean(Tensor.of(boundedLinkedList.stream()),
            extrapolatoryWeights(WindowSideSampler.of(smoothingKernel).apply(boundedLinkedList.size() - 1)));
    boundedLinkedList.add(x);
    return Se2Geodesic.INSTANCE.split(value, x, alpha);
  }
}

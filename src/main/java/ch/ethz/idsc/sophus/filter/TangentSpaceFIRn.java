// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupElement;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class TangentSpaceFIRn implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceFIRn(//
        Objects.requireNonNull(smoothingKernel), radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final SmoothingKernel smoothingKernel;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ TangentSpaceFIRn(SmoothingKernel smoothingKernel, int radius, Scalar alpha) {
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


  private static Tensor process(Tensor tensor) {
    LieGroup lieGroup = Se2Group.INSTANCE;
    LieExponential lieExponential = Se2CoveringExponential.INSTANCE;
    SmoothingKernel smoothingKernel = SmoothingKernel.GAUSSIAN;
    Function<Integer, Tensor> function = WindowSideSampler.of(smoothingKernel);
    int length = tensor.length();
    Tensor extrapolatoryWeights = extrapolatoryWeights(function.apply(length-1));
    LieGroupElement last = lieGroup.element(tensor.get(length-1));
    Tensor tangents = Tensor.of(tensor.stream().map(last.inverse()::combine).map(lieExponential::log));
    return last.combine(lieExponential.exp(extrapolatoryWeights.dot(tangents)));
  }
  
  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : Se2Geodesic.INSTANCE.split(process(Tensor.of(boundedLinkedList.stream())), x, alpha);
    boundedLinkedList.add(value);
    return value;
  }
}

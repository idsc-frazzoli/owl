// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.owl.data.BoundedLinkedList;
import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupElement;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.WindowSideSampler;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** BiinvariantMeanCenter projects a uniform sequence of points to their extrapolate
 * with each point weighted as provided by an external function. */
public class TangentSpaceFIRnNEW implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceFIRnNEW(//
        Objects.requireNonNull(geodesicDisplay), Objects.requireNonNull(smoothingKernel), radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final ScalarUnaryOperator smoothingKernel;
  private final LieGroup lieGroup;
  private final GeodesicInterface geodesicInterface;
  private final LieExponential lieExponential;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ TangentSpaceFIRnNEW(GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.smoothingKernel = smoothingKernel;
    this.alpha = alpha;
    this.boundedLinkedList = new BoundedLinkedList<>(radius);
    this.lieGroup = geodesicDisplay.lieGroup();
    this.geodesicInterface = geodesicDisplay.geodesicInterface();
    this.lieExponential = geodesicDisplay.lieExponential();
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

  private Tensor process(Tensor tensor) {
    Function<Integer, Tensor> function = WindowSideSampler.of(smoothingKernel);
    int length = tensor.length();
    Tensor extrapolatoryWeights = extrapolatoryWeights(function.apply(length - 1));
    LieGroupElement last = lieGroup.element(tensor.get(length - 1));
    Tensor tangents = Tensor.of(tensor.stream().map(last.inverse()::combine).map(lieExponential::log));
    return last.combine(lieExponential.exp(extrapolatoryWeights.dot(tangents)));
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : geodesicInterface.split(process(Tensor.of(boundedLinkedList.stream())), x, alpha);
    boundedLinkedList.add(x);
    return value;
  }
}

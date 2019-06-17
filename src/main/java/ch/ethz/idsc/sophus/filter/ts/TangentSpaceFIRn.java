// code by ob, jph
package ch.ethz.idsc.sophus.filter.ts;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
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
public class TangentSpaceFIRn implements TensorUnaryOperator {
  /** @param biinvariantMean non-null
   * @param function non-null
   * @return operator that maps a sequence of odd number of points to their barycenter
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    return new TangentSpaceFIRn( //
        geodesicDisplay, //
        Objects.requireNonNull(smoothingKernel), radius, //
        Objects.requireNonNull(alpha));
  }

  // ---
  private final Function<Integer, Tensor> windowFunction;
  private final LieGroup lieGroup;
  private final GeodesicInterface geodesicInterface;
  private final LieExponential lieExponential;
  private final Scalar alpha;
  private final BoundedLinkedList<Tensor> boundedLinkedList;

  /* package */ TangentSpaceFIRn(GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    this.lieGroup = geodesicDisplay.lieGroup();
    this.geodesicInterface = geodesicDisplay.geodesicInterface();
    this.lieExponential = geodesicDisplay.lieExponential();
    windowFunction = WindowSideSampler.of(smoothingKernel);
    // private final ScalarUnaryOperator smoothingKernel;
    // private final Scalar alpha;
    // private final BoundedLinkedList<Tensor> boundedLinkedList;
    // /* package */ TangentSpaceFIRn(ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    //// this.smoothingKernel = smoothingKernel;
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
    extrapolatoryWeights.append(RealScalar.ONE.add(l).subtract(l.multiply(weights.Get(weights.length()-1))));
    return extrapolatoryWeights;
  }

  private Tensor process(Tensor tensor) {
    LieGroup lieGroup = Se2Group.INSTANCE;
    LieExponential lieExponential = Se2CoveringExponential.INSTANCE;
    int length = tensor.length();
    Tensor extrapolatoryWeights = extrapolatoryWeights(windowFunction.apply(length - 1));
    LieGroupElement last = lieGroup.element(tensor.get(length - 1));
    Tensor tangents = Tensor.of(tensor.stream().map(last.inverse()::combine).map(lieExponential::log));
    return last.combine(lieExponential.exp(extrapolatoryWeights.dot(tangents)));
  }

  @Override
  public Tensor apply(Tensor x) {
    Tensor value = boundedLinkedList.size() < 2 //
        ? x.copy()
        : Se2Geodesic.INSTANCE.split(process(Tensor.of(boundedLinkedList.stream())), x, alpha);
    boundedLinkedList.add(x);
    return value;
  }
}
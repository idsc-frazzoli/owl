// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.SymmetricVectorQ;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.LieGroupElement;
import ch.ethz.idsc.sophus.math.IntegerTensorFunction;
import ch.ethz.idsc.sophus.math.MemoFunction;
import ch.ethz.idsc.sophus.math.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** GeodesicCenterTangentSpace projects a sequence of point to the tangent space,
 * takes the weighted average and reprojects this average to the group
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
public class GeodesicCenterTangentSpace implements TensorUnaryOperator {
  /** @param geodesicInterface
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their geodesic center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieGroup lieGroup, LieExponential lieExponential, IntegerTensorFunction function) {
    return new GeodesicCenterTangentSpace(lieGroup, lieExponential, MemoFunction.wrap(function));
  }

  /** @param geodesicInterface
   * @param windowFunction
   * @return
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieGroup lieGroup, LieExponential lieExponential, ScalarUnaryOperator windowFunction) {
    return new GeodesicCenterTangentSpace(lieGroup, lieExponential, WindowCenterSampler.of(windowFunction));
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final Function<Integer, Tensor> function;

  private GeodesicCenterTangentSpace(LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function) {
    this.lieGroup = lieGroup;
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int radius = (tensor.length() - 1) / 2;
    LieGroupElement center = lieGroup.element(tensor.get(radius));
    LieGroupElement reference = center.inverse();
    Tensor tangentTensor = Tensor.of(tensor.stream().map(reference::combine).map(lieExponential::log));
    Tensor mask = SymmetricVectorQ.require(function.apply(radius));
    return center.combine(lieExponential.exp(mask.dot(tangentTensor)));
  }
}

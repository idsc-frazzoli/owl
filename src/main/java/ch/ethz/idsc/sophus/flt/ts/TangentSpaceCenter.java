// code by ob, jph
package ch.ethz.idsc.sophus.flt.ts;

import java.util.Objects;
import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.math.win.UniformWindowSampler;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** TangentSpaceCenter projects a sequence of point to the tangent space,
 * takes the weighted average and reprojects this average to the group
 * 
 * <p>Careful: the implementation only supports sequences with ODD number of elements!
 * When a sequence of even length is provided an Exception is thrown. */
// TODO OB is there an official name for this weighted average in the literature?
// TODO JPH OWL 045 refactor, document
public class TangentSpaceCenter implements TensorUnaryOperator {
  /** @param lieGroup
   * @param lieExponential
   * @param function that maps an extent to a weight mask of length == 2 * extent + 1
   * @return operator that maps a sequence of odd number of points to their weighted center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function) {
    return new TangentSpaceCenter(lieGroup, lieExponential, MemoFunction.wrap(function));
  }

  /** @param lieGroup
   * @param lieExponential
   * @param windowFunction
   * @return operator that maps a sequence of odd number of points to their weighted center
   * @throws Exception if either input parameter is null */
  public static TensorUnaryOperator of(LieGroup lieGroup, LieExponential lieExponential, ScalarUnaryOperator windowFunction) {
    return new TangentSpaceCenter(lieGroup, lieExponential, UniformWindowSampler.of(windowFunction));
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final Function<Integer, Tensor> function;

  private TangentSpaceCenter(LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function) {
    this.lieGroup = Objects.requireNonNull(lieGroup);
    this.lieExponential = Objects.requireNonNull(lieExponential);
    this.function = function;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    int radius = (tensor.length() - 1) / 2;
    LieGroupElement lieGroupElement = lieGroup.element(tensor.get(radius));
    Tensor tangents = Tensor.of(tensor.stream().map(lieGroupElement.inverse()::combine).map(lieExponential::log));
    return lieGroupElement.combine(lieExponential.exp(function.apply(tensor.length()).dot(tangents)));
  }
}

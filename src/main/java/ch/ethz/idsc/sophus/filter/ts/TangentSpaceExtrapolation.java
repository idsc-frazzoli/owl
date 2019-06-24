// code by ob, jph
package ch.ethz.idsc.sophus.filter.ts;

import java.util.function.Function;

import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupElement;
import ch.ethz.idsc.sophus.util.MemoFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** implementation computes a linear combination of tangent vectors */
public class TangentSpaceExtrapolation implements TensorUnaryOperator {
  /** @param lieGroup
   * @param lieExponential
   * @param function
   * @return */
  public static TensorUnaryOperator of(LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function) {
    return new TangentSpaceExtrapolation(lieGroup, lieExponential, MemoFunction.wrap(function));
  }

  // ---
  private final LieGroup lieGroup;
  private final LieExponential lieExponential;
  private final Function<Integer, Tensor> function;

  private TangentSpaceExtrapolation(LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function) {
    this.lieGroup = lieGroup;
    this.lieExponential = lieExponential;
    this.function = function;
  }

  @Override
  public Tensor apply(Tensor tensor) {
    LieGroupElement lieGroupElement = lieGroup.element(Last.of(tensor));
    Tensor tangents = Tensor.of(tensor.stream().map(lieGroupElement.inverse()::combine).map(lieExponential::log));
    return lieGroupElement.combine(lieExponential.exp(function.apply(tensor.length()).dot(tangents)));
  }
}

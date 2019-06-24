// code by jph
package ch.ethz.idsc.sophus.filter.ts;

import java.util.function.Function;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRn;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum TangentSpaceIIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function, //
      SplitInterface splitInterface, int radius, Scalar alpha) {
    TensorUnaryOperator tensorUnaryOperator = TangentSpaceExtrapolation.of(lieGroup, lieExponential, function);
    return CausalFilter.of(() -> GeodesicIIRn.of(tensorUnaryOperator, splitInterface, radius, alpha));
  }
}
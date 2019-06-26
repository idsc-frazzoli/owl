// code by jph
package ch.ethz.idsc.sophus.flt.ts;

import java.util.function.Function;

import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRnFilter;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum TangentSpaceFIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      LieGroup lieGroup, LieExponential lieExponential, Function<Integer, Tensor> function, //
      SplitInterface splitInterface, int radius, Scalar alpha) {
    return GeodesicFIRnFilter.of( //
        TangentSpaceExtrapolation.of(lieGroup, lieExponential, function), //
        splitInterface, radius, alpha);
  }
}
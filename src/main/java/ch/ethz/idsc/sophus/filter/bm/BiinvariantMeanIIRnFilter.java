// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import java.util.function.Function;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRn;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum BiinvariantMeanIIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      BiinvariantMean biinvariantMean, Function<Integer, Tensor> function, //
      SplitInterface splitInterface, int radius, Scalar alpha) {
    TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of(biinvariantMean, function);
    return CausalFilter.of(() -> GeodesicIIRn.of(geodesicExtrapolation, splitInterface, radius, alpha));
  }
}
// code by jph
package ch.ethz.idsc.sophus.flt.bm;

import java.util.function.Function;

import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRnFilter;
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
    return GeodesicIIRnFilter.of( //
        BiinvariantMeanExtrapolation.of(biinvariantMean, function), //
        splitInterface, radius, alpha);
  }
}
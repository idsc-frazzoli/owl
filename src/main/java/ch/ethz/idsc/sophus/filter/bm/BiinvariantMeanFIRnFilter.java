// code by jph
package ch.ethz.idsc.sophus.filter.bm;

import java.util.function.Function;

import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRn;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public enum BiinvariantMeanFIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      BiinvariantMean biinvariantMean, Function<Integer, Tensor> function, //
      SplitInterface splitInterface, int radius, Scalar alpha) {
    TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of(biinvariantMean, function);
    return CausalFilter.of(() -> GeodesicFIRn.of(geodesicExtrapolation, splitInterface, radius, alpha));
  }
  /** @param splitInterface
   * @param biinvariantMean
   * @param smoothingKernel
   * @param radius
   * @param alpha
   * @return */
  // public static TensorUnaryOperator of( //
  // BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, SplitInterface splitInterface, int radius, Scalar alpha) {
  // TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of(biinvariantMean, WindowSideExtrapolation.of(smoothingKernel));
  // return CausalFilter.of(() -> GeodesicFIRn.of(geodesicExtrapolation, splitInterface, radius, alpha));
  // }
}
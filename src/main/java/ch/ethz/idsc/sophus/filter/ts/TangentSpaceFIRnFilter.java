// code by jph
package ch.ethz.idsc.sophus.filter.ts;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.filter.CausalFilter;
import ch.ethz.idsc.sophus.filter.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRn;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum TangentSpaceFIRnFilter {
  ;
  public static TensorUnaryOperator of( //
      SplitInterface splitInterface, BiinvariantMean biinvariantMean, int radius, Scalar alpha) {
    TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of(biinvariantMean, MonomialExtrapolationMask.INSTANCE);
    return CausalFilter.of(() -> GeodesicFIRn.of(geodesicExtrapolation, splitInterface, radius, alpha));
  }

  /** @param splitInterface
   * @param biinvariantMean
   * @param smoothingKernel
   * @param radius
   * @param alpha
   * @return */
  public static TensorUnaryOperator of( //
      SplitInterface splitInterface, BiinvariantMean biinvariantMean, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
    TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of(biinvariantMean, WindowSideExtrapolation.of(smoothingKernel));
    return CausalFilter.of(() -> GeodesicFIRn.of(geodesicExtrapolation, splitInterface, radius, alpha));
  }
}
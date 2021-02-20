// code by ob, jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

/** in the current implementation all filters have the same performance for an arbitrary radius */
public enum GeodesicFilters {
  GEODESIC {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return GeodesicCenter.of(splitInterface, smoothingKernel);
    }
  },
  GEODESIC_MID {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return GeodesicCenterMidSeeded.of(splitInterface, smoothingKernel);
    }
  },
  BIINVARIANT_MEAN {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return BiinvariantMeanCenter.of(biinvariantMean, smoothingKernel);
    }
  };

  /** @param splitInterface
   * @param smoothingKernel
   * @param biinvariantMean
   * @return */
  public abstract TensorUnaryOperator supply( //
      SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean);

  /** @param geodesicDisplay
   * @param smoothingKernel
   * @return */
  public TensorUnaryOperator from(ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel) {
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    return supply(geodesicInterface, smoothingKernel, biinvariantMean);
  }
}
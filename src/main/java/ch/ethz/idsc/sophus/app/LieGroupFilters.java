// code by ob, jph
package ch.ethz.idsc.sophus.app;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** in the current implementation all filters have the same performance for an arbitrary radius */
public enum LieGroupFilters {
  GEODESIC {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, Exponential exponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenter.of(geodesicInterface, smoothingKernel);
    }
  }, //
  GEODESIC_MID {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, Exponential exponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenterMidSeeded.of(geodesicInterface, smoothingKernel);
    }
  }, //
  BIINVARIANT_MEAN {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, Exponential exponential, BiinvariantMean biinvariantMean) {
      return BiinvariantMeanCenter.of(biinvariantMean, smoothingKernel);
    }
  };

  /** @param geodesicInterface
   * @param smoothingKernel
   * @param lieGroup
   * @param exponential
   * @param biinvariantMean
   * @return */
  public abstract TensorUnaryOperator supply( //
      GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
      LieGroup lieGroup, Exponential exponential, BiinvariantMean biinvariantMean);

  /** @param geodesicDisplay
   * @param smoothingKernel
   * @return */
  public TensorUnaryOperator from(GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel) {
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Exponential exponential = geodesicDisplay.exponential();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    return supply(geodesicInterface, smoothingKernel, lieGroup, exponential, biinvariantMean);
  }
}
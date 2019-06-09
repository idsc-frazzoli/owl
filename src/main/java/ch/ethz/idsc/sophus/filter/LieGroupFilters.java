// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LieGroupFilters {
  GEODESIC {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenter.of(geodesicInterface, smoothingKernel);
    }
  }, //
  GEODESIC_MID {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return GeodesicCenterMidSeeded.of(geodesicInterface, smoothingKernel);
    }
  }, //
  TANGENT_SPACE {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return TangentSpaceCenter.of(lieGroup, lieExponential, smoothingKernel);
    }
  }, //
  BIINVARIANT_MEAN {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
        LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean) {
      return BiinvariantMeanCenter.of(biinvariantMean, smoothingKernel);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      GeodesicInterface geodesicInterface, ScalarUnaryOperator smoothingKernel, //
      LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean);
}
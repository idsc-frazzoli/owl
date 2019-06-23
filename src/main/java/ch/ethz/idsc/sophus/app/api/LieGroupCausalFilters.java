// code by ob, jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanFIRn;
import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanIIRn;
import ch.ethz.idsc.sophus.filter.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRn;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRn;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceFIRnNEW;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceIIRnNEW;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LieGroupCausalFilters {
  GEODESIC_FIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  }, //
  GEODESIC_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  }, //
  TANGENT_SPACE_FIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return TangentSpaceFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  TANGENT_SPACE_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return TangentSpaceIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_FIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return BiinvariantMeanFIRn.of( //
          geodesicDisplay.geodesicInterface(), //
          geodesicDisplay.biinvariantMean(), smoothingKernel, radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return BiinvariantMeanIIRn.of( //
          geodesicDisplay.geodesicInterface(), //
          geodesicDisplay.biinvariantMean(), smoothingKernel, radius, alpha);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha);
}
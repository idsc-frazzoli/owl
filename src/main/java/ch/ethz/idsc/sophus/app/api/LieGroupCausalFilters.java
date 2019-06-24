// code by ob, jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.filter.WindowSideExtrapolation;
import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRn;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRn;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceExtrapolation;
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
      TensorUnaryOperator geodesicExtrapolation = TangentSpaceExtrapolation.of( //
          geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(), WindowSideExtrapolation.of(smoothingKernel));
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicDisplay.geodesicInterface(), radius, alpha);
    }
  }, //
  TANGENT_SPACE_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      // return TangentSpaceIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
      // FIXME
      return null;
    }
  }, //
  BIINVARIANT_MEAN_FIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of( //
          geodesicDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicDisplay.geodesicInterface(), radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of( //
          geodesicDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicDisplay.geodesicInterface(), radius, alpha);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha);
}
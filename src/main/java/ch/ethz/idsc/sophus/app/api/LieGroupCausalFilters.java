// code by ob, jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanFIRnNEW;
import ch.ethz.idsc.sophus.filter.bm.BiinvariantMeanIIRnNEW;
import ch.ethz.idsc.sophus.filter.ga.GeodesicFIRnNEW;
import ch.ethz.idsc.sophus.filter.ga.GeodesicIIRnNEW;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceFIRnNEW;
import ch.ethz.idsc.sophus.filter.ts.TangentSpaceIIRnNEW;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LieGroupCausalFilters {
  GEODESIC_FIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return GeodesicIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  GEODESIC_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return GeodesicFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
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
      return BiinvariantMeanFIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      return BiinvariantMeanIIRnNEW.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      GeodesicDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha);
}
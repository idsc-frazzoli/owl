// code by ob, jph
package ch.ethz.idsc.sophus.opt;

import ch.ethz.idsc.sophus.crv.spline.MonomialExtrapolationMask;
import ch.ethz.idsc.sophus.flt.bm.BiinvariantMeanExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicExtrapolation;
import ch.ethz.idsc.sophus.flt.ga.GeodesicFIRn;
import ch.ethz.idsc.sophus.flt.ga.GeodesicIIRn;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum GeodesicCausalFilters {
  GEODESIC_FIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  },
  GEODESIC_IIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  },
  BIINVARIANT_MEAN_FIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of( //
          geodesicDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicDisplay.geodesicInterface(), radius, alpha);
    }
  },
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = BiinvariantMeanExtrapolation.of( //
          geodesicDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicDisplay.geodesicInterface(), radius, alpha);
    }
  };

  public abstract TensorUnaryOperator supply( //
      ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha);
}
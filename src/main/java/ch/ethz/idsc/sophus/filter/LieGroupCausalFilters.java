// code by ob, jph
package ch.ethz.idsc.sophus.filter;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum LieGroupCausalFilters {
  GEODESIC_FIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  }, //
  GEODESIC_IIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  }, //
  TANGENT_SPACE_FIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return TangentSpaceFIRn.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  TANGENT_SPACE_IIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return TangentSpaceIIRn.of(geodesicDisplay, smoothingKernel, radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_FIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return BiinvariantMeanFIRn.of(biinvariantMean, smoothingKernel, radius, alpha);
    }
  }, //
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
        ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
        int radius, Scalar alpha) {
      return BiinvariantMeanIIRn.of(biinvariantMean, smoothingKernel, radius, alpha);
    }
  }, //
  ;
  // ---
  public abstract TensorUnaryOperator supply( //
      TensorUnaryOperator geodesicExtrapolation, GeodesicDisplay geodesicDisplay, GeodesicInterface geodesicInterface, //
      ScalarUnaryOperator smoothingKernel, LieGroup lieGroup, LieExponential lieExponential, BiinvariantMean biinvariantMean, //
      int radius, Scalar alpha);
}
// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.util.UserHome;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.math.Loxodrome;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.space.SnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/* package */ enum LoxodromeData {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public static void main(String[] args) throws IOException {
    ScalarTensorFunction loxodrome = Loxodrome.of(RealScalar.of(.15));
    Tensor tensor = Subdivide.of(0, 4.5, 250).map(AbsSquared.FUNCTION).map(loxodrome);
    Export.of(UserHome.file("loxodrome_exact.csv"), tensor);
    Tensor noise = RandomVariate.of(NormalDistribution.of(0, .05), Dimensions.of(tensor));
    tensor = tensor.add(noise);
    tensor = Tensor.of(tensor.stream().map(NORMALIZE));
    Export.of(UserHome.file("loxodrome_noise.csv"), tensor);
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = GeodesicCenterFilter.of( //
          GeodesicCenter.of(SnGeodesic.INSTANCE, smoothingKernel), 7);
      Tensor smooth = tensorUnaryOperator.apply(tensor);
      Export.of(UserHome.file("loxodrome_" + smoothingKernel.name().toLowerCase() + ".csv"), smooth);
    }
  }
}

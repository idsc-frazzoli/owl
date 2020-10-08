// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.SmoothingKernel;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.hs.s2.Loxodrome;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.AbsSquared;

/* package */ enum LoxodromeData {
  ;
  private static final TensorUnaryOperator NORMALIZE = Normalize.with(Norm._2);

  public static void main(String[] args) throws IOException {
    Tensor tensor = Subdivide.of(0, 4.5, 250).map(AbsSquared.FUNCTION).map(Loxodrome.of(RealScalar.of(0.15)));
    Export.of(HomeDirectory.file("loxodrome_exact.csv"), tensor);
    Tensor noise = RandomVariate.of(NormalDistribution.of(0, 0.05), Dimensions.of(tensor));
    tensor = tensor.add(noise);
    tensor = Tensor.of(tensor.stream().map(NORMALIZE));
    Export.of(HomeDirectory.file("loxodrome_noise.csv"), tensor);
    GeodesicInterface geodesicInterface = S2GeodesicDisplay.INSTANCE.geodesicInterface();
    for (SmoothingKernel smoothingKernel : SmoothingKernel.values()) {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(geodesicInterface, smoothingKernel), 7);
      Tensor smooth = tensorUnaryOperator.apply(tensor);
      Export.of(HomeDirectory.file("loxodrome_" + smoothingKernel.name().toLowerCase() + ".csv"), smooth);
    }
  }
}

// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.io.IOException;

import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.hs.sn.S2Loxodrome;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.AbsSquared;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ enum LoxodromeData {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = Subdivide.of(0, 4.5, 250).map(AbsSquared.FUNCTION).map(S2Loxodrome.of(RealScalar.of(0.15)));
    Export.of(HomeDirectory.file("loxodrome_exact.csv"), tensor);
    Tensor noise = RandomVariate.of(NormalDistribution.of(0, 0.05), Dimensions.of(tensor));
    tensor = tensor.add(noise);
    tensor = Tensor.of(tensor.stream().map(Vector2Norm.NORMALIZE));
    Export.of(HomeDirectory.file("loxodrome_noise.csv"), tensor);
    Geodesic geodesicInterface = S2Display.INSTANCE.geodesicInterface();
    for (WindowFunctions windowFunctions : WindowFunctions.values()) {
      ScalarUnaryOperator smoothingKernel = windowFunctions.get();
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(geodesicInterface, smoothingKernel), 7);
      Tensor smooth = tensorUnaryOperator.apply(tensor);
      Export.of(HomeDirectory.file("loxodrome_" + smoothingKernel + ".csv"), smooth);
    }
  }
}

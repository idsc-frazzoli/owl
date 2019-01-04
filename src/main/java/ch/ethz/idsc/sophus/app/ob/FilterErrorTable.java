// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.TableBuilder;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Round;

enum FilterErrorTable {
  ;
  public static final File ROOT = new File("C:/Users/Oliver/Desktop/MA/owl_export");

  public static Tensor process(String name, int width) {
    TableBuilder tableBuilder = new TableBuilder();
    Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
        name + ".csv").stream().map(row -> row.extract(1, 4)));
    // Tensor control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + //
    // "2r/20180820T165637_3" + ".csv").stream().map(row -> row.extract(1, 4)));
    TensorUnaryOperator geodesicCenterFilter = //
        GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), width);
    System.out.println(width);
    GeodesicCausalFilteringIIR geodesicCausal1Filtering = GeodesicCausalFilteringIIR.se2(control, geodesicCenterFilter.apply(control), 0);
    Tensor alpharange = Subdivide.of(0.1, 1, 12);
    for (int j = 0; j < alpharange.length(); ++j) {
      Scalar alpha = alpharange.Get(j);
      // Tensor row = Tensors.of(alpha, geodesicCausal1Filtering.evaluate0Error(alpha), //
      // geodesicCausal1Filtering.evaluate1Error(alpha));
      Tensor row = Tensors.of(alpha, geodesicCausal1Filtering.evaluate0ErrorSeperated(alpha), //
          geodesicCausal1Filtering.evaluate1ErrorSeperated(alpha));
      tableBuilder.appendRow(row);
    }
    Tensor log = tableBuilder.toTable();
    System.out.println(Pretty.of(log.map(Round._4)));
    System.out.println("done!");
    return log;
  }

  public static void main(String[] args) throws IOException {
    // String dataname = "gyro/20181203T184122_1";
    // String dataname = "gyro/20181203T184122_2";
    // String dataname = "gyro/20181203T184122_3";
    // String dataname = "2r/20180820T165637_1";
    // String dataname = "2r/20180820T165637_2";
    String dataname = "2r/20180820T165637_3";
    // String dataname = "0w/20180702T133612_2";
    for (int width = 1; width < 12; width++) {
      Tensor tensor = process(dataname, width);
      Export.of(new File(ROOT, dataname.replace('/', '_') + "_" + width + ".csv"), tensor);
    }
  }
}

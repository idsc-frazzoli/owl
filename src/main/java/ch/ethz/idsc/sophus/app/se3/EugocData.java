// code by jph
package ch.ethz.idsc.sophus.app.se3;

import java.io.IOException;

import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Differences;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/* package */ enum EugocData {
  ;
  public static void main(String[] args) throws IOException {
    Tensor tensor = ResourceData.of("/dubilab/app/pose/2r/20180820T165637_2.csv");
    // System.out.println(Dimensions.of(tensor));
    Tensor poses = Tensor.of(tensor.stream() //
        .map(row -> row.extract(1, 4)));
    // Tensors.empty();
    // for (Tensor row : tensor) {
    // Tensor xyt = row.extract(1, 4);
    // poses.append(xyt);
    // }
    System.out.println(Dimensions.of(poses));
    Put.of(HomeDirectory.file("gokart_poses.file"), poses);
    {
      Tensor delta = Se2Differences.INSTANCE.apply(poses);
      Put.of(HomeDirectory.file("gokart_delta.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(HomeDirectory.file("gokart_poses_gauss.file"), smooth);
      Tensor delta = Se2Differences.INSTANCE.apply(smooth);
      Put.of(HomeDirectory.file("gokart_delta_gauss.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowFunctions.HAMMING.get()), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(HomeDirectory.file("gokart_poses_hammi.file"), smooth);
      Tensor delta = Se2Differences.INSTANCE.apply(smooth);
      Put.of(HomeDirectory.file("gokart_delta_hammi.file"), delta);
    }
  }
}

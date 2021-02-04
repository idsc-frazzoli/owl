// code by jph
package ch.ethz.idsc.sophus.app.se3;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.UzhSe3TxtFormat;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se3.Se3Differences;
import ch.ethz.idsc.sophus.lie.se3.Se3Geodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.sca.win.WindowFunctions;

/** the quaternions in the data set have norm of approximately
 * 1.00005... due to the use of float precision */
/* package */ enum UzhSe3Differences {
  ;
  public static void main(String[] args) throws IOException {
    File file = new File("/media/datahaki/media/resource/uzh/groundtruth", "outdoor_forward_5_davis.txt");
    Tensor poses = UzhSe3TxtFormat.of(file).extract(0, 1200);
    System.out.println(Dimensions.of(poses));
    Put.of(HomeDirectory.file("MH_04_difficult_poses.file"), poses);
    System.out.println("differences");
    {
      Tensor delta = Se3Differences.INSTANCE.apply(poses);
      Put.of(HomeDirectory.file("MH_04_difficult_delta.file"), delta);
    }
    System.out.println("smooth");
    {
      TensorUnaryOperator tensorUnaryOperator = //
          CenterFilter.of(GeodesicCenter.of(Se3Geodesic.INSTANCE, WindowFunctions.GAUSSIAN.get()), 4 * 3 * 2);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      System.out.println("store");
      Put.of(HomeDirectory.file("MH_04_difficult_poses_smooth.file"), smooth);
      System.out.println("differences");
      Tensor delta = Se3Differences.INSTANCE.apply(smooth);
      System.out.println("store");
      Put.of(HomeDirectory.file("MH_04_difficult_delta_smooth.file"), delta);
    }
  }
}

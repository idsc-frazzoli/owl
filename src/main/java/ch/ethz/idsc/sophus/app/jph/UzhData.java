// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se3.Se3Differences;
import ch.ethz.idsc.sophus.lie.se3.Se3Geodesic;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quaternion;
import ch.ethz.idsc.tensor.qty.QuaternionToRotationMatrix;

/** the quaternions in the data set have norm of approximately
 * 1.00005... due to the use of float precision */
/* package */ enum UzhData {
  ;
  public static void main(String[] args) throws IOException {
    System.out.println("here");
    Tensor tensor = Import.of(new File("/media/datahaki/media/resource/uzh/indoor_forward_7_davis_with_gt/groundtruth.csv"));
    System.out.println(Dimensions.of(tensor));
    // Export.of(HomeDirectory.file("MH_04_difficult_time.csv"), tensor.get(Tensor.ALL, 0));
    Tensor poses = Tensors.empty();
    for (Tensor row : tensor) {
      Tensor p = row.extract(2, 5);
      Tensor R = QuaternionToRotationMatrix.of(Quaternion.of(row.Get(5), row.extract(6, 9)));
      Tensor SE3 = Tensors.of( //
          Join.of(R.get(0), p.extract(0, 1)), //
          Join.of(R.get(1), p.extract(1, 2)), //
          Join.of(R.get(2), p.extract(2, 3)), //
          Tensors.vector(0, 0, 0, 1));
      poses.append(SE3);
      if (12500 <= poses.length())
        break;
    }
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
          CenterFilter.of(GeodesicCenter.of(Se3Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 4 * 3 * 2);
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

// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.QuaternionToRotationMatrix;
import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.LinearGroup;
import ch.ethz.idsc.owl.math.group.Se3Exponential;
import ch.ethz.idsc.owl.math.group.Se3Geodesic;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

enum EurocDemo {
  ;
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(LinearGroup.INSTANCE, Se3Exponential.INSTANCE);

  public static void main(String[] args) throws IOException {
    System.out.println("here");
    Tensor tensor = ResourceData.of("/3rdparty/app/pose/euroc/MH_04_difficult.csv");
    System.out.println(Dimensions.of(tensor));
    Export.of(UserHome.file("MH_04_difficult_time.csv"), tensor.get(Tensor.ALL, 0));
    Tensor poses = Tensors.empty();
    for (Tensor row : tensor) {
      Tensor p = row.extract(1, 4);
      Tensor q = row.extract(4, 8);
      Tensor R = QuaternionToRotationMatrix.of(q);
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
    Put.of(UserHome.file("MH_04_difficult_poses.file"), poses);
    System.out.println("differences");
    {
      Tensor delta = LIE_DIFFERENCES.apply(poses);
      Put.of(UserHome.file("MH_04_difficult_delta.file"), delta);
    }
    System.out.println("smooth");
    {
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se3Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 4 * 3 * 2);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      System.out.println("store");
      Put.of(UserHome.file("MH_04_difficult_poses_smooth.file"), smooth);
      System.out.println("differences");
      Tensor delta = LIE_DIFFERENCES.apply(smooth);
      System.out.println("store");
      Put.of(UserHome.file("MH_04_difficult_delta_smooth.file"), delta);
    }
  }
}

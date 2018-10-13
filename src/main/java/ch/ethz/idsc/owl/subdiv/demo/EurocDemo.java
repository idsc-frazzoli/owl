// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.QuaternionToRotationMatrix;
import ch.ethz.idsc.owl.math.group.LinearGroup;
import ch.ethz.idsc.owl.math.group.Se3Exponential;
import ch.ethz.idsc.owl.math.group.Se3Geodesic;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicDifferences;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sig.WindowFunctions;

enum EurocDemo {
  ;
  private static final GeodesicDifferences GEODESIC_DIFFERENCES = //
      new GeodesicDifferences(LinearGroup.INSTANCE, Se3Exponential.INSTANCE);

  public static void main(String[] args) throws IOException {
    System.out.println("here");
    Tensor tensor = ResourceData.of("/3rdparty/app/pose/euroc/MH_04_difficult.csv");
    // System.out.println(Dimensions.of(tensor));
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
    }
    System.out.println(Dimensions.of(poses));
    Put.of(UserHome.file("MH_04_difficult_poses.file"), poses);
    {
      Tensor delta = GEODESIC_DIFFERENCES.apply(poses);
      Put.of(UserHome.file("MH_04_difficult_delta.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se3Geodesic.INSTANCE, WindowFunctions.GAUSSIAN), 4);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(UserHome.file("MH_04_difficult_poses_smooth.file"), smooth);
      Tensor delta = GEODESIC_DIFFERENCES.apply(smooth);
      Put.of(UserHome.file("MH_04_difficult_delta_smooth.file"), delta);
    }
  }
}

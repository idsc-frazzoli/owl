// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.io.IOException;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.owl.math.group.LieDifferences;
import ch.ethz.idsc.owl.math.group.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.group.Se2Geodesic;
import ch.ethz.idsc.owl.math.group.Se2Group;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenterFilter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

enum EugocDemo {
  ;
  // public static void main(String[] args) {
  // control = Tensor.of(ResourceData.of("/dubilab/app/pose/" + resource + ".csv").stream().limit(5000).map(row -> row.extract(1, 4))));
  // }
  private static final LieDifferences LIE_DIFFERENCES = //
      new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);

  public static void main(String[] args) throws IOException {
    System.out.println("here");
    Tensor tensor = ResourceData.of("/dubilab/app/pose/2r/20180820T165637_2.csv");
    // System.out.println(Dimensions.of(tensor));
    Tensor poses = Tensors.empty();
    for (Tensor row : tensor) {
      Tensor xyt = row.extract(1, 4);
      poses.append(xyt);
    }
    System.out.println(Dimensions.of(poses));
    Put.of(UserHome.file("gokart_poses.file"), poses);
    {
      Tensor delta = LIE_DIFFERENCES.apply(poses);
      Put.of(UserHome.file("gokart_delta.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(UserHome.file("gokart_poses_gauss.file"), smooth);
      Tensor delta = LIE_DIFFERENCES.apply(smooth);
      Put.of(UserHome.file("gokart_delta_gauss.file"), delta);
    }
    {
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenterFilter.of(GeodesicCenter.of(Se2Geodesic.INSTANCE, SmoothingKernel.HAMMING), 6);
      Tensor smooth = tensorUnaryOperator.apply(poses);
      Put.of(UserHome.file("gokart_poses_hammi.file"), smooth);
      Tensor delta = LIE_DIFFERENCES.apply(smooth);
      Put.of(UserHome.file("gokart_delta_hammi.file"), delta);
    }
  }
}

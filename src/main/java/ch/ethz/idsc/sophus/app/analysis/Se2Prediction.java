// code by jph
package ch.ethz.idsc.sophus.app.analysis;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.bm.BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ enum Se2Prediction {
  ;
  public static void main(String[] args) throws IOException {
    GokartPoseData gokartPoseData = GokartPoseDataV2.INSTANCE;
    String name = gokartPoseData.list().get(1);
    Tensor pqr_t = Partition.of(gokartPoseData.getPose(name, 4 * 500), 4);
    Tensor Wp = Subdivide.of(-1.5, 0.0, 25);
    Tensor Wq = Subdivide.of(-1.0, 1.0, 25);
    Tensor err_xy = Array.zeros(Wp.length(), Wq.length());
    Tensor err_hd = Array.zeros(Wp.length(), Wq.length());
    Tensor lp3_xy = Array.zeros(Wp.length(), Wq.length(), 3);
    Tensor lp3_hd = Array.zeros(Wp.length(), Wq.length(), 3);
    // Tensor rotation = RotationMatrix.of(0);
    int i = 0;
    for (Tensor wp : Wp) {
      int j = 0;
      for (Tensor wq : Wq) {
        // Tensor wpq = Tensors.of(wp.subtract(wq), wp.add(wq));
        Tensor wpq = Tensors.of(wp, wq);
        Scalar wr = RealScalar.ONE.subtract(Total.ofVector(wpq));
        Tensor weights = wpq.append(wr);
        TensorUnaryOperator tensorUnaryOperator = BiinvariantMeans.of(Se2BiinvariantMeans.FILTER, weights);
        for (Tensor sequence : pqr_t) {
          Tensor pqr = sequence.extract(0, 3);
          Tensor t_prediction = tensorUnaryOperator.apply(pqr);
          Tensor t_measured = sequence.get(3);
          {
            Scalar err = Vector2Norm.between(t_prediction.extract(0, 2), t_measured.extract(0, 2));
            err_xy.set(err::add, i, j);
          }
          {
            Scalar diff = So2.MOD.apply(Abs.between(t_prediction.Get(2), t_measured.Get(2)));
            err_hd.set(diff::add, i, j);
          }
        }
        lp3_xy.set(wp, i, j, 0);
        lp3_xy.set(wq, i, j, 1);
        lp3_hd.set(wp, i, j, 0);
        lp3_hd.set(wq, i, j, 1);
        ++j;
      }
      ++i;
    }
    lp3_xy.set(err_xy, Tensor.ALL, Tensor.ALL, 2);
    lp3_hd.set(err_hd, Tensor.ALL, Tensor.ALL, 2);
    {
      Put.of(HomeDirectory.file("lp3_xy.mathematica"), lp3_xy);
      Put.of(HomeDirectory.file("lp3_hd.mathematica"), lp3_hd);
    }
    {
      Tensor image = ArrayPlot.of(err_xy, ColorDataGradients.CLASSIC);
      Export.of(HomeDirectory.file("err_xy.csv"), err_xy);
      Export.of(HomeDirectory.Pictures(Se2Prediction.class.getSimpleName() + "_xy.png"), image);
    }
    {
      Tensor image = ArrayPlot.of(err_hd, ColorDataGradients.CLASSIC);
      Export.of(HomeDirectory.file("err_hd.csv"), err_hd);
      Export.of(HomeDirectory.Pictures(Se2Prediction.class.getSimpleName() + "_hd.png"), image);
    }
  }
}

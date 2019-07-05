// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.IOException;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.lie.BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum Se2Prediction {
  ;
  public static void main(String[] args) throws IOException {
    String name = GokartPoseData.INSTANCE.list().get(0);
    // Tensor tensor = ;
    Tensor pqr_t = Partition.of(GokartPoseData.getPose(name, 4 * 500), 4);
    Tensor Wp = Subdivide.of(-4.0, 1.0, 15);
    Tensor Wq = Subdivide.of(-4.0, 1.0, 15);
    Tensor zeros = Array.zeros(Wp.length(), Wq.length());
    int i = 0;
    for (Tensor _wp : Wp) {
      int j = 0;
      for (Tensor _wq : Wq) {
        Scalar wp = _wp.Get();
        Scalar wq = _wq.Get();
        Scalar wr = RealScalar.ONE.subtract(wp).subtract(wq);
        Tensor weights = Tensors.of(wp, wq, wr);
        // System.out.println(weights.map(Round._4));
        TensorUnaryOperator tensorUnaryOperator = BiinvariantMeans.of(Se2BiinvariantMean.FILTER, weights);
        for (Tensor sequence : pqr_t) {
          Tensor pqr = sequence.extract(0, 3);
          Tensor t_prediction = tensorUnaryOperator.apply(pqr);
          Tensor t_measured = sequence.get(3);
          Scalar err = Norm._2.between(t_prediction.extract(0, 2), t_measured.extract(0, 2));
          zeros.set(err::add, i, j);
        }
        ++j;
      }
      ++i;
    }
    Tensor image = ArrayPlot.of(zeros, ColorDataGradients.CLASSIC);
    Export.of(HomeDirectory.Pictures(Se2Prediction.class.getSimpleName() + ".png"), image);
  }
}

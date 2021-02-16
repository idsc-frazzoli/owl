// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.io.IOException;

import ch.ethz.idsc.sophus.fit.RigidMotionFit;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class RigidMotionFitImage {
  private static Tensor shufflePoints(int n) {
    Distribution distribution = NormalDistribution.standard();
    Tensor random = RandomVariate.of(distribution, n, 2);
    Tensor mean = Mean.of(random).negate();
    return Tensor.of(random.stream().map(mean::add));
  }

  public static void main(String[] args) throws IOException {
    Tensor target = Array.zeros(1, 2);
    Tensor shuffl = shufflePoints(2);
    shuffl.stream().forEach(target::append);
    System.out.println(Pretty.of(target));
    Tensor points = target.copy();
    int RES = 128;
    Tensor param = Subdivide.of(-10, 10, RES);
    Clip clip = Clips.absolute(Pi.VALUE);
    Scalar[][] array = new Scalar[RES][RES];
    for (int x = 0; x < RES; ++x)
      for (int y = 0; y < RES; ++y) {
        points.set(Tensors.of(param.get(x), param.get(y)), 0);
        RigidMotionFit rigidMotionFit = RigidMotionFit.of(target, points);
        Tensor rotation = rigidMotionFit.rotation(); // 2 x 2
        Scalar angle = ArcTan.of(rotation.Get(0, 0), rotation.Get(1, 0));
        array[x][y] = clip.rescale(angle);
      }
    Tensor image = ArrayPlot.of(Tensors.matrix(array), ColorDataGradients.HUE);
    Export.of(HomeDirectory.Pictures("some.png"), image);
  }
}

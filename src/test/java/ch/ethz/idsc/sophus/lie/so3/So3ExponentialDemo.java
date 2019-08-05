// code by jph
package ch.ethz.idsc.sophus.lie.so3;

import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum So3ExponentialDemo {
  ;
  private static final int RES = 192;
  private static final Tensor RE = Subdivide.of(-4, +4, RES - 1);
  private static final Tensor IM = Subdivide.of(-4, +4, RES - 1);
  private static Scalar Z;

  private static Scalar function(int y, int x) {
    Tensor mat = So3Exponential.INSTANCE.exp(Tensors.of(RE.Get(x), IM.Get(y), Z));
    return mat.Get(0, 2);
  }

  public static void main(String[] args) throws Exception {
    try (AnimationWriter ani = AnimationWriter.of(HomeDirectory.Pictures("rodriquez.gif"), 100)) {
      for (Tensor _z : Subdivide.of(-4 * Math.PI, 4 * Math.PI, 40)) {
        System.out.println(_z);
        Z = _z.Get();
        Tensor matrix = Parallelize.matrix(So3ExponentialDemo::function, RES, RES);
        ani.append(ArrayPlot.of(matrix, ColorDataGradients.CLASSIC));
      }
      ani.close();
    }
  }
}

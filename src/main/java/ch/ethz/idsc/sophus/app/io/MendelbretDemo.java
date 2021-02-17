// code by jph
package ch.ethz.idsc.sophus.app.io;

import java.io.IOException;

import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.nrm.Vector2Norm;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Nest;

/* package */ class MendelbretDemo implements TensorUnaryOperator {
  private final Tensor c;

  public MendelbretDemo(Tensor c) {
    this.c = c;
  }

  @Override
  public Tensor apply(Tensor zn) {
    Se2GroupElement gzn = Se2Group.INSTANCE.element(zn);
    Tensor g2 = gzn.combine(zn);
    return Se2Group.INSTANCE.element(g2).combine(c);
  }

  public static void main(String[] args) throws IOException {
    Tensor _x = Subdivide.of(-4, 4, 512);
    Tensor _y = Subdivide.of(-4, 4, 512);
    Tensor image = Array.zeros(_x.length(), _y.length());
    for (int x = 0; x < _x.length(); ++x) {
      for (int y = 0; y < _y.length(); ++y) {
        Tensor c = Tensors.of(_x.Get(x), RealScalar.ZERO, _y.get(y));
        MendelbretDemo mendelbretDemo = new MendelbretDemo(c);
        Tensor tensor = Nest.of(mendelbretDemo, c, 1);
        // if (Scalars.lessThan(Norm._2.ofVector(tensor.extract(0, 2)), RealScalar.of(2)))
        // image.set(RealScalar.ONE, x, y);
        image.set(Min.of(Vector2Norm.of(tensor.extract(0, 2)), RealScalar.of(1)), x, y);
      }
    }
    Tensor tensor = ArrayPlot.of(image, ColorDataGradients.CLASSIC);
    Export.of(HomeDirectory.Pictures(MendelbretDemo.class.getSimpleName() + ".png"), tensor);
  }
}

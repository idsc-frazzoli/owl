// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import java.io.IOException;

import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.red.Max;

/* package */ class ClothoidFigure {
  private static final int RES = 192;
  private static final Tensor RE = Subdivide.of(-1, +1, RES - 1);
  private static final Tensor IM = Subdivide.of(+0.1, +2.1, RES - 1);
  // ---
  private final Scalar angle;

  public ClothoidFigure(Scalar angle) {
    this.angle = angle;
  }

  private Scalar function(int y, int x) {
    Tensor q = Tensors.of(RE.Get(x), IM.Get(y), angle);
    // return new Clothoid(q.map(Scalar::zero), q).new Curve().length();
    return Max.of( //
        new Clothoid(q.map(Scalar::zero), q).new Curvature().head().abs(), //
        new Clothoid(q.map(Scalar::zero), q).new Curvature().tail().abs()).reciprocal();
  }

  public static void main(String[] args) throws IOException {
    ClothoidFigure newtonDemo = new ClothoidFigure(RealScalar.of(2.6));
    Tensor matrix = Parallelize.matrix(newtonDemo::function, RES, RES);
    Tensor image = ArrayPlot.of(matrix, ColorDataGradients.SUNSET);
    Export.of(HomeDirectory.Pictures(ClothoidFigure.class.getSimpleName() + ".png"), image);
  }
}

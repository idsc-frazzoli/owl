// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum DubinsPathCurvatureImage {
  ;
  private static final int RES = 128 + 64;
  private static final Tensor RE = Subdivide.of(-2, +2, RES - 1);
  private static final Tensor IM = Subdivide.of(-2, +2, RES - 1);
  private static final Scalar ALPHA = RealScalar.of(-2.0);
  private static final Scalar RADIUS = RealScalar.of(0.5);

  private static Scalar function(int y, int x) {
    Tensor xya = Tensors.of(RE.Get(x), IM.Get(y), ALPHA);
    DubinsPath dubinsPath = FixedRadiusDubins.of(xya, RADIUS).allValid().min(DubinsPathComparator.length()).get();
    int ordinal = dubinsPath.type().ordinal();
    return dubinsPath.curvature().add(RealScalar.of(ordinal));
  }

  public static void main(String[] args) throws IOException {
    Tensor matrix = Tensors.matrix(DubinsPathCurvatureImage::function, RES, RES);
    File directory = HomeDirectory.Pictures(DubinsPathCurvatureImage.class.getSimpleName());
    directory.mkdir();
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor image = ArrayPlot.of(matrix, colorDataGradients);
      Export.of(new File(directory, colorDataGradients.name() + ".png"), image);
    }
  }
}

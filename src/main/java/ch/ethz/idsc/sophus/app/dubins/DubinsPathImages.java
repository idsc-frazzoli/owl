// code by jph
package ch.ethz.idsc.sophus.app.dubins;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.sophus.crv.dubins.DubinsPath;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparators;
import ch.ethz.idsc.sophus.crv.dubins.FixedRadiusDubins;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum DubinsPathImages {
  ;
  private static final int RES = 128 + 64;
  private static final Tensor RE = Subdivide.of(-2, +2, RES - 1);
  private static final Tensor IM = Subdivide.of(-2, +2, RES - 1);
  private static final Scalar ALPHA = RealScalar.of(-2.0);
  private static final Scalar RADIUS = RealScalar.of(0.5);

  static Scalar type(int y, int x) {
    Tensor xya = Tensors.of(RE.Get(x), IM.Get(y), ALPHA);
    DubinsPath dubinsPath = FixedRadiusDubins.of(xya, RADIUS).stream().min(DubinsPathComparators.LENGTH).get();
    int ordinal = dubinsPath.type().ordinal();
    return RealScalar.of(ordinal);
  }

  static Scalar curvature(int y, int x) {
    Tensor xya = Tensors.of(RE.Get(x), IM.Get(y), ALPHA);
    DubinsPath dubinsPath = FixedRadiusDubins.of(xya, RADIUS).stream().min(DubinsPathComparators.LENGTH).get();
    int ordinal = dubinsPath.type().ordinal();
    return dubinsPath.totalCurvature().add(RealScalar.of(ordinal));
  }

  public static void main(String[] args) throws IOException {
    Tensor matrix = Tensors.matrix(DubinsPathImages::type, RES, RES);
    File directory = HomeDirectory.Pictures(DubinsPathImages.class.getSimpleName());
    directory.mkdir();
    for (ColorDataLists colorDataLists : ColorDataLists.values()) {
      Tensor image = matrix.map(colorDataLists.strict());
      Export.of(new File(directory, colorDataLists.name() + ".png"), image);
    }
  }
}

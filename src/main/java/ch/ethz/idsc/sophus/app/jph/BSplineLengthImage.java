// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import ch.ethz.idsc.sophus.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum BSplineLengthImage {
  ;
  private static final int RES = 128 + 64;
  private static final Tensor RE = Subdivide.of(+0.0, +3.0, RES - 1);
  private static final Tensor IM = Subdivide.of(+0.0, +6.1, RES - 1);
  // private static final Scalar ALPHA = RealScalar.of(-2.0);
  // private static final Scalar RADIUS = RealScalar.of(.5);
  private static final Tensor P0 = Tensors.vector(-1.0, 1.0, +1.0);
  private static final Tensor P2 = Tensors.vector(+1.0, 0, +1.0);
  private static final CurveSubdivision CURVE_SUBDIVISION = new BSpline3CurveSubdivision(Se2CoveringGeodesic.INSTANCE);

  private static Scalar function(int y, int x) {
    Tensor p1 = Tensors.of(RealScalar.ZERO, RE.Get(y), IM.Get(x));
    Tensor control = Tensors.of(P0, p1, P2);
    Tensor tensor = Nest.of(CURVE_SUBDIVISION::string, control, 3);
    return Differences.of(tensor).stream().map(t -> t.extract(0, 2)).map(Norm._2::ofVector).reduce(Scalar::add).get();
    // Tensor xya = Tensors.of(RE.Get(x), IM.Get(y), ALPHA);
    // DubinsPath dubinsPath = FixedRadiusDubins.of(xya, RADIUS).allValid().min(DubinsPathComparator.length()).get();
    // int ordinal = dubinsPath.dubinsPathType().ordinal();
    // return dubinsPath.curvature().add(RealScalar.of(ordinal));
    // return RealScalar.of(0.0);
  }

  public static void main(String[] args) throws IOException {
    Tensor matrix = Tensors.matrix(BSplineLengthImage::function, RES, RES);
    File directory = HomeDirectory.Pictures(BSplineLengthImage.class.getSimpleName());
    directory.mkdir();
    for (ColorDataGradients colorDataGradients : //
    Arrays.asList(ColorDataGradients.CLASSIC)
    // ColorDataGradients.values()
    ) {
      Tensor image = ArrayPlot.of(matrix, colorDataGradients);
      Export.of(new File(directory, colorDataGradients.name() + ".png"), image);
    }
  }
}

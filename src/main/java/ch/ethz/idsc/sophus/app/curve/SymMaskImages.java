// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline5CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.curve.HormannSabinCurveSubdivision;
import ch.ethz.idsc.sophus.curve.SixPointCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SymMaskImages {
  BSPLINE1(BSpline1CurveSubdivision::new, 2, 0, 1), //
  BSPLINE2(BSpline2CurveSubdivision::new, 2, 0, 1), //
  BSPLINE3(BSpline3CurveSubdivision::new, 3, 1, 2), //
  BSPLINE4(BSpline4CurveSubdivision::of, 3, 1, 2), //
  BSPLINE4S2(BSpline4CurveSubdivision::split2, 3, 1, 2), //
  BSPLINE4S3(CurveSubdivisionHelper::split3, 3, 1, 2), //
  BSPLINE5(BSpline5CurveSubdivision::new, 4, 2, 3), //
  THREEPOINT(HormannSabinCurveSubdivision::of, 5, 1, 2), //
  FOURPOINT(FourPointCurveSubdivision::new, 6, 0, 3), //
  SIXPOINT(SixPointCurveSubdivision::new, 6, 0, 5), //
  ;
  // ---
  private final CurveSubdivision curveSubdivision;
  private final int support;
  private final BufferedImage image0;
  private final BufferedImage image1;

  private SymMaskImages(Function<GeodesicInterface, CurveSubdivision> function, int support, int index0, int index1) {
    curveSubdivision = function.apply(SymGeodesic.INSTANCE);
    this.support = support;
    image0 = bufferedImage(index0);
    image1 = bufferedImage(index1);
  }

  private BufferedImage bufferedImage(int index) {
    Tensor vector = Tensor.of(IntStream.range(0, support).mapToObj(SymScalar::leaf));
    Tensor tensor = curveSubdivision.string(vector);
    return new SymLinkImage((SymScalar) tensor.Get(index)).bufferedImage();
  }

  public BufferedImage image0() {
    return image0;
  }

  public BufferedImage image1() {
    return image1;
  }

  public static Optional<SymMaskImages> get(String string) {
    try {
      return Optional.ofNullable(SymMaskImages.valueOf(string));
    } catch (Exception exception) {
      // ---
    }
    return Optional.empty();
  }
}

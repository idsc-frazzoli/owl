// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.util.EnumSet;
import java.util.Set;

import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ enum CurveSubdivisionHelper {
  ;
  private static final Set<CurveSubdivisionSchemes> DUAL = EnumSet.of( //
      CurveSubdivisionSchemes.BSPLINE2, //
      CurveSubdivisionSchemes.BSPLINE4, //
      CurveSubdivisionSchemes.BSPLINE4S2, //
      CurveSubdivisionSchemes.BSPLINE4S3, //
      CurveSubdivisionSchemes.LR2, //
      CurveSubdivisionSchemes.LR4, //
      CurveSubdivisionSchemes.LR6);

  static boolean isDual(CurveSubdivisionSchemes curveSubdivisionSchemes) {
    return DUAL.contains(curveSubdivisionSchemes);
  }

  // ---
  static Scalar MAGIC_C = RationalScalar.of(1, 6);

  static CurveSubdivision split3(GeodesicInterface geodesicInterface) {
    return BSpline4CurveSubdivision.split3(geodesicInterface, MAGIC_C);
  }

  static CurveSubdivision fps(GeodesicInterface geodesicInterface) {
    return new FourPointCurveSubdivision(geodesicInterface, MAGIC_C.multiply(RationalScalar.of(125, 1000)));
  }
}

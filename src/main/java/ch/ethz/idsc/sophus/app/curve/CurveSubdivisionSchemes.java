// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.sophus.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline5CurveSubdivision;
import ch.ethz.idsc.sophus.curve.BSpline6CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.sophus.curve.DualC2FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.curve.FarSixPointCurveSubdivision;
import ch.ethz.idsc.sophus.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.curve.HormannSabinCurveSubdivision;
import ch.ethz.idsc.sophus.curve.SixPointCurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

/* package */ enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new, 1), //
  BSPLINE2(BSpline2CurveSubdivision::new, 2), //
  BSPLINE3(BSpline3CurveSubdivision::new, 3),
  /** Dyn/Sharon 2014 that uses 2 binary averages */
  BSPLINE4(BSpline4CurveSubdivision::of, 4),
  /** Alternative to Dyn/Sharon 2014 that also uses 2 binary averages */
  BSPLINE4S2(BSpline4CurveSubdivision::split2, 4), //
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4S3(CurveSubdivisionHelper::split3, 4), //
  BSPLINE5(BSpline5CurveSubdivision::new, 5), //
  BSPLINE6(BSpline6CurveSubdivision::of, 6), //
  DOBSEB(i -> DodgsonSabinCurveSubdivision.INSTANCE), //
  THREEPOINT(HormannSabinCurveSubdivision::of), //
  FOURPOINT(FourPointCurveSubdivision::new), //
  C2CUBIC(DualC2FourPointCurveSubdivision::cubic), //
  C2TIGHT(DualC2FourPointCurveSubdivision::tightest), //
  SIXPOINT(SixPointCurveSubdivision::new), //
  SIXFAR(FarSixPointCurveSubdivision::new), //
  ;
  public final Function<GeodesicInterface, CurveSubdivision> function;
  public final Optional<Integer> degree;

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function) {
    this(function, -1);
  }

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function, int degree) {
    this.function = function;
    this.degree = 0 <= degree //
        ? Optional.of(degree)
        : Optional.empty();
  }
}

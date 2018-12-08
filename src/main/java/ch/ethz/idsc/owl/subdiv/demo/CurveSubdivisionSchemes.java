// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.util.Optional;
import java.util.function.Function;

import ch.ethz.idsc.owl.math.GeodesicInterface;
import ch.ethz.idsc.owl.subdiv.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline5CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline6CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.DualC2FourPointCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.FarSixPointCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.HormannSabinCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.SixPointCurveSubdivision;

public enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new, 1), //
  BSPLINE2(BSpline2CurveSubdivision::new, 2), //
  BSPLINE3(BSpline3CurveSubdivision::new, 3), //
  BSPLINE4(BSpline4CurveSubdivision::of, 4), //
  BSPLINE4S2(BSpline4CurveSubdivision::split2, 4), //
  BSPLINE4S3(StaticHelper::split3, 4), //
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

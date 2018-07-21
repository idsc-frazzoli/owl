// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import java.util.function.Function;

enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new), //
  BSPLINE2(BSpline2CurveSubdivision::new), //
  BSPLINE3(BSpline3CurveSubdivision::new), //
  BSPLINE4(BSpline4CurveSubdivision::of), //
  THREEPOINT(ThreePointCurveSubdivision::hormannSabin), //
  FOURPOINT(FourPointCurveSubdivision::new), //
  ;
  public final Function<GeodesicInterface, CurveSubdivision> function;

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function) {
    this.function = function;
  }
}

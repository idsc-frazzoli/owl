// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.util.function.Function;

import ch.ethz.idsc.owl.subdiv.curve.BSpline1CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline2CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline3CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.CurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.FarSixPointCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.FourPointCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicInterface;
import ch.ethz.idsc.owl.subdiv.curve.HormannSabinCurveSubdivision;
import ch.ethz.idsc.owl.subdiv.curve.SixPointCurveSubdivision;

enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new), //
  BSPLINE2(BSpline2CurveSubdivision::new), //
  BSPLINE3(BSpline3CurveSubdivision::new), //
  BSPLINE4(BSpline4CurveSubdivision::of), //
  DOBSEB(i -> DodgsonSabinCurveSubdivision.INSTANCE), //
  THREEPOINT(HormannSabinCurveSubdivision::of), //
  FOURPOINT(FourPointCurveSubdivision::new), //
  SIXPOINT(SixPointCurveSubdivision::new), //
  SIXFAR(FarSixPointCurveSubdivision::new), //
  ;
  public final Function<GeodesicInterface, CurveSubdivision> function;

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function) {
    this.function = function;
  }
}

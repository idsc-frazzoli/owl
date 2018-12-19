// code by jph
package ch.ethz.idsc.sophus.app.demo;

import java.util.function.Function;

import ch.ethz.idsc.sophus.curve.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.math.GeodesicInterface;

/* package */ enum BSpline4CurveSubdivisions {
  DYN_SHARON(BSpline4CurveSubdivision::of), //
  ALTERNATIVE(BSpline4CurveSubdivision::split2), //
  HAKENBERG(BSpline4CurveSubdivision::split3), //
  ;
  public final Function<GeodesicInterface, CurveSubdivision> function;

  private BSpline4CurveSubdivisions(Function<GeodesicInterface, CurveSubdivision> function) {
    this.function = function;
  }
}

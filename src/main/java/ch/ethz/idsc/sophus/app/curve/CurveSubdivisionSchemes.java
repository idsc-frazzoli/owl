// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.util.function.Function;

import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline5CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline6CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.DualC2FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.EightPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.FarSixPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HormannSabinCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeld3CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.SixPointCurveSubdivision;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new), //
  BSPLINE2(BSpline2CurveSubdivision::new), //
  BSPLINE3(BSpline3CurveSubdivision::new), //
  BSPLINE3LR(LaneRiesenfeld3CurveSubdivision::new), //
  /** Dyn/Sharon 2014 that uses 2 binary averages */
  BSPLINE4(BSpline4CurveSubdivision::of), //
  /** Alternative to Dyn/Sharon 2014 that also uses 2 binary averages */
  BSPLINE4S2(BSpline4CurveSubdivision::split2),
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4S3(CurveSubdivisionHelper::split3), //
  BSPLINE5(BSpline5CurveSubdivision::new), //
  BSPLINE6(BSpline6CurveSubdivision::of), //
  LR1(gi -> new LaneRiesenfeldCurveSubdivision(gi, 1)), //
  LR2(gi -> new LaneRiesenfeldCurveSubdivision(gi, 2)), //
  LR3(gi -> new LaneRiesenfeldCurveSubdivision(gi, 3)), //
  LR4(gi -> new LaneRiesenfeldCurveSubdivision(gi, 4)), //
  LR5(gi -> new LaneRiesenfeldCurveSubdivision(gi, 5)), //
  LR6(gi -> new LaneRiesenfeldCurveSubdivision(gi, 6)), //
  DODGSON_SABIN(i -> DodgsonSabinCurveSubdivision.INSTANCE), //
  THREEPOINT(HormannSabinCurveSubdivision::of), //
  FOURPOINT(FourPointCurveSubdivision::new), //
  FOURPOINT2(CurveSubdivisionHelper::fps), //
  C2CUBIC(DualC2FourPointCurveSubdivision::cubic), //
  C2TIGHT(DualC2FourPointCurveSubdivision::tightest), //
  SIXPOINT(SixPointCurveSubdivision::new), //
  SIXFAR(FarSixPointCurveSubdivision::new), //
  EIGHTPOINT(EightPointCurveSubdivision::new), //
  ;
  public final Function<GeodesicInterface, CurveSubdivision> function;

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function) {
    this.function = function;
  }

  public boolean isStringSupported() {
    try {
      function.apply(RnGeodesic.INSTANCE).string(Tensors.empty());
      return true;
    } catch (Exception exception) {
      // ---
    }
    return false;
  }
}

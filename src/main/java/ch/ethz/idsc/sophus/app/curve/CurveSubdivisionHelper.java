// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import ch.ethz.idsc.sophus.crv.subdiv.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ enum CurveSubdivisionHelper {
  ;
  public static final List<CurveSubdivisionSchemes> LANE_RIESENFELD = Arrays.asList( //
      CurveSubdivisionSchemes.LR1, //
      CurveSubdivisionSchemes.LR2, //
      CurveSubdivisionSchemes.LR3, //
      CurveSubdivisionSchemes.LR4, //
      CurveSubdivisionSchemes.LR5 //
  );
  // ---
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

  static CurveSubdivision split3(SplitInterface splitInterface) {
    return BSpline4CurveSubdivision.split3(splitInterface, MAGIC_C);
  }

  static CurveSubdivision fps(SplitInterface splitInterface) {
    return new FourPointCurveSubdivision(splitInterface, MAGIC_C.multiply(RationalScalar.of(125, 1000)));
  }
}

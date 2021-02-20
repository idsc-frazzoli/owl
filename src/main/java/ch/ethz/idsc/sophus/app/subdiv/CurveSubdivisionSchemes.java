// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.sophus.math.ParametricCurve;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.sophus.ref.d1.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline5CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.BSpline6CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.DualC2FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.EightPointCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.FarSixPointCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.HormannSabinCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.LaneRiesenfeld3CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.MSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.MSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.ref.d1.SixPointCurveSubdivision;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.itp.BinaryAverage;

/* package */ enum CurveSubdivisionSchemes {
  BSPLINE1 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return new BSpline1CurveSubdivision(midpointInterface);
    }
  },
  BSPLINE2 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      ParametricCurve parametricCurve = geodesicDisplay.geodesicInterface();
      return new BSpline2CurveSubdivision(parametricCurve);
    }
  },
  BSPLINE3 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new BSpline3CurveSubdivision(splitInterface);
    }
  },
  BSPLINE3LR {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeld3CurveSubdivision.of(midpointInterface);
    }
  },
  BSPLINE3M {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      return new MSpline3CurveSubdivision(biinvariantMean);
    }
  },
  /** Dyn/Sharon 2014 that uses 2 binary averages */
  BSPLINE4_S2LO {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return BSpline4CurveSubdivision.split2lo(splitInterface);
    }
  },
  /** Alternative to Dyn/Sharon 2014 that also uses 2 binary averages */
  BSPLINE4_S2HI {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return BSpline4CurveSubdivision.split2hi(splitInterface);
    }
  },
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4_S3 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return CurveSubdivisionHelper.of(splitInterface);
    }
  },
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4M {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      return MSpline4CurveSubdivision.of(biinvariantMean);
    }
  },
  BSPLINE5 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new BSpline5CurveSubdivision(splitInterface);
    }
  },
  BSPLINE6 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      return BSpline6CurveSubdivision.of(geodesicInterface);
    }
  },
  LR1 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 1);
    }
  },
  LR2 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 2);
    }
  },
  LR3 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 3);
    }
  },
  LR4 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 4);
    }
  },
  LR5 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 5);
    }
  },
  LR6 {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 6);
    }
  },
  DODGSON_SABIN {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      return DodgsonSabinCurveSubdivision.INSTANCE;
    }
  },
  THREEPOINT {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      BinaryAverage binaryAverage = geodesicDisplay.geodesicInterface();
      return HormannSabinCurveSubdivision.of(binaryAverage);
    }
  },
  FOURPOINT {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return CurveSubdivisionHelper.fps(splitInterface);
    }
  },
  C2CUBIC {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      return DualC2FourPointCurveSubdivision.cubic(geodesicInterface);
    }
  },
  C2TIGHT {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
      return DualC2FourPointCurveSubdivision.tightest(geodesicInterface);
    }
  },
  SIXPOINT {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new SixPointCurveSubdivision(splitInterface);
    }
  },
  SIXFAR {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new FarSixPointCurveSubdivision(splitInterface);
    }
  },
  EIGHTPOINT {
    @Override
    public CurveSubdivision of(ManifoldDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new EightPointCurveSubdivision(splitInterface);
    }
  };

  public abstract CurveSubdivision of(ManifoldDisplay geodesicDisplay);

  public boolean isStringSupported() {
    try {
      of(R2Display.INSTANCE).string(Tensors.empty());
      return true;
    } catch (Exception exception) {
      // ---
    }
    return false;
  }
}

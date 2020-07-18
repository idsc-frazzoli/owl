// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
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
import ch.ethz.idsc.sophus.crv.subdiv.MSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.MSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.SixPointCurveSubdivision;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.MidpointInterface;
import ch.ethz.idsc.sophus.math.ParametricCurve;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.BinaryAverage;

/* package */ enum CurveSubdivisionSchemes {
  BSPLINE1 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return new BSpline1CurveSubdivision(midpointInterface);
    }
  },
  BSPLINE2 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      ParametricCurve parametricCurve = geodesicDisplay.geodesicInterface();
      return new BSpline2CurveSubdivision(parametricCurve);
    }
  },
  BSPLINE3 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new BSpline3CurveSubdivision(splitInterface);
    }
  },
  BSPLINE3LR {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeld3CurveSubdivision.of(midpointInterface);
    }
  },
  BSPLINE3M {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      return new MSpline3CurveSubdivision(biinvariantMean);
    }
  },
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return CurveSubdivisionHelper.of(splitInterface);
    }
  },
  /** Dyn/Sharon 2014 that uses 2 binary averages */
  BSPLINE4DS {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return BSpline4CurveSubdivision.dynSharon(splitInterface);
    }
  },
  /** Alternative to Dyn/Sharon 2014 that also uses 2 binary averages */
  BSPLINE4S2 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return BSpline4CurveSubdivision.split2(splitInterface);
    }
  },
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4M {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      return MSpline4CurveSubdivision.of(biinvariantMean);
    }
  },
  BSPLINE5 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new BSpline5CurveSubdivision(splitInterface);
    }
  },
  BSPLINE6 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      return BSpline6CurveSubdivision.of(geodesicInterface);
    }
  },
  LR1 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 1);
    }
  },
  LR2 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 2);
    }
  },
  LR3 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 3);
    }
  },
  LR4 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 4);
    }
  },
  LR5 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 5);
    }
  },
  LR6 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      MidpointInterface midpointInterface = geodesicDisplay.geodesicInterface();
      return LaneRiesenfeldCurveSubdivision.of(midpointInterface, 6);
    }
  },
  DODGSON_SABIN {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      return DodgsonSabinCurveSubdivision.INSTANCE;
    }
  },
  THREEPOINT {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      BinaryAverage binaryAverage = geodesicDisplay.geodesicInterface();
      return HormannSabinCurveSubdivision.of(binaryAverage);
    }
  },
  FOURPOINT {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new FourPointCurveSubdivision(splitInterface);
    }
  },
  FOURPOINT2 {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return CurveSubdivisionHelper.fps(splitInterface);
    }
  },
  C2CUBIC {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      return DualC2FourPointCurveSubdivision.cubic(geodesicInterface);
    }
  },
  C2TIGHT {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      return DualC2FourPointCurveSubdivision.tightest(geodesicInterface);
    }
  },
  SIXPOINT {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new SixPointCurveSubdivision(splitInterface);
    }
  },
  SIXFAR {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new FarSixPointCurveSubdivision(splitInterface);
    }
  },
  EIGHTPOINT {
    @Override
    public CurveSubdivision of(GeodesicDisplay geodesicDisplay) {
      SplitInterface splitInterface = geodesicDisplay.geodesicInterface();
      return new EightPointCurveSubdivision(splitInterface);
    }
  };

  public abstract CurveSubdivision of(GeodesicDisplay geodesicDisplay);

  public boolean isStringSupported() {
    try {
      of(R2GeodesicDisplay.INSTANCE).string(Tensors.empty());
      return true;
    } catch (Exception exception) {
      // ---
    }
    return false;
  }
}

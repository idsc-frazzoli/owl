// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.util.DemoLauncher;
import ch.ethz.idsc.sophus.app.avg.BezierFunctionSplitsDemo;
import ch.ethz.idsc.sophus.app.avg.ExtrapolationSplitsDemo;
import ch.ethz.idsc.sophus.app.avg.GeodesicCenterSplitsDemo;
import ch.ethz.idsc.sophus.app.curve.BezierFunctionDemo;
import ch.ethz.idsc.sophus.app.curve.BiinvariantMeanSubdivisionDemo;
import ch.ethz.idsc.sophus.app.curve.GeodesicBSplineFunctionDemo;
import ch.ethz.idsc.sophus.app.curve.SplitCurveSubdivisionDemo;
import ch.ethz.idsc.sophus.app.misc.ClothoidDemo;
import ch.ethz.idsc.sophus.app.misc.DubinsPathDemo;
import ch.ethz.idsc.sophus.app.misc.GeodesicDemo;
import ch.ethz.idsc.sophus.app.ob.LieGroupFiltersDatasetDemo;

public enum GeodesicDemoLauncher {
  ;
  public static void main(String[] args) {
    DemoLauncher.build(Arrays.asList( //
        LieGroupFiltersDatasetDemo.class, //
        GeodesicCenterSplitsDemo.class, //
        BezierFunctionSplitsDemo.class, //
        GeodesicBSplineFunctionDemo.class, //
        SplitCurveSubdivisionDemo.class, //
        BiinvariantMeanSubdivisionDemo.class, //
        GeodesicDemo.class, //
        DubinsPathDemo.class, //
        ClothoidDemo.class, //
        ExtrapolationSplitsDemo.class, //
        BezierFunctionDemo.class //
    ));
  }
}

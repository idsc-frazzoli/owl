// code by jph
package ch.ethz.idsc.sophus.app;

import java.util.Arrays;

import ch.ethz.idsc.owl.bot.util.DemoLauncher;
import ch.ethz.idsc.sophus.app.avg.BezierFunctionSplitsDemo;
import ch.ethz.idsc.sophus.app.avg.ExtrapolationSplitsDemo;
import ch.ethz.idsc.sophus.app.avg.GeodesicCenterSplitsDemo;
import ch.ethz.idsc.sophus.app.curve.BezierFunctionDemo;
import ch.ethz.idsc.sophus.app.curve.GeodesicBSplineFunctionDemo;
import ch.ethz.idsc.sophus.app.curve.GeodesicDemo;
import ch.ethz.idsc.sophus.app.filter.GeodesicFiltersDatasetDemo;
import ch.ethz.idsc.sophus.app.subdiv.CurveSubdivisionDemo;

public enum GeodesicDemoLauncher {
  ;
  public static void main(String[] args) {
    DemoLauncher.build(Arrays.asList( //
        GeodesicFiltersDatasetDemo.class, //
        GeodesicCenterSplitsDemo.class, //
        BezierFunctionSplitsDemo.class, //
        GeodesicBSplineFunctionDemo.class, //
        CurveSubdivisionDemo.class, //
        GeodesicDemo.class, //
        ExtrapolationSplitsDemo.class, //
        BezierFunctionDemo.class //
    ));
  }
}

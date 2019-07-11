// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.awt.Rectangle;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.jfree.chart.JFreeChart;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedIntervalGeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedIntervalGeodesicCenterFilter;
import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedRadiusGeodesicCenter;
import ch.ethz.idsc.sophus.flt.ga.NonuniformFixedRadiusGeodesicCenterFilter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.win.SmoothingKernel;
import ch.ethz.idsc.subare.util.plot.ListPlot;
import ch.ethz.idsc.subare.util.plot.VisualRow;
import ch.ethz.idsc.subare.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;

// TODO OB rewrite for quantities
/* package */ enum NonuniformityEvaluation {
  ;
  public static final GokartPoseData GOKART_POSE_DATA = GokartPoseDataV2.INSTANCE;
  public final static NonuniformFixedRadiusGeodesicCenter nonuniformFixedRadiusGeodesicCenter = NonuniformFixedRadiusGeodesicCenter.of(Se2Geodesic.INSTANCE);
  public final static NonuniformFixedIntervalGeodesicCenter nonuniformFixedIntervalGeodesicCenter = NonuniformFixedIntervalGeodesicCenter
      .of(Se2Geodesic.INSTANCE, SmoothingKernel.GAUSSIAN);
  private static final GeodesicErrorEvaluation GEODESIC_ERROR_EVALUATION = //
      new GeodesicErrorEvaluation(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);

  public static Tensor process(NavigableMap<Scalar, Tensor> fullMap, int nonuniformitySteps, int radius) {
    Tensor errors = Tensors.empty();
    for (int index = 0; index < nonuniformitySteps; ++index) {
      Scalar alpha = RationalScalar.of(index, nonuniformitySteps);
      NavigableMap<Scalar, Tensor> nonUniformMap = nonUniform(fullMap, alpha);
      Tensor refinedRadius = Tensor
          .of(NonuniformFixedRadiusGeodesicCenterFilter.of(nonuniformFixedRadiusGeodesicCenter, radius).apply(nonUniformMap).values().stream());
      Tensor refinedInterval = Tensor.of(NonuniformFixedIntervalGeodesicCenterFilter.of(nonuniformFixedIntervalGeodesicCenter, RationalScalar.of(radius, 20))
          .apply(nonUniformMap).values().stream());
      Tensor original = Tensor.of(nonUniformMap.values().stream());
      Tensor errorRad = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedRadius, original).divide(RealScalar.of(original.length()));
      Tensor errorInt = GEODESIC_ERROR_EVALUATION.evaluate0ErrorSeperated(refinedInterval, original).divide(RealScalar.of(original.length()));
      errors.append(Tensors.of(alpha, errorRad.Get(0), errorRad.Get(1), errorInt.Get(0), errorInt.Get(1)));
    }
    return errors;
  }

  public static NavigableMap<Scalar, Tensor> nonUniform(NavigableMap<Scalar, Tensor> fullMap, Scalar nonuniformity) {
    NavigableMap<Scalar, Tensor> nonUniformMap = new TreeMap<>();
    for (int index = 0; index < fullMap.size(); ++index) {
      if (Math.random() > nonuniformity.number().doubleValue()) {
        Scalar key = RealScalar.of(index).divide(RealScalar.of(20));
        nonUniformMap.put(key, fullMap.get(key));
      }
    }
    return nonUniformMap;
  }

  // signal = 0: xy Error
  // signal = 1: a-Error
  public static void plot(Tensor errors, Tensor radii, int signal) {
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Translational Error");
    visualSet.setAxesLabelX("Ratio of missing data [-]");
    String fileNameSVG;
    if (signal == 0) {
      visualSet.setAxesLabelY("Error per control point$[m]");
      fileNameSVG = "Nonuniformity_XY.svg";
    } else {
      visualSet.setAxesLabelY("Error per control point $[rad]");
      fileNameSVG = "Nonuniformity_A.svg";
    }
    Tensor xAxis = Tensor.of(errors.get(0).stream().map(xya -> xya.Get(0)));
    int index = 0;
    for (Tensor radius : errors) {
      VisualRow visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(radius.stream().map(x -> x.Get(1 + signal))));
      visualRow.setLabel("radius " + radii.get(index));
      visualRow = visualSet.add( //
          xAxis, //
          Tensor.of(radius.stream().map(x -> x.Get(3 + signal))));
      visualRow.setLabel("interval" + radii.get(index));
      index++;
    }
    try {
      // ---
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      SVGGraphics2D svg = new SVGGraphics2D(600, 400);
      Rectangle rectangle = new Rectangle(0, 0, 600, 400);
      jFreeChart.draw(svg, rectangle);
      File fileSVG = HomeDirectory.Pictures(fileNameSVG);
      SVGUtils.writeToSVG(fileSVG, svg.getSVGElement());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    List<String> listData = GOKART_POSE_DATA.list();
    NavigableMap<Scalar, Tensor> fullMap = new TreeMap<>();
    Iterator<String> iterator = listData.iterator();
    Tensor control = GOKART_POSE_DATA.getPose(iterator.next(), 1000);
    for (int index = 0; index < control.length(); ++index) {
      fullMap.put(RealScalar.of(index).divide(RealScalar.of(20)), control.get(index));
    }
    Tensor radii = Tensors.vector(1, 10);
    Tensor errors = Tensors.empty();
    for (Tensor radius : radii) {
      Scalar rad = (Scalar) radius;
      errors.append(process(fullMap, 5, rad.number().intValue()));
    }
    plot(errors, radii, 0);
    plot(errors, radii, 1);
  }
}

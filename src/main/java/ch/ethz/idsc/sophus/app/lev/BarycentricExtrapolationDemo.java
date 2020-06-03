// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;

/* package */ class BarycentricExtrapolationDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private final SpinnerLabel<LogWeighting> spinnerProjectedCoordinates = new SpinnerLabel<>();

  public BarycentricExtrapolationDemo() {
    super(true, GeodesicDisplays.SE2C_R2);
    {
      spinnerProjectedCoordinates.setList(LogWeightings.list());
      spinnerProjectedCoordinates.setIndex(0);
      spinnerProjectedCoordinates.addToComponentReduced(timerFrame.jToolBar, new Dimension(150, 28), "projected coordinate");
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor control = getGeodesicControlPoints();
    int length = control.length();
    Tensor domain = Range.of(-control.length(), 0).map(Tensors::of).unmodifiable();
    graphics.setColor(Color.GRAY);
    graphics.setStroke(STROKE);
    for (int index = 0; index < length; ++index) {
      Line2D line2d = geometricLayer.toLine2D( //
          domain.get(index).append(RealScalar.ZERO), //
          geodesicDisplay.toPoint(control.get(index)));
      graphics.draw(line2d);
    }
    graphics.setStroke(new BasicStroke());
    if (1 < length) {
      Tensor samples = Subdivide.of(-length, 0, 127).map(Tensors::of);
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      WeightingInterface weightingInterface = spinnerProjectedCoordinates.getValue().from(RnManifold.INSTANCE, null);
      Tensor curve = Tensor.of(samples.stream() //
          .map(point -> weightingInterface.weights(domain, point)) //
          .map(weights -> biinvariantMean.mean(control, weights)));
      new PathRender(Color.BLUE, 1.5f) //
          .setCurve(curve, false) //
          .render(geometricLayer, graphics);
    }
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BarycentricExtrapolationDemo().setVisible(1200, 600);
  }
}

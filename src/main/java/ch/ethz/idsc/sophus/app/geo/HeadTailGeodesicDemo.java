// code by jph
package ch.ethz.idsc.sophus.app.geo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class HeadTailGeodesicDemo extends ControlPointsDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  public HeadTailGeodesicDemo() {
    super(false, GeodesicDisplays.ALL);
    // ---
    setGeodesicDisplay(S2Display.INSTANCE);
    spinnerRefine.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    Tensor p = controlPoints.get(0);
    Tensor q = controlPoints.get(1);
    ScalarTensorFunction scalarTensorFunction = geodesicDisplay.geodesicInterface().curve(p, q);
    graphics.setStroke(new BasicStroke(1.5f));
    Tensor shape = geodesicDisplay.shape();
    Tensor domain = Subdivide.of(0, 1, spinnerRefine.getValue());
    Tensor points = domain.map(scalarTensorFunction);
    Tensor xys = Tensor.of(points.stream().map(geodesicDisplay::toPoint));
    graphics.setColor(new Color(128, 255, 0));
    graphics.draw(geometricLayer.toPath2D(xys, false));
    try {
      Scalar pseudoDistance = geodesicDisplay.parametricDistance().distance(p, q);
      {
        graphics.setColor(Color.DARK_GRAY);
        graphics.drawString("" + pseudoDistance.map(Round._4), 10, 20);
      }
    } catch (Exception exception) {
      // ---
    }
    // ---
    graphics.setColor(Color.LIGHT_GRAY);
    for (Tensor _t : domain) {
      Tensor pq = scalarTensorFunction.apply((Scalar) _t);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(pq));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.BLUE);
    for (Tensor _t : Subdivide.of(0, 1, 1)) {
      Tensor pq = scalarTensorFunction.apply((Scalar) _t);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(pq));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    graphics.setStroke(new BasicStroke());
  }

  public static void main(String[] args) {
    new HeadTailGeodesicDemo().setVisible(1000, 600);
  }
}

// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.sn.SnExponential;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ class S2LogDemo extends ControlPointsDemo {
  public S2LogDemo() {
    super(true, GeodesicDisplays.S2_ONLY);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public GeodesicDisplay getGeodesicDisplay() {
        return geodesicDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Tensor GEODESIC_DOMAIN = Subdivide.of(0.0, 1.0, 11);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    S2GeodesicDisplay geodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    Tensor points = getGeodesicControlPoints();
    if (0 < points.length()) {
      Tensor p = points.get(0);
      Tensor vs = Tensor.of(points.stream().skip(1).map(new SnExponential(p)::log));
      {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
        Tensor ts = S2GeodesicDisplay.tangentSpace(p);
        graphics.setStroke(new BasicStroke(1.5f));
        graphics.setColor(Color.GRAY);
        for (Tensor v : vs) // render tangents in tangent space
          graphics.draw(geometricLayer.toLine2D(ts.dot(v)));
        graphics.setColor(new Color(192, 192, 192, 64));
        graphics.fill(geometricLayer.toPath2D(CirclePoints.of(41), true));
        geometricLayer.popMatrix();
      }
      // ---
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      for (Tensor v : vs) { // render tangents as geodesic on sphere
        Tensor q = new SnExponential(p).exp(v); // point on sphere
        ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
        graphics.setStroke(STROKE);
        Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
        graphics.setColor(new Color(192, 192, 192));
        graphics.draw(geometricLayer.toPath2D(ms));
      }
    }
    graphics.setStroke(new BasicStroke());
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new S2LogDemo().setVisible(1000, 800);
  }
}

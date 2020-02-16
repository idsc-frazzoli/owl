// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class BarycentricLeversDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final JToggleButton jToggleMean = new JToggleButton("mean");

  public BarycentricLeversDemo() {
    super(true, GeodesicDisplays.SE2C_SPD2_S2_Rn);
    {
      timerFrame.jToolBar.add(jToggleMean);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}}"));
    // setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 0, 0}}"));
    setGeodesicDisplay(R2GeodesicDisplay.INSTANCE);
    setMidpointIndicated(false);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor controlPointsAll = getGeodesicControlPoints();
    renderControlPoints(geometricLayer, graphics);
    if (1 + geodesicDisplay.dimensions() < controlPointsAll.length()) {
      Tensor origin = controlPointsAll.get(0);
      Tensor controlPoints = controlPointsAll.extract(1, controlPointsAll.length());
      BarycentricCoordinate barycentricCoordinate = geodesicDisplay.barycentricCoordinate();
      Tensor weights = barycentricCoordinate.weights(controlPoints, origin);
      {
        int index = 0;
        for (Tensor q : controlPoints) {
          ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
          graphics.setStroke(new BasicStroke(1.5f));
          {
            Tensor domain = Subdivide.of(weights.Get(index).zero(), weights.Get(index), 15);
            Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream() //
                .map(geodesicDisplay::toPoint));
            graphics.setColor(Color.BLUE);
            graphics.draw(geometricLayer.toPath2D(ms));
          }
          graphics.setStroke(STROKE);
          {
            Tensor domain = Subdivide.of(weights.Get(index), RealScalar.ONE, 15);
            Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream() //
                .map(geodesicDisplay::toPoint));
            graphics.setColor(new Color(128, 128, 128, 128));
            graphics.draw(geometricLayer.toPath2D(ms));
          }
          ++index;
        }
        graphics.setStroke(new BasicStroke());
      }
      {
        graphics.setFont(ArrayPlotRender.FONT);
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int fheight = fontMetrics.getAscent();
        int index = 0;
        Tensor shape = geodesicDisplay.shape();
        for (Tensor q : controlPoints) {
          Tensor matrix = geodesicDisplay.matrixLift(q);
          geometricLayer.pushMatrix(matrix);
          Path2D path2d = geometricLayer.toPath2D(shape, true);
          // graphics.setColor(Color.RED);
          // graphics.draw(path2d.getBounds());
          graphics.setColor(Color.BLACK);
          Rectangle rectangle = path2d.getBounds();
          graphics.drawString(" " + weights.Get(index).map(Round._3), //
              rectangle.x + rectangle.width, //
              rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2);
          geometricLayer.popMatrix();
          ++index;
        }
      }
      if (jToggleMean.isSelected()) {
        Tensor mean = geodesicDisplay.biinvariantMean().mean(controlPoints,
            ConstantArray.of(RationalScalar.of(1, controlPoints.length()), controlPoints.length()));
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
        Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape(), true);
        graphics.setColor(new Color(128, 255, 128, 128));
        graphics.fill(path2d);
        graphics.setColor(new Color(128, 255, 128, 255));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
  }

  public static void main(String[] args) {
    new BarycentricLeversDemo().setVisible(1200, 600);
  }
}

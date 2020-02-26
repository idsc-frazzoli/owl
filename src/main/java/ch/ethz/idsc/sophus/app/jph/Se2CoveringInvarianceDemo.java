// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LieGroupOps;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class Se2CoveringInvarianceDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JTextField jTextField = new JTextField();

  public Se2CoveringInvarianceDemo() {
    super(true, GeodesicDisplays.SE2C_ONLY);
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jTextField.setText("{1, 1, 1}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}}"));
    // setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 0, 0}}"));
    setMidpointIndicated(false);
  }

  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final PointsRender ORIGIN_RENDER_0 = //
      new PointsRender(new Color(64, 255, 64, 128), new Color(64, 255, 64, 255));

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Tensor controlPointsAll = getGeodesicControlPoints();
    new Flush(geodesicDisplay, controlPointsAll).render(geometricLayer, graphics);
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    if (0 < controlPointsAll.length())
      try {
        geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(10, 0)));
        Tensor allR = lieGroupOps.allR(controlPointsAll, Tensors.fromString(jTextField.getText()));
        Tensor result = lieGroupOps.allL(allR, lieGroup.element(allR.get(0)).inverse().toCoordinate());
        new Flush(geodesicDisplay, result).render(geometricLayer, graphics);
        geometricLayer.popMatrix();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public static class Flush implements RenderInterface {
    private final GeodesicDisplay geodesicDisplay;
    private final Tensor controlPointsAll;

    public Flush(GeodesicDisplay geodesicDisplay, Tensor controlPointsAll) {
      this.geodesicDisplay = geodesicDisplay;
      this.controlPointsAll = controlPointsAll;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      if (0 < controlPointsAll.length()) {
        Tensor origin = controlPointsAll.get(0);
        if (1 + geodesicDisplay.dimensions() < controlPointsAll.length()) {
          Tensor controlPoints = controlPointsAll.extract(1, controlPointsAll.length());
          BarycentricCoordinate barycentricCoordinate = geodesicDisplay.barycentricCoordinate();
          Tensor weights = barycentricCoordinate.weights(controlPoints, origin);
          {
            for (Tensor q : controlPoints) {
              ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(origin, q);
              graphics.setStroke(STROKE);
              {
                Tensor domain = Subdivide.of(0, 1, 21);
                Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream() //
                    .map(geodesicDisplay::toPoint));
                graphics.setColor(new Color(128, 128, 128, 128));
                graphics.draw(geometricLayer.toPath2D(ms));
              }
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
              graphics.setColor(Color.BLACK);
              Rectangle rectangle = path2d.getBounds();
              graphics.drawString(" " + weights.Get(index).map(Round._3), //
                  rectangle.x + rectangle.width, //
                  rectangle.y + rectangle.height + (-rectangle.height + fheight) / 2);
              geometricLayer.popMatrix();
              ++index;
            }
          }
        }
        ORIGIN_RENDER_0.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), Tensors.of(origin)).render(geometricLayer, graphics);
        POINTS_RENDER_0.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), controlPointsAll.extract(1, controlPointsAll.length()))
            .render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new Se2CoveringInvarianceDemo().setVisible(1200, 600);
  }
}

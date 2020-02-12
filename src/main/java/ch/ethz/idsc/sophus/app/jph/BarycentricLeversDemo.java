// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.rn.RnVectorNorm;
import ch.ethz.idsc.sophus.math.win.InverseDistanceCoordinates;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class BarycentricLeversDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  // private final SpinnerLabel<R2Barycentrics> spinnerBarycentric = new SpinnerLabel<>();
  private final JToggleButton jToggleEntire = new JToggleButton("entire");
  private final JToggleButton jToggleButton = new JToggleButton("heatmap");

  public BarycentricLeversDemo() {
    super(true, GeodesicDisplays.SE2C_R2);
    // {
    // spinnerBarycentric.setArray(R2Barycentrics.values());
    // spinnerBarycentric.setIndex(0);
    // spinnerBarycentric.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "barycentric");
    // }
    timerFrame.jToolBar.addSeparator();
    {
      timerFrame.jToolBar.add(jToggleEntire);
    }
    {
      jToggleButton.setSelected(true);
      timerFrame.jToolBar.add(jToggleButton);
    }
    setControlPointsSe2(Tensors.fromString("{{-1, -2, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    if (2 < controlPoints.length())
      try {
        GraphicsUtil.setQualityHigh(graphics);
        {
          graphics.setColor(Color.LIGHT_GRAY);
          graphics.setStroke(STROKE);
          Path2D path2d = geometricLayer.toPath2D(controlPoints);
          path2d.closePath();
          graphics.draw(path2d);
          graphics.setStroke(new BasicStroke(1));
        }
        TensorUnaryOperator tensorUnaryOperator = //
            InverseDistanceCoordinates.of(RnVectorNorm.INSTANCE, controlPoints);
        Tensor origin = controlPoints.get(0).map(Scalar::zero);
        Tensor weights = tensorUnaryOperator.apply(origin);
        graphics.setColor(Color.DARK_GRAY);
        for (Tensor p : weights.pmul(controlPoints))
          graphics.draw(geometricLayer.toLine2D(p));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public static void main(String[] args) {
    new BarycentricLeversDemo().setVisible(1200, 600);
  }
}

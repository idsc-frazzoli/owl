// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class BarycentricLeversDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  // private final SpinnerLabel<R2Barycentrics> spinnerBarycentric = new SpinnerLabel<>();
  private final JToggleButton jToggleEntire = new JToggleButton("entire");
  private final JToggleButton jToggleButton = new JToggleButton("heatmap");

  public BarycentricLeversDemo() {
    super(true, GeodesicDisplays.SE2C_SPD2_S2_R2);
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
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsAll = getGeodesicControlPoints();
    if (2 < controlPointsAll.length()) {
      Tensor origin = controlPointsAll.get(0);
      Tensor controlPoints = controlPointsAll.extract(1, controlPointsAll.length());
      try {
        // {
        // graphics.setColor(Color.LIGHT_GRAY);
        // graphics.setStroke(STROKE);
        // Path2D path2d = geometricLayer.toPath2D(controlPoints);
        // path2d.closePath();
        // graphics.draw(path2d);
        // graphics.setStroke(new BasicStroke(1));
        // }
        TensorNorm tensorNorm = q -> geodesicDisplay.parametricDistance(origin, q);
        // TensorUnaryOperator tensorUnaryOperator = //
        // RnInverseDistanceCoordinates.INSTANCE.idc(tensorNorm, controlPoints);
        // Tensor weights = tensorUnaryOperator.apply(origin);
        // graphics.setColor(Color.DARK_GRAY);
        // for (Tensor p : weights.pmul(controlPoints))
        // graphics.draw(geometricLayer.toLine2D(geodesicDisplay.toPoint(p)));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    new BarycentricLeversDemo().setVisible(1200, 600);
  }
}

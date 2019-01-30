// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.filter.GeodesicCenterFilter;
import ch.ethz.idsc.sophus.group.LieDifferences;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class GeodesicCenterFilterDemo extends DatasetKernelDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic();
  private final JToggleButton jToggleWait = new JToggleButton("wait");

  GeodesicCenterFilterDemo() {
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final SmoothingKernel smoothingKernel = spinnerFilter.getValue();
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.3));
    GraphicsUtil.setQualityHigh(graphics);
    final int radius = spinnerRadius.getValue();
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.smoothingKernel(smoothingKernel, radius).bufferedImage(), 0, 0, null);
    // ---
    if (jToggleWait.isSelected())
      return;
    // ---
    Tensor control = control();
    if (jToggleData.isSelected()) {
      if (jToggleLine.isSelected())
        pathRenderCurve.setCurve(control, false).render(geometricLayer, graphics);
      for (Tensor point : control) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    TensorUnaryOperator geodesicCenterFilter = //
        GeodesicCenterFilter.of(GeodesicCenter.of(geodesicDisplay.geodesicInterface(), smoothingKernel), radius);
    final Tensor refined = geodesicCenterFilter.apply(control);
    if (jToggleDiff.isSelected()) {
      final int baseline_y = 200;
      {
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, baseline_y, 300, baseline_y);
      }
      {
        int piy = 30;
        graphics.drawString("Filter: " + spinnerFilter.getValue(), 0, piy);
        Scalar width = Quantity.of(0.05 * (spinnerRadius.getValue() * 2 + 1), "s");
        graphics.drawString("Window: " + Round._3.apply(width), 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(0));
        graphics.drawString("Tangent velocity", 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(1));
        graphics.drawString("Side slip", 0, piy += 15);
        graphics.setColor(COLOR_DATA_INDEXED.getColor(2));
        graphics.drawString("Rotational rate", 0, piy += 15);
      }
      {
        LieDifferences lieDifferences = //
            new LieDifferences(geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential());
        Tensor speeds = lieDifferences.apply(refined);
        if (0 < speeds.length()) {
          int dimensions = speeds.get(0).length();
          graphics.setStroke(new BasicStroke(1.3f));
          for (int index = 0; index < dimensions; ++index) {
            graphics.setColor(COLOR_DATA_INDEXED.getColor(index));
            Path2D path2d = plotFunc(graphics, speeds.get(Tensor.ALL, index).multiply(RealScalar.of(400)), baseline_y);
            graphics.draw(path2d);
          }
        }
      }
    }
    graphics.setStroke(new BasicStroke(1f));
    if (jToggleLine.isSelected())
      pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
    for (Tensor point : refined) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(COLOR_SHAPE);
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new GeodesicCenterFilterDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

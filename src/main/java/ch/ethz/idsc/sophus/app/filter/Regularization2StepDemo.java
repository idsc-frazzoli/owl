// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.Regularization2Step;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.N;

// TODO JPH/OB eliminate redundancies with GeodesicCenterFilterDemo
/* package */ class Regularization2StepDemo extends DatasetFilterDemo {
  private final JSlider jSlider = new JSlider(0, 1000, 600);

  Regularization2StepDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(.3));
    GraphicsUtil.setQualityHigh(graphics);
    // if (jToggleSymi.isSelected())
    // graphics.drawImage(SymLinkImages.smoothingKernel(smoothingKernel, radius).bufferedImage(), 0, 0, null);
    // ---
    Tensor control2 = control();
    {
      if (jToggleLine.isSelected())
        pathRenderCurve.setCurve(control2, false).render(geometricLayer, graphics);
      for (Tensor point : control2) {
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
    Scalar factor = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
    TensorUnaryOperator tensorUnaryOperator = Regularization2Step.string(geodesicDisplay.geodesicInterface(), N.DOUBLE.apply(factor));
    final Tensor refined = tensorUnaryOperator.apply(control2);
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
    AbstractDemo abstractDemo = new Regularization2StepDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

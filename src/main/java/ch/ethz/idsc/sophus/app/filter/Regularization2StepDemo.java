// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.filter.Regularization2Step;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class Regularization2StepDemo extends DatasetFilterDemo {
  /** regularization parameter in the interval [0, 1] */
  private final JSlider jSlider = new JSlider(0, 1000, 600);

  Regularization2StepDemo() {
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    updateData();
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Scalar factor = factor();
    // ---
    if (jToggleSymi.isSelected())
      graphics.drawImage(SymLinkImages.regularization2Step(factor).bufferedImage(), 0, 0, null);
    // ---
    return Regularization2Step.string(geodesicDisplay.geodesicInterface(), N.DOUBLE.apply(factor)).apply(control());
  }

  @Override // from DatasetFilterDemo
  protected String plotLabel() {
    return "Regularization2Step " + factor();
  }

  private Scalar factor() {
    return RationalScalar.of(jSlider.getValue(), jSlider.getMaximum());
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new Regularization2StepDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

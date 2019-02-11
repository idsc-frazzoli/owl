// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymLink;
import ch.ethz.idsc.sophus.sym.SymLinkBuilder;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** visualization of geodesic average along geodesics */
/* package */ class BezierFunctionAverageDemo extends ControlPointsDemo {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 13);
  // ---
  private final JSlider jSlider = new JSlider(0, 1000, 500);

  BezierFunctionAverageDemo() {
    super(true, false, GeodesicDisplays.ALL);
    // ---
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
    // ---
    setControl(Tensors.fromString("{{0,0,0},{2,2,1},{5,0,2}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor control = control();
    Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    int n = control.length();
    Scalar parameter = n <= 1 //
        ? RealScalar.ZERO
        : RationalScalar.of(n, n - 1);
    parameter = parameter.multiply(RationalScalar.of(jSlider.getValue(), 1000));
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(parameter);
    graphics.drawImage(new SymLinkImage(symScalar, FONT).bufferedImage(), 0, 0, null);
    // ---
    SymLink symLink = SymLinkBuilder.of(control, symScalar);
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    // ---
    GeodesicAverageRender.of(geodesicDisplay, symLink).render(geometricLayer, graphics);
    // ---
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor xya = symLink.getPosition(geodesicDisplay.geodesicInterface());
    renderPoints(geometricLayer, graphics, Tensors.of(xya));
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BezierFunctionAverageDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

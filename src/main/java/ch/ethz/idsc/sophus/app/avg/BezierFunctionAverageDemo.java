// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.Dimension;
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
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLink;
import ch.ethz.idsc.sophus.symlink.SymLinkBuilder;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.N;

/** visualization of geodesic average along geodesics */
/* package */ class BezierFunctionAverageDemo extends ControlPointsDemo {
  private Scalar parameter = RationalScalar.HALF;

  BezierFunctionAverageDemo() {
    super(true, GeodesicDisplays.ALL);
    // ---
    JSlider jSlider = new JSlider(0, 1000, 500);
    jSlider.setPreferredSize(new Dimension(500, 28));
    jSlider.addChangeListener(changeEvent -> parameter = RationalScalar.of(jSlider.getValue(), 1000));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor control = control();
    Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(N.DOUBLE.apply(parameter));
    graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
    SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
    SymLink symLink = symLinkBuilder.build(symScalar);
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

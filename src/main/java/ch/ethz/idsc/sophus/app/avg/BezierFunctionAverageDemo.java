// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLink;
import ch.ethz.idsc.sophus.symlink.SymLinkBuilder;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
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
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = control();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(SymGeodesic.INSTANCE, vector);
    SymScalar symScalar = (SymScalar) scalarTensorFunction.apply(N.DOUBLE.apply(parameter));
    graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
    SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
    SymLink symLink = symLinkBuilder.build(symScalar);
    GeodesicInterface geodesicInterface = geodesicInterface();
    GeodesicAverageRender.of(geodesicDisplay, symLink).render(geometricLayer, graphics);
    Tensor xya = symLink.getPosition(geodesicInterface);
    renderControlPoints(geometricLayer, graphics);
    if (Objects.nonNull(xya)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(xya));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      path2d.closePath();
      int rgb = 128 + 32;
      final Color color = new Color(rgb, rgb, rgb, 255);
      graphics.setColor(color);
      graphics.setStroke(new BasicStroke(1f));
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BezierFunctionAverageDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

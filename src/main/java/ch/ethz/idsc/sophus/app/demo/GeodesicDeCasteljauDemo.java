// code by jph
package ch.ethz.idsc.sophus.app.demo;

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
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
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

/* package */ class GeodesicDeCasteljauDemo extends ControlPointsDemo {
  private Scalar parameter = RationalScalar.HALF;

  GeodesicDeCasteljauDemo() {
    timerFrame.jToolBar.add(jButton);
    // ---
    JSlider jSlider = new JSlider(0, 1000, 500);
    jSlider.setPreferredSize(new Dimension(500, 28));
    jSlider.addChangeListener(changeEvent -> parameter = RationalScalar.of(jSlider.getValue(), 1000));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    final Tensor control = controlSe2();
    final Tensor xya;
    {
      Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
      ScalarTensorFunction deCasteljau = BezierFunction.of(SymGeodesic.INSTANCE, vector);
      SymScalar symScalar = (SymScalar) deCasteljau.apply(N.DOUBLE.apply(parameter));
      graphics.drawImage(new SymLinkImage(symScalar).bufferedImage(), 0, 0, null);
      SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
      SymLink symLink = symLinkBuilder.build(symScalar);
      new Se2SplitRender(symLink).render(geometricLayer, graphics);
      xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
    }
    {
      for (Tensor point : control) {
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(new Color(255, 128, 128, 255));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    if (Objects.nonNull(xya)) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xya));
      Path2D path2d = geometricLayer.toPath2D(ARROWHEAD_HI);
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
    AbstractDemo abstractDemo = new GeodesicDeCasteljauDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

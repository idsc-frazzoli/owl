// code by jph
package ch.ethz.idsc.sophus.app.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.DeCasteljau;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.symlink.SymGeoRender;
import ch.ethz.idsc.sophus.symlink.SymGeodesic;
import ch.ethz.idsc.sophus.symlink.SymLink;
import ch.ethz.idsc.sophus.symlink.SymLinkBuilder;
import ch.ethz.idsc.sophus.symlink.SymLinkImage;
import ch.ethz.idsc.sophus.symlink.SymScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class GeodesicDeCasteljauDemo extends ControlPointsDemo {
  private final SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();
  private final SpinnerLabel<BSpline4CurveSubdivisions> spinnerBSpline4 = new SpinnerLabel<>();
  // ---
  private Scalar MAGIC_C = RationalScalar.of(1, 2);

  GeodesicDeCasteljauDemo() {
    timerFrame.jToolBar.add(jButton);
    // ---
    {
      JSlider jSlider = new JSlider(0, 1000, 500);
      jSlider.setPreferredSize(new Dimension(500, 28));
      jSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent changeEvent) {
          MAGIC_C = RationalScalar.of(jSlider.getValue(), 1000);
          System.out.println(MAGIC_C);
        }
      });
      timerFrame.jToolBar.add(jSlider);
    }
    // ---
    {
      spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
      spinnerKernel.setValue(SmoothingKernel.GAUSSIAN);
      spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "filter");
    }
    {
      spinnerBSpline4.setList(Arrays.asList(BSpline4CurveSubdivisions.values()));
      spinnerBSpline4.setValue(BSpline4CurveSubdivisions.DYN_SHARON);
      spinnerBSpline4.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "bspline4");
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    Tensor control = controlSe2();
    Tensor xya = null;
    final Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
    if (control.length() == 4) {
      // BezierCurve bezierCurve = new BezierCurve(Se2CoveringGeodesic.INSTANCE);
      DeCasteljau deCasteljau = new DeCasteljau(SymGeodesic.INSTANCE, vector);
      SymScalar symScalar = (SymScalar) deCasteljau.apply(N.DOUBLE.apply(MAGIC_C));
      {
        SymLinkImage symLinkImage = new SymLinkImage(symScalar);
        graphics.drawImage(symLinkImage.bufferedImage(), 0, 200, null);
      }
      // ScalarTensorFunction scalarTensorFunction = bezierCurve.evaluation(vector);
      // TensorUnaryOperator tensorUnaryOperator = BSpline4CurveSubdivision.split3(SymGeodesic.INSTANCE, RationalScalar.HALF)::cyclic;
      // Scalar tensor = (Scalar) scalarTensorFunction.apply(RationalScalar.of(1, 3));
      SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
      SymLink symLink = symLinkBuilder.build(symScalar);
      new SymGeoRender(symLink).render(geometricLayer, graphics);
      xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
    } else //
    if (control.length() == 3 && false) {
      CurveSubdivision curveSubdivision = spinnerBSpline4.getValue().function.apply(SymGeodesic.INSTANCE);
      TensorUnaryOperator tensorUnaryOperator = curveSubdivision::cyclic;
      Tensor tensor = tensorUnaryOperator.apply(vector).Get(2);
      SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
      SymLink symLink = symLinkBuilder.build((SymScalar) tensor);
      new SymGeoRender(symLink).render(geometricLayer, graphics);
      xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
    } else //
    if (control.length() % 2 == 1) {
      SmoothingKernel smoothingKernel = spinnerKernel.getValue();
      {
        int radius = (control.length() - 1) / 2;
        // SymLinkImage symLinkImage = SymGenerate.window(smoothingKernel, radius);
        // graphics.drawImage(symLinkImage.bufferedImage(), 0, 200, null);
      }
      // ---
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenter.of(SymGeodesic.INSTANCE, smoothingKernel);
      Tensor tensor = tensorUnaryOperator.apply(vector);
      SymLinkBuilder symLinkBuilder = new SymLinkBuilder(control);
      SymLink symLink = symLinkBuilder.build((SymScalar) tensor);
      new SymGeoRender(symLink).render(geometricLayer, graphics);
      xya = symLink.getPosition(Se2CoveringGeodesic.INSTANCE);
    }
    { // SE2
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
      // graphics.setColor(color);
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

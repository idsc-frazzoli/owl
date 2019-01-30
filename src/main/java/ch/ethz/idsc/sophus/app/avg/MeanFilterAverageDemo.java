// code by jph
package ch.ethz.idsc.sophus.app.avg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.filter.GeodesicCenter;
import ch.ethz.idsc.sophus.math.CenterWindowSampler;
import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.sophus.sym.SymGeodesic;
import ch.ethz.idsc.sophus.sym.SymLink;
import ch.ethz.idsc.sophus.sym.SymLinkBuilder;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.sophus.sym.SymScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class MeanFilterAverageDemo extends ControlPointsDemo {
  private final SpinnerLabel<SmoothingKernel> spinnerKernel = new SpinnerLabel<>();

  MeanFilterAverageDemo() {
    super(true, false, GeodesicDisplays.ALL);
    // ---
    spinnerKernel.setList(Arrays.asList(SmoothingKernel.values()));
    spinnerKernel.setValue(SmoothingKernel.GAUSSIAN);
    spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "filter");
    // ---
    setControl(Tensors.fromString("{{0,0,0},{2,2,1},{5,0,2}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    final Tensor control = control();
    Tensor xya = null;
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (control.length() % 2 == 1) {
      SmoothingKernel smoothingKernel = spinnerKernel.getValue();
      int radius = (control.length() - 1) / 2;
      graphics.drawImage(SymLinkImages.smoothingKernel(smoothingKernel, radius).bufferedImage(), 0, 0, null);
      // ---
      CenterWindowSampler centerWindowSampler = new CenterWindowSampler(smoothingKernel);
      TensorUnaryOperator tensorUnaryOperator = //
          GeodesicCenter.of(SymGeodesic.INSTANCE, centerWindowSampler);
      Tensor vector = Tensor.of(IntStream.range(0, control.length()).mapToObj(SymScalar::leaf));
      Tensor tensor = tensorUnaryOperator.apply(vector);
      SymLink symLink = SymLinkBuilder.of(control, (SymScalar) tensor);
      GeodesicAverageRender.of(geodesicDisplay, symLink).render(geometricLayer, graphics);
      xya = symLink.getPosition(geodesicDisplay.geodesicInterface());
    }
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
    AbstractDemo abstractDemo = new MeanFilterAverageDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}

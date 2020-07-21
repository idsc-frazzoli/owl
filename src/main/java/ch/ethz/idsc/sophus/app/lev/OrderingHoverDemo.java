// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.bdn.LegendImage;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class OrderingHoverDemo extends AbstractHoverDemo {
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());

  public OrderingHoverDemo() {
    {
      spinnerLength.addSpinnerListener(v -> recompute());
      spinnerLength.setList(Arrays.asList(50, 75, 100, 200, 300, 400, 500, 800));
      spinnerLength.setValue(200);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    {
      spinnerColorData.setValueSafe(ColorDataGradients.THERMOMETER);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color");
    }
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
    setLogWeighting(LogWeightings.DISTANCES);
    addSpinnerListener(v -> recompute());
    recompute();
  }

  private TensorUnaryOperator tensorUnaryOperator;

  @Override // from LogWeightingDemo
  protected void recompute() {
    System.out.println("recompute");
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    Tensor sequence = RandomVariate.of(distribution, spinnerLength.getValue(), 3);
    setControlPointsSe2(sequence);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
    tensorUnaryOperator = //
        logWeighting().operator(biinvariant(), vectorLogManifold, variogram(), getGeodesicControlPoints());
  }

  @Override // from AbstractHoverDemo
  void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender) {
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = leversRender.getSequence();
    Tensor origin = leversRender.getOrigin();
    Tensor weights = tensorUnaryOperator.apply(origin);
    // ---
    Integer[] integers = Ordering.INCREASING.of(weights);
    ColorDataGradient colorDataGradientF = spinnerColorData.getValue().deriveWithOpacity(RationalScalar.HALF);
    ColorDataGradient colorDataGradientD = spinnerColorData.getValue();
    Tensor shape = geodesicDisplay.shape();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor point = sequence.get(integers[index]);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      Scalar ratio = RationalScalar.of(index, integers.length);
      graphics.setColor(ColorFormat.toColor(colorDataGradientF.apply(ratio)));
      graphics.fill(path2d);
      graphics.setColor(ColorFormat.toColor(colorDataGradientD.apply(ratio)));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    {
      BufferedImage bufferedImage = LegendImage.of(colorDataGradientD, 300, "far", "near");
      Tensor pixel2model = GeodesicArrayPlot.pixel2model( //
          Tensors.of(Pi.VALUE.add(RealScalar.of(0.3)), Pi.VALUE.negate()), //
          Tensors.of(Pi.TWO, Pi.TWO), //
          new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    }
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
      Path2D path2d = geometricLayer.toPath2D(shape, true);
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      graphics.setColor(Color.BLACK);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    leversRender.renderIndexX();
  }

  public static void main(String[] args) {
    new OrderingHoverDemo().setVisible(1200, 600);
  }
}

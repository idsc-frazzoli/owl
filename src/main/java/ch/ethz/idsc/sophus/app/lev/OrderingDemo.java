// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.bdn.ArrayPlotRender;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class OrderingDemo extends AbstractPlaceDemo {
  private final SpinnerLabel<Integer> spinnerLength = new SpinnerLabel<>();
  private final JButton jButton = new JButton("shuffle");
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());

  public OrderingDemo() {
    super(GeodesicDisplays.MANIFOLDS, LogWeightings.list());
    {
      spinnerLength.addSpinnerListener(v -> shuffleSnap());
      spinnerLength.setList(Arrays.asList(50, 75, 100, 200, 300, 400, 500));
      spinnerLength.setValue(200);
      spinnerLength.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "number of points");
    }
    jButton.addActionListener(l -> shuffleSnap());
    timerFrame.jToolBar.add(jButton);
    {
      spinnerColorData.setValueSafe(ColorDataGradients.THERMOMETER);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color");
    }
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
    setLogWeighting(LogWeightings.DISTANCES);
    shuffleSnap();
  }

  private void shuffleSnap() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
    Tensor sequence = RandomVariate.of(distribution, spinnerLength.getValue(), 3);
    sequence.set(Scalar::zero, 0, Tensor.ALL);
    setControlPointsSe2(sequence);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPointsAll = getGeodesicControlPoints();
    if (0 < controlPointsAll.length()) {
      Tensor sequence = controlPointsAll.extract(1, controlPointsAll.length());
      Tensor origin = controlPointsAll.get(0);
      VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
      TensorUnaryOperator tensorUnaryOperator = //
          logWeighting().operator(biinvariant(), vectorLogManifold, variogram(), sequence);
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
        int height = 200;
        BufferedImage legend = //
            ImageFormat.of(Subdivide.decreasing(Clips.unit(), height - 1).map(Tensors::of).map(colorDataGradientD));
        graphics.drawImage(legend, //
            0, //
            0, //
            10, //
            height, null);
        ArrayPlotRender.renderLegendLabel(graphics, "far", "near", 10, height);
      }
      RenderQuality.setQuality(graphics);
      {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(origin));
        Path2D path2d = geometricLayer.toPath2D(shape, true);
        graphics.setColor(Color.DARK_GRAY);
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay, //
          Tensors.empty(), origin, //
          geometricLayer, graphics);
      leversRender.renderIndexX();
    }
  }

  public static void main(String[] args) {
    new OrderingDemo().setVisible(1200, 600);
  }
}

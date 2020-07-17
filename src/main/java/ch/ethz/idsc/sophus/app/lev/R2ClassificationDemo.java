// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class R2ClassificationDemo extends LogWeightingDemo {
  static final Random RANDOM = new Random();
  // ---
  private final SpinnerLabel<ColorDataLists> spinnerColor = SpinnerLabel.of(ColorDataLists.values());
  private final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");
  private final SpinnerLabel<Labels> spinnerLabels = SpinnerLabel.of(Labels.values());
  private Tensor vector;

  public R2ClassificationDemo() {
    super(false, GeodesicDisplays.R2_ONLY, LogWeightings.list());
    setMidpointIndicated(false);
    {
      spinnerLogWeighting.addSpinnerListener(logWeighting -> {
        if (logWeighting.equals(LogWeightings.DISTANCES)) {
          spinnerLabels.setValue(Labels.ARG_MIN);
        }
        if (logWeighting.equals(LogWeightings.WEIGHTING)) {
          spinnerLabels.setValue(Labels.ARG_MAX);
        }
        if (logWeighting.equals(LogWeightings.COORDINATE)) {
          spinnerLabels.setValue(Labels.ARG_MAX);
        }
      });
      spinnerLogWeighting.addSpinnerListener(v -> recompute());
    }
    {
      spinnerColor.addSpinnerListener(v -> recompute());
      spinnerColor.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "color data lists");
    }
    {
      spinnerCount.setList(Arrays.asList(5, 10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(15);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      spinnerRes.setArray(25, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(100);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
      spinnerRes.addSpinnerListener(v -> recompute());
    }
    {
      jButtonShuffle.addActionListener(e -> shuffle(spinnerCount.getValue()));
      timerFrame.jToolBar.add(jButtonShuffle);
    }
    spinnerLabels.addSpinnerListener(v -> recompute());
    setLogWeighting(LogWeightings.DISTANCES);
    shuffle(spinnerCount.getValue());
    spinnerLabels.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "label");
    // ---
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (isPositioningOngoing())
            recompute();
          break;
        }
      }
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
  }

  private BufferedImage bufferedImage;

  void shuffle(int n) {
    Tensor xyzs = Join.of(1, RandomVariate.of(UniformDistribution.of(-5, 5), RANDOM, n, 2), RandomVariate.of(UniformDistribution.of(-2, 2), RANDOM, n, 1));
    setControlPointsSe2(xyzs);
    // assignment of random labels to points
    vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), RANDOM, n);
    recompute();
  }

  @Override
  public void recompute() {
    System.out.println("recomp");
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Classification labelInterface = spinnerLabels.getValue().apply(vector);
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    TensorUnaryOperator operator = operator(getGeodesicControlPoints());
    TensorScalarFunction tensorScalarFunction = //
        point -> RealScalar.of(labelInterface.result(operator.apply(point)).getLabel());
    Scalar[][] scalars = geodesicArrayPlot.array(spinnerRes.getValue(), tensorScalarFunction);
    ColorDataLists colorDataLists = spinnerColor.getValue();
    Tensor image = Tensors.matrix(scalars).map(colorDataLists.cyclic().deriveWithAlpha(128 + 64));
    bufferedImage = ImageFormat.of(image);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (Objects.nonNull(bufferedImage)) {
      Tensor pixel2model = geodesicDisplay.geodesicArrayPlot().pixel2model(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    }
    // ---
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.4));
    int index = 0;
    for (Tensor point : getGeodesicControlPoints()) {
      int label = vector.Get(index).number().intValue();
      ColorDataLists colorDataLists = spinnerColor.getValue();
      ColorDataIndexed colorDataIndexedT = colorDataLists.cyclic();
      ColorDataIndexed colorDataIndexedO = colorDataIndexedT.deriveWithAlpha(128);
      PointsRender pointsRender = new PointsRender( //
          colorDataIndexedT.getColor(label), //
          colorDataIndexedO.getColor(label));
      pointsRender.show(geodesicDisplay::matrixLift, shape, Tensors.of(point)).render(geometricLayer, graphics);
      ++index;
    }
  }

  public static void main(String[] args) {
    new R2ClassificationDemo().setVisible(1200, 900);
  }
}

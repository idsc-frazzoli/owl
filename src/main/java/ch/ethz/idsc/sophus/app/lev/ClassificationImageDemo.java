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
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/* package */ class ClassificationImageDemo extends LogWeightingDemo {
  static final Random RANDOM = new Random();
  // ---
  private final SpinnerLabel<ColorDataLists> spinnerColor = SpinnerLabel.of(ColorDataLists.values());
  private final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");
  private final JToggleButton jToggleButton = new JToggleButton("track");
  private final SpinnerLabel<Labels> spinnerLabels = SpinnerLabel.of(Labels.values());
  private final SpinnerLabel<ClassificationImage> spinnerImage = SpinnerLabel.of(ClassificationImage.values());
  // ---
  protected Tensor vector;

  public ClassificationImageDemo() {
    super(false, GeodesicDisplays.R2_H2_S2_RP2, LogWeightings.list());
    setMidpointIndicated(false);
    addSpinnerListener(v -> shuffle(spinnerCount.getValue()));
    {
      spinnerLogWeighting.addSpinnerListener(logWeighting -> {
        if (logWeighting.equals(LogWeightings.DISTANCES))
          spinnerLabels.setValue(Labels.ARG_MIN);
        else //
        if ( //
        logWeighting.equals(LogWeightings.WEIGHTING) || //
        logWeighting.equals(LogWeightings.COORDINATE))
          spinnerLabels.setValue(Labels.ARG_MAX);
      });
      spinnerLogWeighting.addSpinnerListener(v -> recompute());
    }
    {
      spinnerColor.addSpinnerListener(v -> recompute());
      spinnerColor.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "color data lists");
    }
    {
      spinnerCount.setList(Arrays.asList(5, 10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(15);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "landmark count");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      spinnerRes.setArray(25, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(50);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
      spinnerRes.addSpinnerListener(v -> recompute());
    }
    {
      jButtonShuffle.addActionListener(e -> shuffle(spinnerCount.getValue()));
      timerFrame.jToolBar.add(jButtonShuffle);
    }
    {
      jToggleButton.setSelected(true);
      timerFrame.jToolBar.add(jToggleButton);
    }
    spinnerLabels.addSpinnerListener(v -> recompute());
    setLogWeighting(LogWeightings.DISTANCES);
    shuffle(spinnerCount.getValue());
    spinnerLabels.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "label");
    spinnerImage.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "image");
    spinnerImage.addSpinnerListener(v -> recompute());
    // ---
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()) {
        case MouseEvent.BUTTON1: // insert point
          if (!isPositioningOngoing())
            recompute();
          break;
        }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
        if (jToggleButton.isSelected() && isPositioningOngoing())
          recompute();
      };
    };
    // ---
    timerFrame.geometricComponent.jComponent.addMouseListener(mouseAdapter);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(mouseAdapter);
  }

  private BufferedImage bufferedImage;

  final void shuffle(int n) {
    RandomSampleInterface randomSampleInterface = geodesicDisplay().randomSampleInterface();
    setControlPointsSe2(RandomSample.of(randomSampleInterface, n));
    // assignment of random labels to points
    vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), RANDOM, n);
    recompute();
  }

  @Override
  public void recompute() {
    System.out.println("recomp");
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
    Classification classification = spinnerLabels.getValue().apply(vector);
    TensorUnaryOperator operator = operator(getGeodesicControlPoints());
    ColorDataLists colorDataLists = spinnerColor.getValue();
    TensorUnaryOperator tensorUnaryOperator = //
        spinnerImage.getValue().operator(classification, operator, colorDataLists.strict());
    int resolution = spinnerRes.getValue();
    bufferedImage = ImageFormat.of(geodesicArrayPlot.raster(resolution, tensorUnaryOperator, Array.zeros(4)));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (Objects.nonNull(bufferedImage)) {
      Tensor pixel2model = geodesicDisplay.geodesicArrayPlot().pixel2model(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
      ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
    }
    // ---
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.0));
    int index = 0;
    for (Tensor point : getGeodesicControlPoints()) {
      int label = vector.Get(index).number().intValue();
      ColorDataLists colorDataLists = spinnerColor.getValue();
      ColorDataIndexed colorDataIndexedT = colorDataLists.cyclic();
      ColorDataIndexed colorDataIndexedO = colorDataIndexedT.deriveWithAlpha(128);
      PointsRender pointsRender = new PointsRender( //
          colorDataIndexedO.getColor(label), //
          colorDataIndexedT.getColor(label));
      pointsRender.show(geodesicDisplay::matrixLift, shape, Tensors.of(point)).render(geometricLayer, graphics);
      ++index;
    }
  }

  public static void main(String[] args) {
    new ClassificationImageDemo().setVisible(1200, 900);
  }
}

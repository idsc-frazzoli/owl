// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

/* package */ class ClassificationImageDemo extends LogWeightingDemo implements ActionListener {
  static final Random RANDOM = new Random();

  private static List<Biinvariant> distinct() {
    return Arrays.asList( //
        Biinvariants.METRIC, //
        Biinvariants.TARGET, //
        Biinvariants.GARDEN);
  }

  // ---
  private final SpinnerLabel<ColorDataLists> spinnerColor = SpinnerLabel.of(ColorDataLists.values());
  private final SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");
  private final SpinnerLabel<Labels> spinnerLabels = SpinnerLabel.of(Labels.values());
  private final SpinnerLabel<ClassificationImage> spinnerImage = SpinnerLabel.of(ClassificationImage.values());
  private final JButton jButtonExport = new JButton("export");
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
      spinnerLabel.setList(Arrays.asList(2, 3, 4, 5));
      spinnerLabel.setValue(3);
      spinnerLabel.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "label count");
      spinnerLabel.addSpinnerListener(v -> shuffle(spinnerCount.getValue()));
    }
    {
      spinnerCount.setList(Arrays.asList(5, 10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(20);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "landmark count");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      spinnerRes.setArray(25, 40, 50, 75, 100, 150, 200, 250);
      spinnerRes.setValue(50);
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
    spinnerImage.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "image");
    spinnerImage.addSpinnerListener(v -> recompute());
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
  }

  private BufferedImage bufferedImage;

  final void shuffle(int n) {
    RandomSampleInterface randomSampleInterface = geodesicDisplay().randomSampleInterface();
    setControlPointsSe2(RandomSample.of(randomSampleInterface, n));
    // assignment of random labels to points
    vector = RandomVariate.of(DiscreteUniformDistribution.of(0, spinnerLabel.getValue()), RANDOM, n);
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
    render(geometricLayer, graphics, geodesicDisplay, getGeodesicControlPoints(), vector, spinnerColor.getValue().strict());
  }

  static void render(GeometricLayer geometricLayer, Graphics2D graphics, GeodesicDisplay geodesicDisplay, Tensor sequence, Tensor vector,
      ColorDataIndexed colorDataIndexedT) {
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.0));
    int index = 0;
    ColorDataIndexed colorDataIndexedO = colorDataIndexedT.deriveWithAlpha(128);
    for (Tensor point : sequence) {
      int label = vector.Get(index).number().intValue();
      PointsRender pointsRender = new PointsRender( //
          colorDataIndexedO.getColor(label), //
          colorDataIndexedT.getColor(label));
      pointsRender.show(geodesicDisplay::matrixLift, shape, Tensors.of(point)).render(geometricLayer, graphics);
      ++index;
    }
  }

  private static final int REFINEMENT = 250;

  @Override
  public final void actionPerformed(ActionEvent actionEvent) {
    LogWeighting logWeighting = logWeighting();
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        geodesicDisplay().toString());
    root.mkdirs();
    for (Biinvariant biinvariant : distinct()) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator operator = logWeighting.operator( //
          biinvariant, //
          geodesicDisplay().vectorLogManifold(), //
          variogram(), //
          sequence);
      System.out.print("computing " + biinvariant);
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
      Classification classification = spinnerLabels.getValue().apply(vector);
      ColorDataLists colorDataLists = spinnerColor.getValue();
      ColorDataIndexed colorDataIndexed = colorDataLists.strict();
      TensorUnaryOperator tensorUnaryOperator = //
          spinnerImage.getValue().operator(classification, operator, colorDataIndexed);
      int resolution = REFINEMENT;
      BufferedImage bufferedImage = //
          ImageFormat.of(geodesicArrayPlot.raster(resolution, tensorUnaryOperator, Array.zeros(4)));
      {
        Tensor matrix = geodesicArrayPlot.pixel2model(new Dimension(resolution, resolution));
        GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(matrix));
        Graphics2D graphics = bufferedImage.createGraphics();
        RenderQuality.setQuality(graphics);
        render(geometricLayer, graphics, geodesicDisplay, sequence, vector, colorDataIndexed);
      }
      // ---
      String format = String.format("%s_%s.png", logWeighting, biinvariant);
      try {
        ImageIO.write(bufferedImage, "png", new File(root, format));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
  }

  public static void main(String[] args) {
    new ClassificationImageDemo().setVisible(1300, 900);
  }
}

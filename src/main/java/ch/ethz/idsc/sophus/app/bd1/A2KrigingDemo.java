// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.CrossAveraging;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class A2KrigingDemo extends ControlPointsDemo {
  private static final List<Object> LIST = new ArrayList<>();
  static {
    LIST.addAll(Arrays.asList(Krigings.values()));
    LIST.addAll(Arrays.asList(LogWeightings.values()));
  }
  // ---
  private final SpinnerLabel<Object> spinnerKriging = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleVarian = new JToggleButton("est/var");
  private final JToggleButton jToggleButton = new JToggleButton("thres");
  private final JButton jButtonExport = new JButton("export");

  public A2KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(true, Arrays.asList(geodesicDisplay));
    {
      spinnerKriging.setList(LIST);
      spinnerKriging.setIndex(0);
      spinnerKriging.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "function type");
    }
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerBeta.setList(Tensors.fromString("{1, 9/8, 5/4, 3/2, 1.75, 1.99}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100);
      spinnerRes.setValue(30);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
    }
    {
      timerFrame.jToolBar.add(jToggleVarian);
      timerFrame.jToolBar.add(jToggleButton);
    }
    {
      JButton jButton = new JButton("round");
      jButton.addActionListener(e -> {
        Tensor tensor = getControlPointsSe2().copy();
        tensor.set(Round.FUNCTION, Tensor.ALL, 2);
        setControlPointsSe2(tensor);
      });
      timerFrame.jToolBar.add(jButton);
    }
    timerFrame.jToolBar.addSeparator();
    {
      jButtonExport.addActionListener(e -> export());
      timerFrame.jToolBar.add(jButtonExport);
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  private Tensor pixel2model(BufferedImage bufferedImage) {
    double rad = rad();
    Tensor range = Tensors.vector(rad, rad).multiply(RealScalar.of(2)); // model
    Tensor scale = Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()) //
        .pmul(range.map(Scalar::reciprocal)); // model 2 pixel
    return Dot.of( //
        Se2Matrix.translation(range.multiply(RationalScalar.HALF.negate())), //
        DiagonalMatrix.with(scale.map(Scalar::reciprocal).append(RealScalar.ONE)), // pixel 2 model
        Se2Matrix.flipY(bufferedImage.getHeight()));
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    prepare();
    // ---
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    BufferedImage bufferedImage = bufferedImage(spinnerRes.getValue(), spinnerKriging.getValue(), sequence, values);
    RenderQuality.setDefault(graphics);
    ImageRender.of(bufferedImage, pixel2model(bufferedImage)) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    LeverRender leverRender = //
        new LeverRender(geodesicDisplay(), sequence, null, values, geometricLayer, graphics);
    leverRender.renderWeights();
  }

  private BufferedImage bufferedImage(int resolution, Object object, Tensor sequence, Tensor values) {
    FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
    TensorScalarFunction tsf = null;
    if (object instanceof Krigings) {
      Krigings krigings = (Krigings) object;
      ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
      Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
      Kriging kriging = //
          krigings.regression(flattenLogManifold, variogram, sequence, values, covariance);
      // ---
      tsf = jToggleVarian.isSelected() //
          ? kriging::variance
          : point -> (Scalar) kriging.estimate(point);
    } else //
    if (object instanceof LogWeighting) {
      LogWeighting logMetricWeightings = (LogWeighting) object;
      WeightingInterface weightingInterface = logMetricWeightings.from(flattenLogManifold);
      TensorUnaryOperator tuo = CrossAveraging.of(weightingInterface, sequence, RnBiinvariantMean.INSTANCE, values);
      tsf = t -> (Scalar) tuo.apply(t);
    }
    Tensor matrix = Tensors.matrix(array(resolution, tsf));
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
    // ---
    Tensor colorData = matrix.map(spinnerColorData.getValue());
    return ImageFormat.of(colorData);
  }

  private void export() {
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    File folder = HomeDirectory.Pictures(getClass().getSimpleName(), spinnerColorData.getValue().toString());
    folder.mkdirs();
    System.out.println("exporting");
    int index = 0;
    for (Object object : LIST) {
      String format = String.format("%02d%s.png", index, object);
      System.out.println(format);
      BufferedImage bufferedImage = bufferedImage(256, object, sequence, values);
      GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(pixel2model(bufferedImage)));
      Graphics2D graphics = bufferedImage.createGraphics();
      RenderQuality.setQuality(graphics);
      renderControlPoints(geometricLayer, graphics);
      try {
        ImageIO.write(bufferedImage, "png", new File(folder, format));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  void prepare() {
    // ---
  }

  /** @param resolution
   * @param tensorScalarFunction
   * @return array of scalar values clipped to interval [0, 1] or DoubleScalar.INDETERMINATE */
  abstract Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction);

  abstract double rad();
}

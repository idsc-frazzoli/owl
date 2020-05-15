// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
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
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Variograms;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ abstract class A2KrigingDemo extends AnKrigingDemo {
  private final SpinnerLabel<HsScalarFunctions> spinnerKriging = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleVarian = new JToggleButton("est/var");
  private final JToggleButton jToggleButton = new JToggleButton("thres");
  private final JButton jButtonExport = new JButton("export");

  public A2KrigingDemo(List<GeodesicDisplay> geodesicDisplays) {
    super(geodesicDisplays);
    {
      spinnerKriging.setArray(HsScalarFunctions.values());
      spinnerKriging.setIndex(0);
      spinnerKriging.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "function type");
    }
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50, 75, 100, 150, 200, 250);
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
  public final void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    prepare();
    // ---
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    ScalarUnaryOperator variogram = variogram();
    BufferedImage bufferedImage = bufferedImage( //
        spinnerRes.getValue(), geodesicDisplay().flattenLogManifold(), spinnerKriging.getValue(), variogram, sequence, values);
    RenderQuality.setDefault(graphics);
    ImageRender.of(bufferedImage, pixel2model(bufferedImage)) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    LeverRender leverRender = //
        new LeverRender(geodesicDisplay(), sequence, null, values, geometricLayer, graphics);
    leverRender.renderWeights();
  }

  private BufferedImage bufferedImage(int resolution, //
      FlattenLogManifold flattenLogManifold, HsScalarFunction hsScalarFunction, ScalarUnaryOperator variogram, Tensor sequence, Tensor values) {
    TensorScalarFunction tsf = hsScalarFunction.build(flattenLogManifold, variogram, sequence, values);
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
    for (HsScalarFunction hsScalarFunction : HsScalarFunctions.values()) {
      String format = String.format("%02d%s.png", index, hsScalarFunction);
      System.out.println(format);
      BufferedImage bufferedImage = bufferedImage(256, //
          geodesicDisplay().flattenLogManifold(), hsScalarFunction, Variograms.POWER.of(RealScalar.of(1.5)), sequence, values);
      GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(pixel2model(bufferedImage)));
      Graphics2D graphics = bufferedImage.createGraphics();
      RenderQuality.setQuality(graphics);
      renderControlPoints(geometricLayer, graphics);
      {
        graphics.setColor(Color.WHITE);
        graphics.drawString(hsScalarFunction.toString(), 0, 10);
      }
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

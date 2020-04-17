// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogMetricWeightings;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.itp.CrossWeighting;
import ch.ethz.idsc.sophus.krg.Krigings;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.WeightingInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.TensorScalarFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ abstract class A2KrigingDemo extends ControlPointsDemo {
  static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  final SpinnerLabel<Krigings> spinnerKriging = SpinnerLabel.of(Krigings.values());
  final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();
  final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleVarian = new JToggleButton("est/var");
  private final JToggleButton jToggleButton = new JToggleButton("thres");

  public A2KrigingDemo(GeodesicDisplay geodesicDisplay) {
    super(true, Arrays.asList(geodesicDisplay));
    spinnerKriging.addToComponentReduced(timerFrame.jToolBar, new Dimension(100, 28), "krigings");
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
    // general
    Tensor sequence = getGeodesicControlPoints();
    Tensor values = getControlPointsSe2().get(Tensor.ALL, 2);
    // ---
    FlattenLogManifold flattenLogManifold = geodesicDisplay().flattenLogManifold();
    // ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
    // Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
    // Kriging kriging = //
    // spinnerKriging.getValue().regression(flattenLogManifold, variogram, sequence, values, covariance);
    // // ---
    // TensorScalarFunction tsf = jToggleVarian.isSelected() //
    // ? kriging::variance
    // : point -> (Scalar) kriging.estimate(point);
    WeightingInterface weightingInterface = LogMetricWeightings.BI_SMOOTH.from(flattenLogManifold, null);
    TensorUnaryOperator tuo = CrossWeighting.of(weightingInterface, sequence, RnBiinvariantMean.INSTANCE, values);
    TensorScalarFunction tsf = t -> (Scalar) tuo.apply(t);
    Tensor matrix = Tensors.matrix(array(spinnerRes.getValue(), tsf));
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION); // effectively maps to 0 or 1
    // ---
    Tensor colorData = matrix.map(spinnerColorData.getValue());
    BufferedImage bufferedImage = ImageFormat.of(colorData);
    RenderQuality.setDefault(graphics);
    ImageRender.of(bufferedImage, pixel2model(bufferedImage)) //
        .render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    LeverRender leverRender = //
        new LeverRender(geodesicDisplay(), sequence, null, values, geometricLayer, graphics);
    leverRender.renderWeights();
  }

  void prepare() {
    // ---
  }

  /** @param resolution
   * @param kriging
   * @return array of scalar values clipped to interval [0, 1] or DoubleScalar.INDETERMINATE */
  abstract Scalar[][] array(int resolution, TensorScalarFunction tensorScalarFunction);

  abstract double rad();
}

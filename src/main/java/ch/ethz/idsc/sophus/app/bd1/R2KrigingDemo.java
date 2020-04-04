// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeverRender;
import ch.ethz.idsc.sophus.krg.Kriging;
import ch.ethz.idsc.sophus.krg.PowerVariogram;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class R2KrigingDemo extends A1KrigingDemo {
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRes = new SpinnerLabel<>();
  private final JToggleButton jToggleButton = new JToggleButton("thres");

  public R2KrigingDemo() {
    {
      spinnerColorData.setArray(ColorDataGradients.values());
      spinnerColorData.setValue(ColorDataGradients.PARULA);
      spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(120, 28), "color scheme");
    }
    {
      spinnerRes.setArray(20, 30, 50);
      spinnerRes.setIndex(0);
      spinnerRes.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "resolution");
    }
    {
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
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0}}"));
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame.configCoordinateOffset(100, 700);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor pointsSe2 = getControlPointsSe2();
    ScalarUnaryOperator variogram = PowerVariogram.of(RealScalar.ONE, spinnerBeta.getValue());
    Tensor sequence = Tensor.of(pointsSe2.stream().map(Extract2D.FUNCTION));
    Tensor values = pointsSe2.get(Tensor.ALL, 2);
    Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), sequence.length()));
    Kriging kriging = spinnerKriging.getValue().regression(RnManifold.INSTANCE, variogram, sequence, values, covariance);
    // ---
    double rad = 10.0;
    int res = spinnerRes.getValue();
    Tensor dx = Subdivide.of(0, rad, res);
    Tensor dy = Subdivide.of(rad, 0, res);
    int rows = dy.length();
    int cols = dx.length();
    Scalar[][] array = new Scalar[rows][cols];
    Clip unit = Clips.unit();
    IntStream.range(0, rows).parallel().forEach(cx -> {
      for (int cy = 0; cy < cols; ++cy)
        array[cy][cx] = unit.apply((Scalar) kriging.estimate(Tensors.of(dx.get(cx), dy.get(cy))));
    });
    Tensor matrix = Tensors.matrix(array);
    // ---
    if (jToggleButton.isSelected())
      matrix = matrix.map(Round.FUNCTION);
    // ---
    ColorDataGradient colorDataGradient = spinnerColorData.getValue();
    Tensor colorData = matrix.map(colorDataGradient);
    // ---
    ImageRender.range(ImageFormat.of(colorData), Tensors.vector(rad, rad)) //
        .render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    LeverRender leverRender = //
        new LeverRender(geodesicDisplay(), sequence, null, values, geometricLayer, graphics);
    leverRender.renderWeights();
  }

  public static void main(String[] args) {
    new R2KrigingDemo().setVisible(1000, 800);
  }
}

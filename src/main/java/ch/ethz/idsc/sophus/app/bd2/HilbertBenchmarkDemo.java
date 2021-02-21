// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.ply.d2.HilbertPolygon;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.sca.Power;

/** References:
 * "Iterative coordinates"
 * by Chongyang Deng, Qingjun Chang, Kai Hormann, 2020 */
/* package */ class HilbertBenchmarkDemo extends ControlPointsDemo {
  final SpinnerLabel<Integer> spinnerLevels = new SpinnerLabel<>();
  final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  JToggleButton jToggleButton = new JToggleButton("ctrl points");

  public HilbertBenchmarkDemo() {
    super(false, GeodesicDisplays.R2_ONLY);
    setPositioningEnabled(false);
    // ---
    timerFrame.jToolBar.add(jToggleButton);
    {
      spinnerLevels.setList(Arrays.asList(1, 2, 3, 4));
      spinnerLevels.setValue(2);
      spinnerLevels.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
      spinnerLevels.addSpinnerListener(v -> updateCtrl());
    }
    {
      spinnerRefine.setList(Arrays.asList(10, 20, 30, 40, 50, 60, 80, 100));
      spinnerRefine.setValue(20);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "refinement");
      spinnerRefine.addSpinnerListener(v -> bufferedImage = null);
    }
    // ---
    updateCtrl();
  }

  void updateCtrl() {
    Tensor polygon = unit(spinnerLevels.getValue());
    polygon = PadRight.zeros(polygon.length(), 3).apply(polygon);
    setControlPointsSe2(polygon);
    bufferedImage = null;
  }

  private BufferedImage bufferedImage = null;

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(Color.LIGHT_GRAY);
    graphics.draw(geometricLayer.toPath2D(CheckerBoardDemo.BOX, true));
    // ---
    RenderQuality.setQuality(graphics);
    final Tensor sequence = getGeodesicControlPoints();
    LeversRender leversRender = //
        LeversRender.of(manifoldDisplay(), sequence, null, geometricLayer, graphics);
    if (jToggleButton.isSelected())
      renderControlPoints(geometricLayer, graphics);
    // leversRender.renderIndexX();
    // leversRender.renderIndexP();
    leversRender.renderSurfaceP();
    int magnification = 4;
    if (Objects.isNull(bufferedImage))
      compute();
    if (Objects.nonNull(bufferedImage))
      graphics.drawImage(bufferedImage, 0, 200, bufferedImage.getWidth() * magnification, bufferedImage.getHeight() * magnification, null);
  }

  public void compute() {
    bufferedImage = HilbertLevelImage.of(manifoldDisplay(), getGeodesicControlPoints(), spinnerRefine.getValue(), ColorDataGradients.CLASSIC, 32);
  }

  /** @param n positive
   * @return hilbert polygon inside unit square [0, 1]^2 */
  public static Tensor unit(int n) {
    Tensor polygon = HilbertPolygon.of(n).multiply(Power.of(2.0, -n + 1));
    return polygon.map(scalar -> scalar.subtract(RealScalar.of(1.0 + 1e-5)));
  }

  public static void main(String[] args) {
    new HilbertBenchmarkDemo().setVisible(1300, 900);
  }
}

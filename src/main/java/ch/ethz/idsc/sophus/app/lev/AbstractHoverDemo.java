// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.SnGeodesicDisplay;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Abs;

/* package */ abstract class AbstractHoverDemo extends LogWeightingDemo {
  static final Random RANDOM = new Random();
  // ---
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");

  public AbstractHoverDemo() {
    super(false, GeodesicDisplays.SE2C_SE2_S2_H2_R2, LogWeightings.list());
    setMidpointIndicated(false);
    setPositioningEnabled(false);
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      spinnerCount.setList(Arrays.asList(5, 10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(15);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      jButtonShuffle.addActionListener(e -> shuffle(spinnerCount.getValue()));
      timerFrame.jToolBar.add(jButtonShuffle);
    }
    shuffle(spinnerCount.getValue());
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
    timerFrame.jToolBar.addSeparator();
  }

  void shuffle(int n) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor xyzs;
    if (geodesicDisplay instanceof SnGeodesicDisplay) {
      RandomSampleInterface randomSampleInterface = SnRandomSample.of(2);
      xyzs = Tensors.vector(i -> randomSampleInterface.randomSample(RANDOM), n);
      xyzs.set(Abs.FUNCTION, Tensor.ALL, 2);
    } else {
      double lim = 3;
      xyzs = Join.of(1, //
          RandomVariate.of(UniformDistribution.of(-lim, lim), RANDOM, n, 2), //
          RandomVariate.of(UniformDistribution.of(-Math.PI, Math.PI), RANDOM, n, 1));
    }
    setControlPointsSe2(xyzs);
  }

  @Override // from RenderInterface
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor origin = geodesicDisplay.project(geometricLayer.getMouseSe2State());
    Tensor weights = operator(sequence).apply(origin);
    LeversRender leversRender = LeversRender.of( //
        geodesicDisplay, //
        operator(sequence), sequence, origin, //
        geometricLayer, graphics);
    render(geometricLayer, graphics, leversRender, weights);
  }

  /** @param geometricLayer
   * @param graphics
   * @param leversRender */
  abstract void render(GeometricLayer geometricLayer, Graphics2D graphics, LeversRender leversRender, Tensor weights);
}

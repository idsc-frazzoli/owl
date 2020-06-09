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
import ch.ethz.idsc.sophus.app.api.SnGeodesicDisplay;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
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
      xyzs = Join.of(1, RandomVariate.of(UniformDistribution.of(-5, 5), RANDOM, n, 2), RandomVariate.of(UniformDistribution.of(-2, 2), RANDOM, n, 1));
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
    TensorUnaryOperator tensorUnaryOperator = operator(sequence);
    render(geometricLayer, graphics, LeverRender.of( //
        geodesicDisplay, //
        tensorUnaryOperator, //
        sequence, //
        geodesicDisplay.project(geometricLayer.getMouseSe2State()), //
        geometricLayer, graphics));
  }

  abstract void render(GeometricLayer geometricLayer, Graphics2D graphics, LeverRender leverRender);
}

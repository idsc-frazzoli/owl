// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

// TODO refactor with S2AnimationDemo
/* package */ class Se2CoveringAnimationDemo extends AbstractPlaceDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public Se2CoveringAnimationDemo() {
    super(GeodesicDisplays.SE2C_SE2, LogWeightings.list());
    {
      timerFrame.jToolBar.add(jToggleAxes);
      jToggleAxes.setSelected(true);
    }
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor sequence = getGeodesicControlPoints();
          if (0 < sequence.length()) {
            GeodesicDisplay geodesicDisplay = geodesicDisplay();
            LieGroup lieGroup = geodesicDisplay.lieGroup();
            LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
            Tensor origin = sequence.get(0);
            snapshot = lieGroupOps.allLeft(sequence, lieGroup.element(origin).inverse().toCoordinate());
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0.3}}"));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0) * 2, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1) * 2, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      if (jToggleAnimate.isSelected())
        setControlPointsSe2(lieGroupOps.allConjugate(snapshot, random(10 + timing.seconds() * 0.1, 0)));
      RenderQuality.setQuality(graphics);
      Tensor sequence = getSequence();
      TensorUnaryOperator tensorUnaryOperator = operator(sequence);
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
      LeversHud.render(biinvariant(), leversRender);
      if (geodesicDisplay.dimensions() < sequence.length() + 1) {
        Tensor weights = tensorUnaryOperator.apply(origin);
        leversRender.renderWeights(weights);
      }
    }
  }

  public static void main(String[] args) {
    new Se2CoveringAnimationDemo().setVisible(1200, 600);
  }
}

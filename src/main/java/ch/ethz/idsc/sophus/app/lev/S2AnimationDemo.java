// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;
import java.util.Optional;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.lie.so3.Rodrigues;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.Timing;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class S2AnimationDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public S2AnimationDemo() {
    super(true, GeodesicDisplays.S2_ONLY, LogWeightings.list());
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor sequence = getGeodesicControlPoints();
          if (0 < sequence.length()) {
            snapshot = snapshotUncentered.copy();
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.matrix(new Number[][] { //
        { 0.000, 0.000, 0.000 }, { 0.699, -0.521, 0.000 }, { 0.641, 0.634, 0.000 }, //
        { -0.320, 0.734, 0 }, { -0.067, -0.716, 0.000 }, { -0.768, 0.215, 0.000 } }));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2)).multiply(RealScalar.of(1));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      if (jToggleAnimate.isSelected()) {
        Tensor vector = random(10 + timing.seconds() * 0.1, 0);
        Tensor vectorExp = Rodrigues.vectorExp(vector);
        Tensor list = Tensors.empty();
        for (Tensor xya : snapshot) {
          Tensor project = vectorExp.dot(geodesicDisplay.project(xya));
          Tensor xya_ = geodesicDisplay.toPoint(project).append(Sign.isNegative(project.Get(2)) //
              ? RealScalar.of(-1)
              : RealScalar.ZERO);
          list.append(xya_);
        }
        setControlPointsSe2(list);
      }
      RenderQuality.setQuality(graphics);
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
      LeversHud.render(bitype(), leversRender);
    }
  }

  public static void main(String[] args) {
    new S2AnimationDemo().setVisible(1200, 600);
  }
}

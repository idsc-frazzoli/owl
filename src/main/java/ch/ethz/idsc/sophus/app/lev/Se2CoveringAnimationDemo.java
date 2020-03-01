// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;

/* package */ class Se2CoveringAnimationDemo extends ControlPointsDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public Se2CoveringAnimationDemo() {
    super(true, GeodesicDisplays.SE2C_ONLY);
    setMidpointIndicated(false);
    {
      timerFrame.jToolBar.add(jToggleAxes);
      jToggleAxes.setSelected(true);
    }
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor controlPointsAll = getGeodesicControlPoints();
          if (0 < controlPointsAll.length()) {
            GeodesicDisplay geodesicDisplay = geodesicDisplay();
            LieGroup lieGroup = geodesicDisplay.lieGroup();
            LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
            Tensor origin = controlPointsAll.get(0);
            snapshot = lieGroupOps.allLeft(controlPointsAll, lieGroup.element(origin).inverse().toCoordinate());
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
    Tensor controlPoints = getGeodesicControlPoints();
    if (0 < controlPoints.length()) {
      if (jToggleAnimate.isSelected())
        setControlPointsSe2(lieGroupOps.allConjugate(snapshot, random(10 + timing.seconds() * 0.1, 0)));
      GraphicsUtil.setQualityHigh(graphics);
      LeverRender leverRender = new LeverRender( //
          geodesicDisplay, controlPoints.extract(1, controlPoints.length()), controlPoints.get(0), geometricLayer, graphics);
      leverRender.renderLevers();
      leverRender.renderSequence();
      leverRender.renderOrigin();
    }
  }

  public static void main(String[] args) {
    new Se2CoveringAnimationDemo().setVisible(1200, 600);
  }
}

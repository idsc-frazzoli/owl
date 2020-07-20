// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.HsInfluence;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Se2CoveringInvarianceDemo extends AbstractPlaceDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JTextField jTextField = new JTextField();

  public Se2CoveringInvarianceDemo() {
    super(GeodesicDisplays.SE2C_SE2, LogWeightings.list());
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jTextField.setText("{1, 1, 3}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setGeodesicDisplay(Se2GeodesicDisplay.INSTANCE);
    setControlPointsSe2(Tensors.fromString( //
        "{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0}}"));
    setControlPointsSe2(Tensors.fromString( //
        "{{0.000, 0.000, 0.000}, {-0.950, 1.750, -2.618}, {0.833, 2.300, -1.047}, {2.667, 0.733, -2.618}, {2.033, -1.800, 2.356}, {-1.217, -0.633, -3.665}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Tensor controlPointsAll = getGeodesicControlPoints();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    if (0 < controlPointsAll.length()) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.vectorLogManifold();
      {
        Tensor sequence = controlPointsAll.extract(1, controlPointsAll.length());
        Tensor origin = controlPointsAll.get(0);
        Tensor weights = new HsInfluence(vectorLogManifold.logAt(origin), sequence).leverages_sqrt();
        LeversRender leversRender = //
            LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderLevers();
        leversRender.renderWeights(weights);
        leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
        leversRender.renderOrigin();
        leversRender.renderIndexX();
        leversRender.renderIndexP();
      }
      geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(10, 0)));
      try {
        Tensor allR = lieGroupOps.allRight(controlPointsAll, Tensors.fromString(jTextField.getText()));
        Tensor result = lieGroupOps.allLeft(allR, lieGroup.element(allR.get(0)).inverse().toCoordinate());
        Tensor sequence = result.extract(1, result.length());
        Tensor origin = result.get(0);
        Tensor weights = new HsInfluence(vectorLogManifold.logAt(origin), sequence).leverages_sqrt();
        LeversRender leversRender = //
            LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
        leversRender.renderSequence();
        leversRender.renderLevers();
        leversRender.renderWeights(weights);
        leversRender.renderInfluenceX(LeversHud.COLOR_DATA_GRADIENT);
        leversRender.renderOrigin();
        leversRender.renderIndexX("x'");
        leversRender.renderIndexP("p'");
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new Se2CoveringInvarianceDemo().setVisible(1200, 600);
  }
}

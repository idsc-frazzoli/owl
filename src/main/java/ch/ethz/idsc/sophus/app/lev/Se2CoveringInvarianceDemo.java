// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2Display;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.TensorMapping;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.InfluenceMatrix;

/* package */ class Se2CoveringInvarianceDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JTextField jTextField = new JTextField();

  public Se2CoveringInvarianceDemo() {
    super(true, GeodesicDisplays.SE2C_SE2, LogWeightings.list());
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jTextField.setText("{1, 1, 3}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setGeodesicDisplay(Se2Display.INSTANCE);
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
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Tensor controlPointsAll = getGeodesicControlPoints();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    if (0 < controlPointsAll.length()) {
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      {
        Tensor sequence = controlPointsAll.extract(1, controlPointsAll.length());
        Tensor origin = controlPointsAll.get(0);
        Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
        Tensor weights = InfluenceMatrix.of(matrix).leverages_sqrt();
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
        TensorMapping lieGroupOR = lieGroupOps.actionR(Tensors.fromString(jTextField.getText()));
        Tensor allR = lieGroupOR.slash(controlPointsAll);
        TensorMapping lieGroupOp = lieGroupOps.actionL(lieGroup.element(allR.get(0)).inverse().toCoordinate());
        Tensor result = lieGroupOp.slash(allR);
        Tensor sequence = result.extract(1, result.length());
        Tensor origin = result.get(0);
        Tensor matrix = new HsDesign(vectorLogManifold).matrix(sequence, origin);
        Tensor weights = InfluenceMatrix.of(matrix).leverages_sqrt();
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

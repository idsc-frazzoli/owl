// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class Se2CoveringInvarianceDemo extends ControlPointsDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JTextField jTextField = new JTextField();

  public Se2CoveringInvarianceDemo() {
    super(true, GeodesicDisplays.SE2C_SE2);
    setMidpointIndicated(false);
    {
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jTextField.setText("{1, 1, 1}");
      jTextField.setPreferredSize(new Dimension(100, 28));
      timerFrame.jToolBar.add(jTextField);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    Tensor controlPointsAll = getGeodesicControlPoints();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    if (0 < controlPointsAll.length()) {
      {
        LeverRender leverRender = LeverRender.of(geodesicDisplay, //
            controlPointsAll.extract(1, controlPointsAll.length()), //
            controlPointsAll.get(0), geometricLayer, graphics);
        leverRender.renderSequence();
        leverRender.renderLevers();
        leverRender.renderWeights();
        leverRender.renderOrigin();
      }
      try {
        geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(10, 0)));
        Tensor allR = lieGroupOps.allRight(controlPointsAll, Tensors.fromString(jTextField.getText()));
        Tensor result = lieGroupOps.allLeft(allR, lieGroup.element(allR.get(0)).inverse().toCoordinate());
        LeverRender leverRender = LeverRender.of(geodesicDisplay, //
            result.extract(1, result.length()), result.get(0), geometricLayer, graphics);
        leverRender.renderSequence();
        leverRender.renderLevers();
        leverRender.renderWeights();
        leverRender.renderOrigin();
        geometricLayer.popMatrix();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  }

  public static void main(String[] args) {
    new Se2CoveringInvarianceDemo().setVisible(1200, 600);
  }
}

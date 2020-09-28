// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ref.gui.ParametersComponent;

public class GuiExtension extends ControlPointsDemo {
  public Tensor tensor = Tensors.fromString("");

  public GuiExtension() {
    super(true, GeodesicDisplays.R2_ONLY);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    guiExtension.setVisible(800, 600);
    System.out.println("here");
    try {
      JDialog jDialog = new JDialog();
      jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
      ParametersComponent parametersComponent = new ParametersComponent(guiExtension);
      jDialog.setContentPane(parametersComponent.getScrollPane());
      jDialog.setBounds(800, 200, 300, 300);
      jDialog.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    // TensorProperties tensorProperties = TensorProperties.wrap(guiExtension);
    // tensorProperties.fields().forEach(System.out::println);
  }
}

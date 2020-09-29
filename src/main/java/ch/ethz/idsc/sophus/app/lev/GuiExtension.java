// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.gui.ParametersComponent;

public class GuiExtension {
  public Tensor tensor = Tensors.fromString("{1, 2}");
  public File file = HomeDirectory.file();
  public String string = "abc";
  public Scalar scalar = Quantity.of(3, "m*s^-1");
  public Pivots pivots = Pivots.ARGMAX_ABS;
  public Boolean status = true;

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    JDialog jDialog = new JDialog();
    jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    ParametersComponent parametersComponent = new ParametersComponent(guiExtension, new GuiExtension());
    jDialog.setContentPane(parametersComponent.getScrollPane());
    jDialog.setBounds(800, 200, 500, 300);
    jDialog.setVisible(true);
  }
}

// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.io.File;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.ref.FieldColor;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;

public class GuiExtension {
  public String string = "abc";
  public Boolean status = true;
  public Pivots pivots = Pivots.ARGMAX_ABS;
  public File file = HomeDirectory.file();
  @FieldColor
  public Tensor color = Tensors.vector(250, 120, 93, 128);
  public Tensor tensor = Tensors.fromString("{1, 2}");
  @FieldClip(min = "0[m*s^-1]", max = "10[m*s^-1]")
  public Scalar scalar = Quantity.of(3, "m*s^-1");
  @FieldIntegerQ
  public Scalar integer = RealScalar.of(12);

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    JDialog jDialog = new JDialog();
    jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    ConfigPanel configPanel = ConfigPanel.of(guiExtension);
    jDialog.setContentPane(configPanel.getFieldsAndTextarea());
    jDialog.setBounds(500, 200, 500, 500);
    jDialog.setVisible(true);
  }
}

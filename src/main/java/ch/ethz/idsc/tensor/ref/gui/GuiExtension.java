// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GuiExtension {
  public String string = "abc";
  public Boolean status = true;
  public Pivots pivots = Pivots.ARGMAX_ABS;
  public File file = HomeDirectory.file();
  public Tensor tensor = Tensors.fromString("{1, 2}");
  public Scalar scalar = Quantity.of(3, "m*s^-1");

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    JDialog jDialog = new JDialog();
    jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    ParametersComponent parametersComponent = new ParametersComponent(guiExtension, new GuiExtension());
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add("North", parametersComponent.getScrollPane());
    JTextArea jTextArea = new JTextArea();
    jPanel.add("Center", jTextArea);
    jDialog.setContentPane(jPanel);
    jDialog.setBounds(500, 200, 500, 500);
    jDialog.setVisible(true);
  }
}

// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Pivots;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.ref.FieldClip;
import ch.ethz.idsc.tensor.ref.FieldIntegerQ;
import ch.ethz.idsc.tensor.ref.ObjectProperties;

public class GuiExtension {
  public String string = "abc";
  public Boolean status = true;
  public Pivots pivots = Pivots.ARGMAX_ABS;
  public File file = HomeDirectory.file();
  public Tensor tensor = Tensors.fromString("{1, 2}");
  @FieldClip(min = "0[m*s^-1]", max = "10[m*s^-1]")
  public Scalar scalar = Quantity.of(3, "m*s^-1");
  @FieldIntegerQ
  public Scalar integer = RealScalar.of(12);

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    JDialog jDialog = new JDialog();
    jDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // ObjectProperties.wrap(guiExtension).fields().entrySet().stream().forEach(System.out::println);
    // ObjectProperties.wrap(guiExtension).fields().keySet().stream().forEach(System.out::println);
    FieldPanels fieldPanels = FieldPanels.of(guiExtension, new GuiExtension());
    ParametersComponent parametersComponent = new ParametersComponent(fieldPanels);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add("North", parametersComponent.getScrollPane());
    JTextArea jTextArea = new JTextArea();
    Consumer<String> consumer = s -> {
      String text = ObjectProperties.wrap(guiExtension).strings().stream().collect(Collectors.joining("\n"));
      jTextArea.setText(text);
    };
    consumer.accept(null);
    fieldPanels.addUniversalListener(consumer);
    jPanel.add("Center", jTextArea);
    jDialog.setContentPane(jPanel);
    jDialog.setBounds(500, 200, 500, 500);
    jDialog.setVisible(true);
  }
}

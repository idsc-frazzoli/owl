// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JToolBar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** component that generically inspects a given object for fields of type
 * {@link Tensor} and {@link Scalar}. For each such field, a text field
 * is provided that allows the modification of the value. */
// TODO JPH file is getting too long
public class ParametersComponent extends ToolbarsComponent {
  private final JButton jButtonUpdate = new JButton("udpate");
  private final JButton jButtonSave = new JButton("save");

  /** @param object non-null
   * @param reference for instance object.getClass().getDeclaredConstructor().newInstance();
   * @throws Exception if given object is null, or class of object does not have a default constructor */
  public ParametersComponent(FieldPanels fieldPanels) {
    // TODO assert that object and reference are of the same type!
    {
      JToolBar jToolBar = createRow("Actions");
      {
        // jButtonUpdate.addActionListener(actionEvent -> fieldPanels.updateInstance());
        jButtonUpdate.setToolTipText("parse values in text fields into live memory");
        jToolBar.add(jButtonUpdate);
      }
      {
        jButtonSave.addActionListener(actionEvent -> {
          // fieldPanels.updateInstance();
          // AppResources.save(object); // TODO
        });
        jButtonSave.setToolTipText("update values to memory, and save to disk");
        jToolBar.add(jButtonSave);
      }
    }
    addSeparator();
    for (Entry<Field, FieldPanel> entry : fieldPanels.map().entrySet()) {
      Field field = entry.getKey();
      FieldPanel fieldPanel = entry.getValue();
      int height = 28;
      JToolBar jToolBar1 = new JToolBar();
      jToolBar1.setFloatable(false);
      jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
      JLabel jLabel = new JLabel(field.getName());
      jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, height));
      jToolBar1.add(jLabel);
      addPair(jToolBar1, fieldPanel.getComponent(), height);
    }
  }
}

// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import ch.ethz.idsc.tensor.ref.FieldSubdivide;
import ch.ethz.idsc.tensor.ref.TensorProperties;
import ch.ethz.idsc.tensor.ref.TensorProperties.Type;
import ch.ethz.idsc.tensor.ref.TensorReflection;

/** component that generically inspects a given object for fields of type
 * {@link Tensor} and {@link Scalar}. For each such field, a text field
 * is provided that allows the modification of the value. */
// TODO JPH file is getting too long
public class ParametersComponent extends ToolbarsComponent {
  private static final Font FONT = new Font(Font.DIALOG_INPUT, Font.BOLD, 20);
  private static final Color FAIL = new Color(255, 192, 192);
  private static final Color SYNC = new Color(255, 255, 192);
  private static final int BUTTON = 56;
  // ---
  private final Object object;
  private final Object reference;
  private final Map<Field, JTextField> map = new HashMap<>();
  private final JButton jButtonUpdate = new JButton("udpate");
  private final JButton jButtonSave = new JButton("save");

  private void updateInstance() {
    Properties properties = new Properties();
    for (Entry<Field, JTextField> entry : map.entrySet())
      properties.setProperty(entry.getKey().getName(), entry.getValue().getText());
    TensorProperties.wrap(object).set(properties);
  }

  private JButton create(String string, JTextField jTextField, Tensor subdivide, Field field, int increment) {
    JButton jButton = new JButton(string);
    jButton.addActionListener(actionEvent -> {
      try {
        Tensor closest = StaticHelper.closest(subdivide, (Tensor) field.get(object), increment);
        jTextField.setText(closest.toString());
        updateBackground(jTextField, field);
        if (checkFields())
          updateInstance();
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    });
    jButton.setPreferredSize(new Dimension(BUTTON, BUTTON));
    return jButton;
  }

  /** @param object non-null
   * @param reference for instance object.getClass().getDeclaredConstructor().newInstance();
   * @throws Exception if given object is null, or class of object does not have a default constructor */
  public ParametersComponent(Object object, Object reference) {
    this.object = object;
    this.reference = reference;
    // TODO assert that object and reference are of the same type!
    {
      JToolBar jToolBar = createRow("Actions");
      {
        jButtonUpdate.addActionListener(actionEvent -> updateInstance());
        jButtonUpdate.setToolTipText("parse values in text fields into live memory");
        jToolBar.add(jButtonUpdate);
      }
      {
        jButtonSave.addActionListener(actionEvent -> {
          updateInstance();
          // AppResources.save(object); // TODO
        });
        jButtonSave.setToolTipText("update values to memory, and save to disk");
        jToolBar.add(jButtonSave);
      }
    }
    addSeparator();
    Map<Field, Type> fieldMap = TensorProperties.wrap(object).fields();
    for (Entry<Field, Type> entry : fieldMap.entrySet()) {
      Field field = entry.getKey();
      Type type = entry.getValue();
      try {
        Object value = field.get(object); // check for failure, value only at begin!
        switch (type) {
        // case TENSOR:
        // break;
        case FILE: {
          JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
          JLabel jLabel = new JLabel();
          jLabel.setPreferredSize(new Dimension(250, BUTTON));
          jLabel.setFont(FONT);
          jLabel.setText(value.toString());
          jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
              System.out.println("HERE");
              File f = (File) value;
              JFileChooser jFileChooser = new JFileChooser(f);
              jFileChooser.setBounds(100, 100, 600, 600);
              int openDialog = jFileChooser.showOpenDialog(null);
              System.out.println(openDialog);
            }
          });
          jToolBar.add(jLabel);
          break;
        }
        case ENUM: {
          Object[] objects = field.getType().getEnumConstants();
          SpinnerLabel s = new SpinnerLabel();
          s.setArray(objects);
          s.setValueSafe(value);
          JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
          s.addToComponentReduced(jToolBar, new Dimension(200, 20), "tooltip");
          break;
        }
        default:
          Optional<Tensor> optional = TensorReflection.of(field.getAnnotation(FieldSubdivide.class));
          // TODO JPH check if annotations can be restricted to fields of certain class
          final JTextField jTextField;
          if (optional.isPresent() && value instanceof Tensor) {
            Tensor tensor = optional.get();
            JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
            jTextField = new JTextField();
            jTextField.setPreferredSize(new Dimension(250, BUTTON));
            jToolBar.add(create("<", jTextField, tensor, field, -1));
            jTextField.setEditable(false);
            jToolBar.add(jTextField);
            jToolBar.add(create(">", jTextField, tensor, field, +1));
          } else //
          if (value instanceof Boolean) {
            // TODO JPH indicate if value is different from default value
            jTextField = new JTextField();
            JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
            JToggleButton jToggleButton = new JToggleButton(value.toString());
            jToggleButton.setPreferredSize(new Dimension(250, BUTTON));
            jToggleButton.setSelected((Boolean) value);
            jToggleButton.addActionListener(actionEvent -> {
              String text = "" + jToggleButton.isSelected();
              jTextField.setText(text);
              jToggleButton.setText(text);
              if (checkFields())
                updateInstance();
            });
            jToolBar.add(jToggleButton);
          } else {
            jTextField = createEditing(field.getName());
            jTextField.addKeyListener(new KeyAdapter() {
              @Override
              public void keyReleased(KeyEvent keyEvent) {
                updateBackground(jTextField, field);
                checkFields();
              }
            });
            jTextField.addActionListener(actionEvent -> {
              if (checkFields())
                updateInstance();
            });
          }
          jTextField.setFont(FONT);
          jTextField.setText(value.toString());
          updateBackground(jTextField, field);
          map.put(field, jTextField);
          break;
        }
      } catch (Exception exception) {
        // ---
      }
    }
  }

  private void updateBackground(JTextField jTextField, Field field) {
    String string = jTextField.getText();
    boolean isOk = isOk(field, string);
    jTextField.setBackground(isOk ? Color.WHITE : FAIL);
    if (isOk)
      try {
        Object compare = field.get(reference);
        Object object = TensorProperties.parse(field, string);
        if (!compare.equals(object))
          jTextField.setBackground(SYNC);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  private boolean checkFields() {
    boolean status = true;
    for (Entry<Field, JTextField> entry : map.entrySet()) {
      Field field = entry.getKey();
      status &= isOk(field, entry.getValue().getText());
    }
    jButtonUpdate.setEnabled(status);
    jButtonSave.setEnabled(status);
    return status;
  }

  private static boolean isOk(Field field, String string) {
    Object object = TensorProperties.parse(field, string);
    if (object instanceof Tensor)
      return !StringScalarQ.any((Tensor) object);
    return Objects.nonNull(object);
  }
}

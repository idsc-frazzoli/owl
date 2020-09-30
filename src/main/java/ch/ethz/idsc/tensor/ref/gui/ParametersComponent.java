// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.StringScalarQ;
import ch.ethz.idsc.tensor.ref.ObjectProperties;
import ch.ethz.idsc.tensor.ref.ObjectProperties.Type;

/** component that generically inspects a given object for fields of type
 * {@link Tensor} and {@link Scalar}. For each such field, a text field
 * is provided that allows the modification of the value. */
// TODO JPH file is getting too long
public class ParametersComponent extends ToolbarsComponent {
  private static final Color FAIL = new Color(255, 192, 192);
  private static final Color SYNC = new Color(255, 255, 192);
  private static final int BUTTON = 56;
  // ---
  private final Object object;
  private final Object reference;
  private final Map<Field, FieldPanel> map = new HashMap<>();
  private final JButton jButtonUpdate = new JButton("udpate");
  private final JButton jButtonSave = new JButton("save");

  private void updateInstance() {
    Properties properties = new Properties();
    for (Entry<Field, FieldPanel> entry : map.entrySet())
      properties.setProperty(entry.getKey().getName(), entry.getValue().getText());
    ObjectProperties.wrap(object).set(properties);
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
    Map<Field, Type> fieldMap = ObjectProperties.wrap(object).fields();
    for (Entry<Field, Type> entry : fieldMap.entrySet()) {
      Field field = entry.getKey();
      Type type = entry.getValue();
      try {
        Object value = field.get(object); // check for failure, value only at begin!
        FieldPanel fieldPanel = factor(field, type, value);
        {
          int height = 28;
          JToolBar jToolBar1 = new JToolBar();
          jToolBar1.setFloatable(false);
          jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
          JLabel jLabel = new JLabel(field.getName());
          jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, height));
          jToolBar1.add(jLabel);
          addPair(jToolBar1, fieldPanel.getComponent(), height);
        }
        map.put(field, fieldPanel);
      } catch (Exception exception) {
        // ---
      }
    }
    addListener(s -> updateInstance());
  }

  FieldPanel factor(Field field, Type type, Object value) {
    switch (type) {
    case STRING:
      return new StringPanel((String) value);
    case BOOLEAN:
      return new BooleanPanel((Boolean) value);
    case ENUM:
      return new EnumPanel(field.getType().getEnumConstants(), value);
    case FILE:
      return new FilePanel((File) value);
    case TENSOR:
      return new TensorPanel((Tensor) value);
    case SCALAR:
      return new ScalarPanel(field, (Scalar) value);
    }
    throw new RuntimeException();
    // Optional<Tensor> optional = TensorReflection.of(field.getAnnotation(FieldSubdivide.class));
    // final JTextField jTextField;
    // if (optional.isPresent() && value instanceof Tensor) {
    // Tensor tensor = optional.get();
    // JToolBar jToolBar = createRow(field.getName(), BUTTON + 2);
    // jTextField = new JTextField();
    // jTextField.setPreferredSize(new Dimension(250, BUTTON));
    // jToolBar.add(create("<", jTextField, tensor, field, -1));
    // jTextField.setEditable(false);
    // jToolBar.add(jTextField);
    // jToolBar.add(create(">", jTextField, tensor, field, +1));
    // } else //
    // } else {
    // jTextField.setFont(FONT);
    // jTextField.setText(value.toString());
    // updateBackground(jTextField, field);
    // break;
  }

  private void updateBackground(JTextField jTextField, Field field) {
    String string = jTextField.getText();
    boolean isOk = isOk(field, string);
    jTextField.setBackground(isOk ? Color.WHITE : FAIL);
    if (isOk)
      try {
        Object compare = field.get(reference);
        Object object = ObjectProperties.parse(field, string);
        if (!compare.equals(object))
          jTextField.setBackground(SYNC);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  private boolean checkFields() {
    boolean status = true;
    for (Entry<Field, FieldPanel> entry : map.entrySet()) {
      Field field = entry.getKey();
      status &= isOk(field, entry.getValue().getText());
    }
    jButtonUpdate.setEnabled(status);
    jButtonSave.setEnabled(status);
    return status;
  }

  private static boolean isOk(Field field, String string) {
    Object object = ObjectProperties.parse(field, string);
    if (object instanceof Tensor)
      return !StringScalarQ.any((Tensor) object);
    return Objects.nonNull(object);
  }

  public void addListener(Consumer<String> consumer) {
    map.values().stream().forEach(fp -> fp.addListener(consumer));
  }
}

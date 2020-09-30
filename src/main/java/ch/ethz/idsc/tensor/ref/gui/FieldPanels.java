// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.ref.FieldType;
import ch.ethz.idsc.tensor.ref.ObjectProperties;

public class FieldPanels {
  private final Map<Field, FieldPanel> map = new LinkedHashMap<>();

  public FieldPanels(Object object, Object reference) {
    ObjectProperties objectProperties = ObjectProperties.wrap(object);
    Map<Field, FieldType> fieldMap = objectProperties.fields();
    for (Entry<Field, FieldType> entry : fieldMap.entrySet()) {
      Field field = entry.getKey();
      FieldType type = entry.getValue();
      try {
        Object value = field.get(object); // check for failure, value only at begin!
        FieldPanel fieldPanel = factor(field, type, value);
        fieldPanel.addListener(string -> objectProperties.setIfValid(field, type, string));
        map.put(field, fieldPanel);
      } catch (Exception exception) {
        // ---
      }
    }
  }

  public Map<Field, FieldPanel> map() {
    return Collections.unmodifiableMap(map);
  }

  public void addUniversalListener(Consumer<String> consumer) {
    map.values().stream().forEach(fieldPanel -> fieldPanel.addListener(consumer));
  }

  private static FieldPanel factor(Field field, FieldType type, Object value) {
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
  }
}

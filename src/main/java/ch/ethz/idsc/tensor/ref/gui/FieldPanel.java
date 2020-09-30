// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JComponent;

/* package */ abstract class FieldPanel {
  private final List<Consumer<String>> list = new LinkedList<>();

  public final void addListener(Consumer<String> consumer) {
    list.add(consumer);
  }

  public final void notifyListeners(String text) {
    list.forEach(consumer -> consumer.accept(text));
  }

  abstract JComponent getComponent();
}

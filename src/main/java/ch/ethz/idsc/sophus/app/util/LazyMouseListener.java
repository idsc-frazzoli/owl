// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.event.MouseEvent;

public interface LazyMouseListener {
  void lazyClicked(MouseEvent mouseEvent);

  default void lazyDragged(MouseEvent mouseEvent) {
    // empty by default
  }
}

// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.event.MouseEvent;

interface LazyMouseListener {
  void lazyClicked(MouseEvent myMouseEvent);

  default void lazyDragged(MouseEvent myMouseEvent) {
    // empty by default
  }
}

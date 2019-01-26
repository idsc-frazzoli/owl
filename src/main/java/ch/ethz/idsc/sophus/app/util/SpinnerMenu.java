// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

class SpinnerMenu<Type> extends StandardMenu {
  private final Map<Type, JMenuItem> map = new LinkedHashMap<>();
  private final SpinnerLabel<Type> spinnerLabel;
  private final boolean hover;

  SpinnerMenu(SpinnerLabel<Type> spinnerLabel, boolean hover) {
    this.spinnerLabel = spinnerLabel;
    this.hover = hover;
  }

  @Override
  protected void design(JPopupMenu jPopupMenu) {
    for (Type type : spinnerLabel.list) {
      JMenuItem jMenuItem = new JMenuItem(type.toString());
      if (hover)
        jMenuItem.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseEntered(MouseEvent mouseEvent) {
            setValue(type);
          }
        });
      jMenuItem.addActionListener(actionEvent -> {
        if (!type.equals(spinnerLabel.getValue())) // invoke only when different
          setValue(type);
      });
      map.put(type, jMenuItem);
      jPopupMenu.add(jMenuItem);
    }
  }

  private void setValue(Type type) {
    spinnerLabel.setValueSafe(type);
    spinnerLabel.reportToAll();
  }

  public void showRight(JLabel jLabel) {
    JPopupMenu jPopupMenu = designShow();
    // ---
    Type myType = spinnerLabel.getValue();
    if (myType != null) {
      int delta = 2;
      map.get(myType).setBackground(Colors.ACTIVE_ITEM); // Colors.gold
      for (Entry<Type, JMenuItem> entry : map.entrySet()) {
        delta += entry.getValue().getPreferredSize().height;
        if (entry.getKey().equals(myType)) {
          delta -= entry.getValue().getPreferredSize().height / 2;
          break;
        }
      }
      Dimension dimension = jLabel.getSize();
      jPopupMenu.show(jLabel, dimension.width, dimension.height / 2 - delta);
    }
  }
}

// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

/* package */ final class RowPanel {
  private final GridBagLayout gridBagLayout = new GridBagLayout();
  public final JPanel jPanel = new JPanel(gridBagLayout);
  private final GridBagConstraints gridBagConstraints = new GridBagConstraints();

  public RowPanel() {
    gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1;
    jPanel.setOpaque(false);
  }

  public void add(JComponent jComponent) {
    ++gridBagConstraints.gridy; // initially -1
    gridBagLayout.setConstraints(jComponent, gridBagConstraints);
    jPanel.add(jComponent);
    jPanel.repaint();
  }
}

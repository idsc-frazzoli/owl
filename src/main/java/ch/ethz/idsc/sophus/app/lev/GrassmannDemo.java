// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

/* package */ class GrassmannDemo extends AbstractHoverDemo {
  private final SpinnerLabel<ColorDataGradient> spinnerColorData = SpinnerLabel.of(ColorDataGradients.values());
  private final JToggleButton jToggleShowX = new JToggleButton("x");
  private final JToggleButton jToggleShowP = new JToggleButton("P");

  public GrassmannDemo() {
    spinnerColorData.setValue(ColorDataGradients.TEMPERATURE);
    spinnerColorData.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "color scheme");
    // ---
    jToggleShowX.setSelected(true);
    timerFrame.jToolBar.add(jToggleShowX);
    // ---
    timerFrame.jToolBar.add(jToggleShowP);
    // ---
    spinnerCount.setValue(5);
    shuffle(spinnerCount.getValue());
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics, LeverRender leverRender) {
    leverRender.renderLevers();
    // ---
    ColorDataGradient colorDataGradient = spinnerColorData.getValue().deriveWithOpacity(RealScalar.of(0.5));
    if (jToggleShowP.isSelected())
      leverRender.renderGrassmannians(colorDataGradient);
    if (jToggleShowX.isSelected())
      leverRender.renderGrassmannianOrigin(colorDataGradient);
    leverRender.renderSequence();
    leverRender.renderOrigin();
  }

  public static void main(String[] args) {
    new GrassmannDemo().setVisible(1200, 900);
  }
}

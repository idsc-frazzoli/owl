// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.Spd2GeodesicDisplay;
import ch.ethz.idsc.sophus.hs.spd.SpdExponential;
import ch.ethz.idsc.sophus.hs.spd.SpdGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.lie.Symmetrize;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class SpdGeodesicDemo extends GeodesicDisplayDemo {
  private static final Distribution DISTRIBUTION = UniformDistribution.of(-1, 1);
  // ---
  private final JButton jButton = new JButton("suffle");
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  // ---
  private Tensor p;
  private Tensor q;

  public SpdGeodesicDemo() {
    super(GeodesicDisplays.SPD2_ONLY);
    jButton.addActionListener(e -> shuffle());
    timerFrame.jToolBar.add(jButton);
    // ---
    spinnerRefine.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    shuffle();
  }

  private void shuffle() {
    p = SpdExponential.INSTANCE.exp(Symmetrize.of(RandomVariate.of(DISTRIBUTION, 2, 2)));
    q = SpdExponential.INSTANCE.exp(Symmetrize.of(RandomVariate.of(DISTRIBUTION, 2, 2)));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    ScalarTensorFunction scalarTensorFunction = SpdGeodesic.INSTANCE.curve(p, q);
    graphics.setStroke(new BasicStroke(1.5f));
    graphics.setColor(Color.LIGHT_GRAY);
    Tensor shape = Spd2GeodesicDisplay.INSTANCE.shape();
    for (Tensor _t : Subdivide.of(0, 1, spinnerRefine.getValue())) {
      Tensor pq = scalarTensorFunction.apply(_t.Get());
      geometricLayer.pushMatrix(Spd2GeodesicDisplay.INSTANCE.matrixLift(pq));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    graphics.setColor(Color.BLUE);
    for (Tensor _t : Subdivide.of(0, 1, 1)) {
      Tensor pq = scalarTensorFunction.apply(_t.Get());
      geometricLayer.pushMatrix(Spd2GeodesicDisplay.INSTANCE.matrixLift(pq));
      graphics.draw(geometricLayer.toPath2D(shape, true));
      geometricLayer.popMatrix();
    }
    graphics.setStroke(new BasicStroke());
  }

  public static void main(String[] args) {
    new SpdGeodesicDemo().setVisible(1000, 600);
  }
}

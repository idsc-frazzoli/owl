// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.hs.spd.SpdExponential;
import ch.ethz.idsc.sophus.hs.spd.SpdGeodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class SpdGeodesicDemo extends AbstractDemo {
  private static final Distribution DISTRIBUTION = UniformDistribution.of(-1, 1);
  // ---
  private final JButton jButton = new JButton("suffle");
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private Tensor p;
  private Tensor q;

  public SpdGeodesicDemo() {
    jButton.addActionListener(e -> shuffle());
    timerFrame.jToolBar.add(jButton);
    {
      spinnerRefine.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(6);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    shuffle();
  }

  private void shuffle() {
    {
      Tensor a = RandomVariate.of(DISTRIBUTION, 2, 2);
      p = SpdExponential.INSTANCE.exp(Transpose.of(a).add(a).multiply(RationalScalar.HALF));
    }
    {
      Tensor a = RandomVariate.of(DISTRIBUTION, 2, 2);
      q = SpdExponential.INSTANCE.exp(Transpose.of(a).add(a).multiply(RationalScalar.HALF));
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    ScalarTensorFunction scalarTensorFunction = SpdGeodesic.INSTANCE.curve(p, q);
    for (Tensor _t : Subdivide.of(0, 1, spinnerRefine.getValue())) {
      geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.of(_t.multiply(RealScalar.of(10)), RealScalar.ZERO)));
      new CovarianceRender(scalarTensorFunction.apply(_t.Get())).render(geometricLayer, graphics);
      geometricLayer.popMatrix();
    }
  }

  public static void main(String[] args) {
    new SpdGeodesicDemo().setVisible(1000, 600);
  }
}

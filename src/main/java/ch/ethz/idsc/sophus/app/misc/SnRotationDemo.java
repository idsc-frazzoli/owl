// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.ext.BoundedLinkedList;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.lie.MatrixExp;
import ch.ethz.idsc.tensor.lie.TensorWedge;
import ch.ethz.idsc.tensor.sca.Mod;

public class SnRotationDemo extends AbstractDemo {
  private static final int DIM = 3;
  private static final ColorDataGradient colorDataGradient = //
      ColorDataGradients.JET.deriveWithOpacity(RealScalar.of(0.3));
  private final JButton jButton = new JButton("shuffle");
  private final BoundedLinkedList<Tensor> boundedLinkedList = new BoundedLinkedList<Tensor>(40);
  private Tensor samples;
  private Tensor rotation;

  public SnRotationDemo() {
    jButton.addActionListener(a -> setDimension(DIM));
    timerFrame.jToolBar.add(jButton);
    setDimension(DIM);
  }

  public void setDimension(int dimension) {
    RandomSampleInterface randomSampleInterface = SnRandomSample.of(dimension);
    samples = RandomSample.of(randomSampleInterface, 200);
    Tensor angle = RandomSample.of(randomSampleInterface).multiply(RealScalar.of(0.2));
    rotation = MatrixExp.of(TensorWedge.of(angle, ConstantArray.of(RealScalar.ONE, dimension + 1)));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // graphics.setColor(new Color(128, 128, 128, 64));
    samples = samples.dot(rotation);
    boundedLinkedList.add(samples);
    for (int count = 0; count < samples.length(); ++count) {
      Tensor rgba = colorDataGradient.apply(Mod.function(1).apply(RationalScalar.of(count, 20)));
      Color color = ColorFormat.toColor(rgba);
      int fi = count;
      Tensor trace = Tensor.of(boundedLinkedList.stream().map(p -> p.get(fi)));
      Path2D path2d = geometricLayer.toPath2D(trace);
      graphics.setColor(color);
      graphics.draw(path2d);
    }
  }

  public static void main(String[] args) {
    SnRotationDemo snRotationDemo = new SnRotationDemo();
    snRotationDemo.setVisible(800, 600);
  }
}

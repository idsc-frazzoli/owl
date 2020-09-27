// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.TensorProperties;

/* package */ class GuiExtension extends ControlPointsDemo {
  public Tensor tensor = Tensors.fromString("");

  public GuiExtension() {
    super(true, GeodesicDisplays.R2_ONLY);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // TODO Auto-generated method stub
  }

  public static void main(String[] args) {
    GuiExtension guiExtension = new GuiExtension();
    TensorProperties tensorProperties = TensorProperties.wrap(guiExtension);
    tensorProperties.fields().forEach(System.out::println);
  }
}

// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.IOException;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;

/* package */ enum SunshineDemo {
  ;
  public static void main(String[] args) throws IOException {
    KlotskiProblem klotskiProblem = Sunshine.ORIGINAL.create();
    KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
    Tensor model2Pixel = klotskiDemo.klotskiFrame.timerFrame.geometricComponent.getModel2Pixel();
    klotskiDemo.klotskiFrame.timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(0.4, 0.4, 1).pmul(model2Pixel));
    klotskiDemo.klotskiFrame.timerFrame.configCoordinateOffset(100, 600);
    KlotskiSolution klotskiSolution = klotskiDemo.compute();
    Export.object(KlotskiDemo.solutionFile(klotskiProblem), klotskiSolution);
    klotskiDemo.close();
    KlotskiPlot.export(klotskiSolution);
  }
}

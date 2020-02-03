// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Import;

enum KlotskiSolutions {
  ;
  public static void run() throws IOException {
    for (Huarong huarong : Huarong.values()) {
      KlotskiProblem klotskiProblem = huarong.create();
      File file = KlotskiDemo.solutionFile(klotskiProblem);
      try {
        Import.object(file);
      } catch (Exception exception) {
        KlotskiDemo klotskiDemo = new KlotskiDemo(klotskiProblem);
        KlotskiSolution klotskiSolution = klotskiDemo.compute();
        Export.object(file, klotskiSolution);
        klotskiDemo.close();
      }
    }
  }

  public static void main(String[] args) throws IOException {
    run();
  }
}

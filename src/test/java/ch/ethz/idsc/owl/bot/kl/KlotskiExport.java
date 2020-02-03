// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.NavigableMap;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.io.HtmlUtf8;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.red.Tally;

/* package */ enum KlotskiExport {
  ;
  private static final File ROOT = new File("/media/datahaki/data/public_html/numerics");

  static String imageFilename(String prefix, KlotskiProblem klotskiProblem) {
    return "klotski/" + prefix + "_" + klotskiProblem.name().toLowerCase() + ".png";
  }

  public static void main(String[] args) throws IOException {
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(ROOT, "klotski.htm"))) {
      htmlUtf8.appendln("<table>");
      for (Huarong huarong : Huarong.values()) {
        htmlUtf8.appendln("<tr>");
        KlotskiProblem klotskiProblem = huarong.create();
        {
          KlotskiPlot klotskiPlot = new KlotskiPlot(klotskiProblem, 32);
          BufferedImage bufferedImage = klotskiPlot.plot(klotskiProblem.startState());
          ImageIO.write(bufferedImage, "png", new File(ROOT, imageFilename("beg", klotskiProblem)));
        }
        htmlUtf8.appendln("<td>" + klotskiProblem.name() + "<br/>");
        NavigableMap<Tensor, Long> map = Tally.sorted(klotskiProblem.startState().get(Tensor.ALL, 0));
        htmlUtf8.appendln("" + map.values() + "<br/>");
        htmlUtf8.appendln("<td><img src='" + imageFilename("beg", klotskiProblem) + "'/>");
        try {
          KlotskiSolution klotskiSolution = Import.object(KlotskiDemo.solutionFile(klotskiProblem));
          VisualSet visualSet = new VisualSet();
          visualSet.add(klotskiSolution.domain.get(Tensor.ALL, 0), klotskiSolution.domain.get(Tensor.ALL, 1));
          JFreeChart jFreeChart = ListPlot.of(visualSet);
          String filename = imageFilename("eva", klotskiProblem);
          ChartUtils.saveChartAsPNG(new File(ROOT, filename), jFreeChart, 500, 200);
          htmlUtf8.appendln("<td><img src='" + filename + "'/>");
        } catch (Exception exception) {
          // ---
        }
        htmlUtf8.appendln("</tr>");
      }
      htmlUtf8.appendln("</table>");
    }
  }
}

// code by jph
package ch.ethz.idsc.owl.bot.kl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.io.HtmlUtf8;
import ch.ethz.idsc.owl.data.Lists;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.pdf.BinCounts;

/* package */ enum KlotskiExport {
  ;
  private static final File ROOT = new File("/media/datahaki/data/public_html/numerics");
  private static final int RES = 24;

  static String imageFilename(String prefix, KlotskiProblem klotskiProblem, String extension) {
    return "klotski/" + prefix + "_" + klotskiProblem.name().toLowerCase() + "." + extension;
  }

  public static void main(String[] args) throws IOException {
    KlotskiSolutions.run();
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(ROOT, "klotski.htm"))) {
      htmlUtf8.appendln("<table>");
      htmlUtf8.appendln("<tr>");
      htmlUtf8.appendln("<th>Problem");
      htmlUtf8.appendln("<th>Init<td>");
      htmlUtf8.appendln("<th>Solution<td>");
      htmlUtf8.appendln("<th>Last<td>");
      htmlUtf8.appendln("<th>Expansion Count vs. Leaves and Depth");
      for (Huarong huarong : Huarong.values())
        try {
          KlotskiProblem klotskiProblem = huarong.create();
          KlotskiSolution klotskiSolution = Import.object(KlotskiDemo.solutionFile(klotskiProblem));
          htmlUtf8.appendln("<tr>");
          htmlUtf8.appendln("<td>" + klotskiProblem.name() + "<br/>");
          Tensor binCounts = BinCounts.of(klotskiProblem.startState().get(Tensor.ALL, 0));
          htmlUtf8.appendln(binCounts + "<br/>");
          htmlUtf8.appendln(klotskiSolution.list.size() + "<br/>");
          // ---
          {
            String filename = imageFilename("beg", klotskiProblem, "png");
            BufferedImage bufferedImage = new KlotskiPlot(klotskiProblem, RES).plot(klotskiProblem.startState());
            ImageIO.write(bufferedImage, "png", new File(ROOT, filename));
            htmlUtf8.appendln("<td><img src='" + filename + "'/><td>");
          }
          {
            String filename = imageFilename("ani", klotskiProblem, "gif");
            try (AnimationWriter animationWriter = //
                new GifAnimationWriter(new File(ROOT, filename), 500, TimeUnit.MILLISECONDS)) {
              for (StateTime stateTime : klotskiSolution.list) {
                BufferedImage bufferedImage = new KlotskiPlot(klotskiProblem, RES).plot(stateTime.state());
                animationWriter.write(bufferedImage);
              }
            }
            htmlUtf8.appendln("<td><img src='" + filename + "'/><td>");
          }
          {
            String filename = imageFilename("end", klotskiProblem, "png");
            StateTime stateTime = Lists.getLast(klotskiSolution.list);
            BufferedImage bufferedImage = new KlotskiPlot(klotskiProblem, RES).plot(stateTime.state());
            ImageIO.write(bufferedImage, "png", new File(ROOT, filename));
            htmlUtf8.appendln("<td><img src='" + filename + "'/><td>");
          }
          {
            ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic(); // .deriveWithAlpha(16);
            VisualSet visualSet = new VisualSet(colorDataIndexed);
            Tensor expandCount = klotskiSolution.domain.get(Tensor.ALL, 0);
            visualSet.add(expandCount, klotskiSolution.domain.get(Tensor.ALL, 2));
            visualSet.add(expandCount, klotskiSolution.domain.get(Tensor.ALL, 3));
            JFreeChart jFreeChart = ListPlot.of(visualSet);
            String filename = imageFilename("eva", klotskiProblem, "png");
            ChartUtils.saveChartAsPNG(new File(ROOT, filename), jFreeChart, 500, 130);
            htmlUtf8.appendln("<td><img src='" + filename + "'/>");
          }
          htmlUtf8.appendln("</tr>");
        } catch (Exception exception) {
          // ---
        }
      htmlUtf8.appendln("</table>");
    }
  }
}

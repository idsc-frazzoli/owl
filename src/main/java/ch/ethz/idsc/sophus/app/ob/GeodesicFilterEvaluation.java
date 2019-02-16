// code by ob
package ch.ethz.idsc.sophus.app.ob;

import java.io.IOException;

import ch.ethz.idsc.sophus.math.SmoothingKernel;
import ch.ethz.idsc.subare.util.HtmlUtf8;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.HomeDirectory;

public class GeodesicFilterEvaluation {
  GeodesicFilterEvaluation() {
  }

  public static Tensor process(String data) throws IOException {
    ParameterMinimizer parameterMinimizer = new ParameterMinimizer();
    Tensor minimizer = parameterMinimizer.minimizer(data);
    GeodesicCurveEvaluation geodesicCurveEvaluation = new GeodesicCurveEvaluation(minimizer, data);
    geodesicCurveEvaluation.windowSizeCurves();
    geodesicCurveEvaluation.alphaCurves();
    return minimizer;
  }

  public static void htmlWriter(String data, Tensor minimizer) {
    // TODO OB remove
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(HomeDirectory.Pictures(data.replace('/', '_') + ".html"))) {
      Tensor minimizingAlphas = minimizer.get(0);
      Tensor minimizingWindowSizes = minimizer.get(1);
      Tensor minimizingKernels = minimizer.get(2);
      Tensor minimizingErrors = minimizer.get(3);
      htmlUtf8.appendln("<p>Suggested Filter parameters for this dataset:</p>");
      htmlUtf8.appendln("<table>");
      htmlUtf8.appendln("<tr><th>_</th><th>Minimizing alpha</th><th>Minimizing WindowSize</th><th>Minimizing Kernel</th><th>Minimizing Erros</th></tr>  ");
      htmlUtf8.appendln("<tr><th>Pose</th><th>" + minimizingAlphas.Get(0) + "</th><th>" + minimizingWindowSizes.Get(0) + "</th><th>"
          + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(0))] + "</th><th>" + minimizingErrors.Get(0) + " </th></tr>");
      htmlUtf8.appendln("<tr><th>Orientation</th><th>" + minimizingAlphas.Get(1) + "</th><th>" + minimizingWindowSizes.Get(1) + "</th><th>"
          + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(1))] + "</th><th>" + minimizingErrors.Get(1) + "</th></tr>  ");
      htmlUtf8.appendln("<tr><th>Pose change</th><th>" + minimizingAlphas.Get(2) + "</th><th>" + minimizingWindowSizes.Get(2) + "</th><th>"
          + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(2))] + "</th><th>" + minimizingErrors.Get(2) + "</th></tr>  ");
      htmlUtf8.appendln("<tr><th>Orientation change</th><th>" + minimizingAlphas.Get(3) + "</th><th>" + minimizingWindowSizes.Get(3) + "</th><th>"
          + SmoothingKernel.values()[Scalars.intValueExact(minimizingKernels.Get(3))] + "</th><th>" + minimizingErrors.Get(3) + "</th></tr>  ");
      htmlUtf8.appendln("</table>");
      String name = "GeodesicFilterEvaluation";
      String win = "windowSizeCurves";
      String alpha = "alphaCurves";
      htmlUtf8.appendln("<p>Alpha varying plots::</p>");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Pose:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_x.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Orientation:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_a.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Pose change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_xdot.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Orientation change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + alpha + "_adot.png' />");
      htmlUtf8.appendln("<p>Window varying plots::</p>");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Pose:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_x.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Orientation:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_a.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Pose change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_xdot.png' />");
      htmlUtf8.appendln("<p>Dependence of filter error on window size - Orientation change:</p>");
      htmlUtf8.appendln("<img src='" + name + data.replace('/', '_') + "_" + win + "_adot.png' />");
    }
  }

  public static void main(String[] args) throws IOException {
    String control = "0w/20180702T133612_2";
    Tensor minimizer = GeodesicFilterEvaluation.process(control);
    GeodesicFilterEvaluation.htmlWriter(control, minimizer);
  }
}

package ch.ethz.idsc.owl.symlink;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicCenter;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sig.WindowFunctions;

public enum SymGen {
  ;
  public static void render_bin3() throws IOException {
    int radius = 3;
    Function<Integer, Tensor> function = WindowFunctions.BINOMIAL;
    // ---
    SymLinkImage symLinkImage = new SymLinkImage();
    TensorUnaryOperator tensorUnaryOperator = //
        GeodesicCenter.of(SymGeodesic.INSTANCE, function);
    Tensor vector = Tensor.of(IntStream.range(0, 2 * radius + 1).mapToObj(SymScalar::of));
    Tensor tensor = tensorUnaryOperator.apply(vector);
    SymLink root = SymLink.build((SymScalar) tensor);
    symLinkImage.renderRoot(root);
    symLinkImage.renderTops(function.apply(radius));
    ImageIO.write(symLinkImage.bufferedImage(), "png", UserHome.Pictures("export/binomial3.png"));
  }

  public static void main(String[] args) throws IOException {
    render_bin3();
  }
}

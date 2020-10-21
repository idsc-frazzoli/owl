// code by jph
package ch.ethz.idsc.sophus.app.ubo;

import java.io.File;
import java.util.List;
import java.util.function.Function;

import ch.ethz.idsc.tensor.ext.Cache;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum UbongoLoader {
  INSTANCE;

  private final Function<UbongoBoards, List<List<UbongoEntry>>> cache = Cache.of(this::of, 64);

  public List<List<UbongoEntry>> load(UbongoBoards ubongoBoards) {
    return cache.apply(ubongoBoards);
  }

  private List<List<UbongoEntry>> of(UbongoBoards ubongoBoards) {
    File folder = HomeDirectory.Documents("ubongo");
    folder.mkdir();
    File file = new File(folder, ubongoBoards.name());
    if (file.isFile())
      try {
        return Import.object(file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    System.out.println("compute");
    List<List<UbongoEntry>> list = ubongoBoards.solve();
    try {
      Export.object(file, list);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return list;
  }
}

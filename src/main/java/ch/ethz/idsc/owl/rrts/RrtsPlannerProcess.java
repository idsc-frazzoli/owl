// code by gjoel
package ch.ethz.idsc.owl.rrts;

@FunctionalInterface
public interface RrtsPlannerProcess {
  void run(int steps) throws Exception;
}

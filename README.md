# ch.ethz.idsc.owl

<a href="https://travis-ci.org/idsc-frazzoli/owl"><img src="https://travis-ci.org/idsc-frazzoli/owl.svg?branch=master" alt="Build Status"></a>

The repository contains Java 8 implementations of motion planners and their variants.

Version `0.0.2`

The code in the `owl` repository operates a heavy and fast robot that may endanger living creatures.
We follow best practices and coding standards to protect from avoidable errors.

List of algorithms:

* [GLC](src/main/java/ch/ethz/idsc/owl/glc/std/StandardTrajectoryPlanner.java)
* [RRT*](src/main/java/ch/ethz/idsc/owl/rrts/core/DefaultRrts.java)

The references are

* *A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning*
by Brian Paden and Emilio Frazzoli, 
[arXiv:1607.06966](https://arxiv.org/abs/1607.06966)
* *Sampling-based algorithms for optimal motion planning*
by Sertac Karaman and Emilio Frazzoli,
[IJRR11](http://ares.lids.mit.edu/papers/Karaman.Frazzoli.IJRR11.pdf)

The following integrators are available:

* Euler, Midpoint
* Runge-Kutta 4th order, and 5th order
* exact integrator for the group SE2

The `owl` repository implements visualizations in 2D as showcased below.
See also a [video](https://www.youtube.com/watch?v=lPQW3GqQqSY).

The separate repository [owly3d](https://github.com/idsc-frazzoli/owly3d) implements animations and visualizations in 3D.


## Examples

### GLC

Rice2: 4-dimensional state space + time

<table>
<tr>
<td>

![rice2dentity_1510227502495](https://user-images.githubusercontent.com/4012178/32603926-dd317aea-c54b-11e7-97ab-82df23b52fa5.gif)

<td>

![rice2dentity_1510234462100](https://user-images.githubusercontent.com/4012178/32608146-b6106d1c-c55b-11e7-918d-e0a1d1c8e400.gif)

</tr>
</table>

---

SE2: 3-dimensional state space

<table>
<tr>
<td>

Car

![se2entity_1510232282788](https://user-images.githubusercontent.com/4012178/32606961-813b05a6-c557-11e7-804c-83b1c5e94a6f.gif)

<td>

Two-wheel drive (with Lidar simulator)

![twdentity_1510751358909](https://user-images.githubusercontent.com/4012178/32838106-2d88fa2c-ca10-11e7-9c2a-68b34b1717cc.gif)

</tr>
</table>

### RRT*

R^2

![r2ani](https://cloud.githubusercontent.com/assets/4012178/26282173/06dccee8-3e0c-11e7-930f-fedab34fe396.gif)

![r2](https://cloud.githubusercontent.com/assets/4012178/26045794/16bd0a54-394c-11e7-9d11-19558bc3be88.png)

## Include in your project

Modify the `pom` file of your project to specify `repository` and `dependency` of the tensor library:

    <repositories>
      <repository>
        <id>owl-mvn-repo</id>
        <url>https://raw.github.com/idsc-frazzoli/owl/mvn-repo/</url>
        <snapshots>
          <enabled>true</enabled>
          <updatePolicy>always</updatePolicy>
        </snapshots>
      </repository>
    </repositories>
    
    <dependencies>
      <dependency>
        <groupId>ch.ethz.idsc</groupId>
        <artifactId>owl</artifactId>
        <version>0.0.2</version>
      </dependency>
    </dependencies>

## References

The library is used in the projects:
* `retina`
* `owly3d`
* `matsim`

The repository has over `300` unit tests.

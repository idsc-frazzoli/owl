# ch.ethz.idsc.owl <a href="https://travis-ci.org/idsc-frazzoli/owl"><img src="https://travis-ci.org/idsc-frazzoli/owl.svg?branch=master" alt="Build Status"></a>

Library for motion planning in Java, version `0.2.2`

The library was developed with the following objectives in mind
* suitable for use in safety-critical real-time systems
* trajectory planning for an autonomous vehicle
* implementation of theoretical concepts with high level of abstraction
* simulation and visualization

<table>
<tr>
<td>

![usecase_motionplan](https://user-images.githubusercontent.com/4012178/35968244-96577dee-0cc3-11e8-80a1-b38691e863af.png)

Motion planning

<td>

![shadow_regions](https://user-images.githubusercontent.com/4012178/42315433-b53034de-8047-11e8-8fc2-87fa504460c5.png)

Obstacle anticipation

<td>

![usecase_gokart](https://user-images.githubusercontent.com/4012178/35968269-a92a3b46-0cc3-11e8-8d5e-1276762cdc36.png)

[Trajectory pursuit](https://www.youtube.com/watch?v=XgmS8CP6gqw)

<td>

![planning_obstacles](https://user-images.githubusercontent.com/4012178/40268689-2af06cd4-5b72-11e8-95cf-d94edfdc3dd1.png)

[Static obstacles](https://www.youtube.com/watch?v=xLZeKFeAokM)

</tr>
</table>

## Features

* Motion planning algorithms: [GLC](src/main/java/ch/ethz/idsc/owl/glc/std/StandardTrajectoryPlanner.java), and [RRT*](src/main/java/ch/ethz/idsc/owl/rrts/core/DefaultRrts.java)
* integrators: Euler, Midpoint, Runge-Kutta 4-5th order, exact integrator for the group SE2
* state-space models: car-like, two-wheel-drive, pendulum-swing-up, Lotka-Volterra, etc.
* efficient heuristic for goal regions: sphere, conic section
* refinement of waypoint sequence using non-linear curve subdivision along geodesics
* visualizations and animations, see [video](https://www.youtube.com/watch?v=lPQW3GqQqSY)

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

---

Simulation: autonomous gokart or car

<table>
<tr>
<td>

Gokart

![_1530775215911](https://user-images.githubusercontent.com/4012178/42308510-10283bf0-8036-11e8-8a42-b8f1f807bb88.gif)

<td>

Car

![_1530775403211](https://user-images.githubusercontent.com/4012178/42308523-1ae4ea8e-8036-11e8-8067-83bdd67a2d33.gif)

</tr>
</table>


### RRT*

R^2

![r2ani](https://cloud.githubusercontent.com/assets/4012178/26282173/06dccee8-3e0c-11e7-930f-fedab34fe396.gif)

![r2](https://cloud.githubusercontent.com/assets/4012178/26045794/16bd0a54-394c-11e7-9d11-19558bc3be88.png)

## Integration

Specify `repository` and `dependency` of the owl library in the `pom.xml` file of your maven project:

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
        <version>0.2.2</version>
      </dependency>
    </dependencies>

## Contributors

Jan Hakenberg, Jonas Londschien, Yannik Nager

> The code in the repository operates a heavy and fast robot that may endanger living creatures. We follow best practices and coding standards to protect from avoidable errors.

## References

* *A Generalized Label Correcting Method for Optimal Kinodynamic Motion Planning*
by Brian Paden and Emilio Frazzoli, 
[arXiv:1607.06966](https://arxiv.org/abs/1607.06966)
* *Sampling-based algorithms for optimal motion planning*
by Sertac Karaman and Emilio Frazzoli,
[IJRR11](http://ares.lids.mit.edu/papers/Karaman.Frazzoli.IJRR11.pdf)
* *Curve Subdivision in SE(2)*
by Jan Hakenberg,
[viXra:1807.0463](http://vixra.org/abs/1807.0463)

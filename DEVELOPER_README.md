# Java Configuration

The 'client' subproject tends to be set to Java release 1.7. It is better to reset the Java Build Path to point to a local Java 8 (or better) JRE. Once you do that, you should also set the corresponding Project Facet to point to 1.8 (or better) as well.

# Gradle Configuration and Library Upgrades

We periodically upgrade the versions of the various components of the KBA system. Sometimes, a cascade of upgrades in dependencies are needed. Our recent upgrade to the Neo4j (to 3.3.3) and associated Spring framework libraries is one such case. In effect, we also needed to upgrade our Gradle build tool concurrently. 

This latest version of the KBA may be built under Eclipse using the Buildship Gradle plugin, but special attention must be given to use the latest Buildship version - currently, 2.2.1, as this text is being written - so that the Gradle version - currently 4.5.1 as this is written - can properly set.  Older versions of buildship plugin were a bit rigid in taking the 3.5 version of Gradle as a default, which is problematic for our latest Gradle build strategy (and latest Spring et al. libraries used).

If necessary, you may upgrade your buildship ot the latest version from the [Buildship Project Web site](https://projects.eclipse.org/projects/tools.buildship). Sometimes, the Eclipse Marketplace is a bit behind in publishing such updates, so you may need to use the more traditional Eclipse Update mechanism (with URL as given on the Buildship web site) to conduct the upgrade.

It is generally a good idea to often refresh your code tree (in Project Explorer context menu) and your Gradle dependencies (the 'Gradle..Refresh Gradle Project' context manu item), in particular, after switching branches.



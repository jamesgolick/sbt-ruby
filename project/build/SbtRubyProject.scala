import sbt._

class SbtRuby(info: ProjectInfo) extends sbt.PluginProject(info) with rsync.RsyncPublishing {
  val liftUtil = "net.liftweb" % "lift-util_2.8.0" % "2.1-M1" withSources()
  val logula   = "com.codahale" %% "logula" % "1.0.3" withSources()
  
  /**
   * Borrowed these from codahale/cassie
   */

  /**
   * Include docs and source as build artifacts.
   */
  override def packageSrcJar = defaultJarPath("-sources.jar")
  val sourceArtifact = sbt.Artifact(artifactID, "src", "jar", Some("sources"), Nil, None)
  override def packageToPublishActions = super.packageToPublishActions ++ Seq(packageSrc, `package`)

  /**
   * Publish via rsync.
   */
  def rsyncRepo = "james@jamesgolick.com:/var/www/repo.jamesgolick.com"
}

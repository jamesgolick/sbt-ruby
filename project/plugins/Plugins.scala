class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
  val codasRepo = "codahale.com" at "http://repo.codahale.com/"
  val rsync = "com.codahale" % "rsync-sbt" % "0.1.1"
}

import sbt._

class SbtRuby(info: ProjectInfo) extends sbt.PluginProject(info) with rsync.RsyncPublishing {
  /**
   * Publish via rsync.
   */
  def rsyncRepo = "james@jamesgolick.com:/var/www/repo.jamesgolick.com"
}

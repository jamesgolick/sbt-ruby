package ruby

import java.io.File
import sbt._
import Process._

trait GemBuilding extends DefaultProject {
  lazy val rubyTargetDir   = info.projectPath / "target" / "ruby"
  lazy val gemStagingDir   = rubyTargetDir / "gem"
  lazy val gemName         = projectName.get.get
  lazy val gemspecFilename = "%s.gemspec".format(gemName)
  val rubyMainDir          = "src" / "main" / "ruby"
  val rubyTestDir          = "src" / "test" / "ruby"
  val gemVersion           = "0.0.1"
  val gemDependencies      = Map[String, String]()

  val gemAuthor:       String
  val gemAuthorEmail:  String

  lazy val rubyTest = execTask {
    val pb   = new java.lang.ProcessBuilder("spec", rubyTestDir.toString)
    pb.environment.put("RUBYOPT", "-rubygems -I%s -I%s".format(rubyMainDir, outputPath))
    pb
  } dependsOn(`package`) describedAs("Run the ruby specs.")

  lazy val cleanGem = cleanTask(gemStagingDir) describedAs("Clean the gem staging directory.")

  lazy val setupGemFiles = task {
    emitGemspec.run

    val gemLibDir     = gemStagingDir / "lib"
    val gemLibDirFile = new File(gemLibDir.toString)

    if (!gemLibDirFile.exists) { gemLibDirFile.mkdirs }

    FileUtilities.copy(descendents(rubyMainDir ##, "*.rb").get, gemLibDir, log)
    FileUtilities.copy(List(info.projectPath / "%s.gemspec".format(gemName)), gemStagingDir, log)
    None
  } dependsOn(rubyTest, cleanGem)

  lazy val buildGem = execTask {
    val pb = new java.lang.ProcessBuilder("gem", "build", gemspecFilename)
    pb.directory(new File(gemStagingDir.toString))
    pb
  } dependsOn(setupGemFiles) describedAs("Builds the gem.")

  lazy val installGem = execTask {
    val pb = new java.lang.ProcessBuilder("gem", "install", "%s-%s.gem".format(gemName, gemVersion))
    pb.directory(new File(gemStagingDir.toString))
    pb
  } dependsOn(buildGem) describedAs("Installs the gem.")

  lazy val publishGem = execTask {
    val pb = new java.lang.ProcessBuilder("gem", "push", "%s-%s.gem".format(gemName, gemVersion))
    pb.directory(new java.io.File(gemStagingDir.toString))
    pb
  } dependsOn(buildGem) describedAs("Publishes the gem using `gem push`")

  lazy val emitGemspec = task {
    val gemspecFile = new File((gemStagingDir / gemspecFilename).toString)
    FileUtilities.write(gemspecFile, buildGemspec, log)
  }

  lazy val printGemspec = task {
    println(buildGemspec)
    None
  } describedAs("Print the gemspec to STDOUT.")

  private def buildGemspec: String = {
    val dependencies = gemDependencies.map { case (name, version) =>
      """  s.add_dependency("%s", "%s")""".format(name, version)
    }.mkString("\n")
    """Gem::Specification.new do |s|
  s.name    = "%s"
  s.version = "%s"

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["%s"]
  s.date    = "2010-08-31"
  s.description = ""
  s.email = "%s"
  s.files = Dir["lib/**/*.rb"] + ["%s"]
  s.homepage = ""
  s.rdoc_options = ["--charset=UTF-8"]
  s.require_paths = ["lib"]
  s.rubygems_version = "1.3.7"
  s.summary = ""
%s
end
""".format(gemName, gemVersion, gemAuthor, gemAuthorEmail, gemspecFilename, dependencies)
  }
}

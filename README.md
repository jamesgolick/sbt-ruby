sbt-ruby
========

An sbt plugin for running ruby specs and building rubygems as part of your sbt build.

## How it works

Depend on sbt-ruby in project/plugins/Plugins.scala:

    class Plugins(info: sbt.ProjectInfo) extends sbt.PluginDefinition(info) {
      val jamesgolickRepo = "James Golick's Repository" at "http://repo.jamesgolick.com/"
      val sbtRuby         = "com.bitlove" % "sbt-ruby" % "1.0.6"
    }
    
Then, add the GemBuilding trait to your project definition. At the very least, you have to define gemAuthor and gemAuthorEmail.

    class MyAmazingProject extends sbt.DefaultProject(info) with ruby.GemBuilding {
      val gemAuthor	 = "James Golick"
      val gemAuthorEmail = "jamesgolick@gmail.com"
    }

Now, if you have ruby source code in src/main/ruby and specs in src/test/ruby, you can use a variety of sbt commands to do various things:

  * sbt ruby-test # runs the ruby specs
  * sbt build-gem # builds the gem and leaves it in target/ruby/gem
  * sbt install-gem # installs the gem if the tests pass
  * sbt printGemspec # prints the gemspec that'll be used to STDOUT

There are a bunch of default settings that you can override:

  * gemName: String // defaults to the name of the sbt project as defined in build.properties
  * gemVersion: String // defaults to 0.0.1
  * gemDependencies: Map[String, String] // defaults to empty. The format is "gemname" -> "version".

## Copyright

Copyright (c) 2010 James Golick, Protose Inc. See LICENSE for details.

name := "snowflake_id_worker"

organization := "com.rikima"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "twitter-repo" at "https://maven.twttr.com"
resolvers += "maven-central" at "http://central.maven.org"

libraryDependencies += "log4j" % "log4j" % "1.2.17"
libraryDependencies += "org.specs2" %% "specs2-core" % "3.6.6" % "test"
libraryDependencies += "junit" % "junit" % "4.12"
libraryDependencies += "com.twitter" % "ostrich_2.11" % "9.26.0"

name := "Sparkadabra"

version := "1.0"

scalaVersion in ThisBuild := "2.11.8"

crossScalaVersions in ThisBuild := Seq(scalaVersion.value)

resolvers in ThisBuild ++= Seq(
  "sonatype-oss" at "http://oss.sonatype.org/content/repositories/snapshots",
  "OSS" at "http://oss.sonatype.org/content/repositories/releases",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Apache Repo" at "https://repository.apache.org/content/repositories/releases/"
)

val sparkV = "2.0.2"

val jacksonV = "2.5.2"

libraryDependencies in ThisBuild ++= Seq(
  "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonV,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonV,
  "org.apache.spark" %% "spark-core" % sparkV,
  "org.apache.spark" %% "spark-streaming" % sparkV,
  "org.apache.spark" %% "spark-mllib" % sparkV,
  "org.apache.spark" %% "spark-hive" % sparkV,
  "org.json4s" %% "json4s-jackson" % "{latestVersion}",
  "org.scalatest" % "scalatest_2.11" % "2.2.5" % "test",
  "javax.servlet" % "javax.servlet-api" % "3.0.1"
).map(_.exclude("org.mortbay.jetty", "servlet-api"))
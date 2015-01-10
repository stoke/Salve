name := "Salve"

version := "1.0"

scalaVersion := "2.11.4"

resolvers += "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/com/typesafe/akka/"

libraryDependencies ++= Seq(
  "com.skadistats" % "clarity" % "1.1",
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

testOptions in Test += Tests.Argument("-oDF")

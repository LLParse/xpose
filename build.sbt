
name := "xpose"

version := "0.0.0.1"

scalaVersion := "2.10.3"

mainClass := Some("com.zarniwoop.xpose.HTTPServer")

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Twitter Repo" at "http://maven.twttr.com"

libraryDependencies ++= Seq(
  "com.datastax.cassandra" % "cassandra-driver-core" % "2.0.1",
  "com.typesafe.akka" %% "akka-actor" % "2.2.3",
  "com.twitter" % "finatra" % "1.4.0",
  "log4j" % "log4j" % "1.2.17",
  "dnsjava" % "dnsjava" % "2.1.6"
)

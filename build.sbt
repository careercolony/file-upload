lazy val root = (project in file(".")).enablePlugins(JavaAppPackaging)
name := "file-upload-api"

packageName in Docker := "careercolony/akka-http-docker-file-upload-api"

dockerExposedPorts := Seq(80)

version := "1.2"

scalaVersion := "2.11.12"

//val akkaV = "2.4.5"
val akkaV = "2.4.11"
libraryDependencies ++= Seq (
  "org.neo4j.driver" % "neo4j-java-driver" % "1.0.4",
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-http"  % "10.0.7",
  "com.typesafe.akka" %% "akka-http-core" % "10.0.7",
  "com.typesafe.akka" %% "akka-http-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "com.typesafe.akka" %% "akka-http-testkit-experimental" % "1.0",
  "ch.megard" %% "akka-http-cors" % "0.1.8",
  "com.jason-goodwin" %% "authentikat-jwt" % "0.4.5"
)

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.12.7"
)

libraryDependencies += "com.typesafe.akka" %% "akka-stream-kafka" % "0.18"

//libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.5"
//libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.5"
//libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.5"
//libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.13"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.5"
libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.5"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.13"

// elastic search
libraryDependencies += "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "0.19"

//libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.1"
//libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.1"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.0.3"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers += "Typesafe" at "https://repo.typesafe.com/typesafe/releases/"

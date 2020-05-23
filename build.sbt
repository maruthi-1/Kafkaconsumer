
name := "Myconsumer"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += Resolver.mavenCentral

libraryDependencies += "org.apache.flink" %% "flink-connector-elasticsearch7" % "1.10.0"
libraryDependencies += "org.apache.flink" %% "flink-streaming-scala" % "1.10.0"
libraryDependencies += "org.apache.flink" %% "flink-connector-kafka" % "1.10.0"
// https://mvnrepository.com/artifact/org.apache.flink/flink-core
libraryDependencies += "org.apache.flink" % "flink-core" % "1.10.0"
// https://mvnrepository.com/artifact/org.apache.flink/flink-scala
libraryDependencies += "org.apache.flink" %% "flink-scala" % "1.10.0"

// Override the transitive dependencies
libraryDependencies ++= Seq(
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.3",
  "com.fasterxml.jackson.core" % "jackson-core" % "2.10.3",
  "com.fasterxml.jackson.core" % "jackson-annotations" % "2.10.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.3"
)

// Set the default classpath
run in Compile := Defaults.runTask(fullClasspath in Compile,
  mainClass in (Compile, run),
  runner in (Compile,run)
).evaluated

// Added to remove sbt-assembly duplicate errors while merging
//assemblyMergeStrategy in assembly := {
//  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
//  case "reference.conf" => MergeStrategy.concat
//  case "log4j.properties" => MergeStrategy.last
//  case x => MergeStrategy.first
//}
//assemblyOption in assembly :=  (assemblyOption in assembly).value.copy(includeScala = false)
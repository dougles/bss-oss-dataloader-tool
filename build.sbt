
organization := "max2.com"

name := "bss-boss-dataloader-tool"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.6"

mainClass in run := Some("Main")


libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5.3"
libraryDependencies += "com.google.inject" % "guice" % "4.1.0"
libraryDependencies += "org.scala-sbt" % "util-logging_2.10" % "1.0.0"
libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.9.1"
libraryDependencies += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.9.2"
// https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.9.0.1"
libraryDependencies += "org.apache.avro" % "avro" % "1.8.2"
// https://mvnrepository.com/artifact/log4j/log4j
libraryDependencies += "log4j" % "log4j" % "1.2.17"
// https://mvnrepository.com/artifact/com.jcabi/jcabi-log
libraryDependencies += "com.jcabi" % "jcabi-log" % "0.17.2"




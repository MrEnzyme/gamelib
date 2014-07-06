name := "gamelib"

version := "1.0"

scalaVersion := "2.11.1"

resolvers += "clojars" at "http://clojars.org/repo"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "tv.cntt" %% "chill-scala" % "1.1",
    "org.reflections" % "reflections" % "0.9.9-RC1"
)
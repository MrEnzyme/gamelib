name := "gamelib"

version := "1.0"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "2.2.0" % "test",
    "tv.cntt" %% "chill-scala" % "1.1",
    "org.reflections" % "reflections" % "0.9.9-RC1",
    "com.esotericsoftware" % "kryonet" % "2.22.0-RC1"
)
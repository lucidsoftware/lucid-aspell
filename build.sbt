import java.io.File

enablePlugins(JniPlugin)

lazy val Native = config("native").extend(Compile)

lazy val aspell = (project in file("."))
  .configs(Native)
  .enablePlugins(BuildInfoPlugin)

inConfig(Native)(Defaults.configSettings ++ Defaults.packageConfig)

addArtifact(artifact in (Native, packageBin), packageBin in Native)

artifactClassifier in (Native, packageBin) := Some("x86_64")

//TODO: add os detection to system loader and prefix these keys with os?
buildInfoKeys := Seq[BuildInfoKey](
  //TODO: replace with jniFullLibraryName when next version is released
  "libraryName" -> s"lib${jniLibraryName.value}.${jniLibSuffix.value}"
)

buildInfoPackage := "com.lucidchart.aspell"

crossScalaVersions := Seq("2.10.6", "2.11.7")

credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  Option(System.getenv("SONATYPE_USERNAME")).getOrElse(""),
  Option(System.getenv("SONATYPE_PASSWORD")).getOrElse("")
)

dependencyClasspath in Test ++= (exportedProducts in Native).value

exportJars := true

//TODO: ld: unknown option: --no-as-needed
// Had to remove this one. I'm not familiar with this flag.
jniIncludes ++= Seq("-Wl,-rpath,/usr/lib -Wl,-rpath,/usr/local/lib -L/usr/lib -L/usr/local/lib -laspell")

jniLibraryName := s"lucidaspell-${version.value}"

jniNativeClasses := Seq(
  "com.lucidchart.aspell.Aspell"
)

libraryDependencies ++= Seq(
  "com.jsuereth" %% "scala-arm" % "1.4",
  "commons-io" % "commons-io" % "2.4",
  "org.specs2" %% "specs2-core" % "2.4.17" % Test
)

managedResourceDirectories in Native += jniBinPath.value

name := "lucid-aspell"

organization := "com.lucidchart"

pomExtra := {
  <developers>
    <developer>
      <name>Lucid Software</name>
      <email>github@lucidchart.com</email>
      <organization>Lucid Software, Inc.</organization>
      <organizationUrl>https://www.golucid.co/</organizationUrl>
    </developer>
  </developers>
    <licenses>
      <license>
        <name>Apache 2</name>
        <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:github.com/lucidsoftware/lucid-aspell</connection>
      <developerConnection>scm:git:git@github.com:lucidsoftware/lucid-aspell</developerConnection>
      <url>https://github.com/lucidsoftware/lucid-aspell</url>
    </scm>
    <url>https://github.com/lucidsoftware/lucid-aspell</url>
}

resourceGenerators in Native +=
  Def.task {
    jniBinPath.value.***.filter(_.isFile).get
  }
    .dependsOn(jniCompile)
    .taskValue

jniNativeCompiler := "g++"

scalaVersion := "2.11.7"

version := "2.1.0-SNAPSHOT"

pomIncludeRepository := { _ => false }

pgpPassphrase := Some(Array())

pgpPublicRing := file(System.getProperty("user.home")) / ".pgp" / "pubring"

pgpSecretRing := file(System.getProperty("user.home")) / ".pgp" / "secring"

publishMavenStyle := true

credentials += Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", System.getenv("SONATYPE_USERNAME"), System.getenv("SONATYPE_PASSWORD"))

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

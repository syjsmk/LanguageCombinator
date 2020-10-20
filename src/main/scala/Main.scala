import ResourceManager.{ResourceReader, ResourceWriter}
import cats.effect._
import cats.implicits.{catsSyntaxTuple4Semigroupal, toFoldableOps}
import org.rogach.scallop.{ScallopConf, ScallopOption}
import fs2.{Pure, Stream}

class Conf(arguments: Seq[String]) extends ScallopConf(arguments) {

  val firstAlphabetPath: ScallopOption[String] = opt[String](required = true)
  val firstVoicePath: ScallopOption[String] = opt[String](required = true)
  val secondAlphabetPath: ScallopOption[String] = opt[String](required = true)
  val secondVoicePath: ScallopOption[String] = opt[String](required = true)
  val outputPath: ScallopOption[String] = opt[String](required = true, default = Some("output/combined_alphabet.txt"))

  verify()
}

object Main extends IOApp {

  val resourceReader = new ResourceReader()
  val resourceWriter = new ResourceWriter()
  
  override def run(args: List[String]): IO[ExitCode] = {

    IO {
      val conf = new Conf(args)
//      val combinedAlphabets: Stream[IO, String] = resourceReader.getCombinedAlphabets(conf.firstAlphabetPath(), conf.secondAlphabetPath())

      val program = for {
        combinedAlphabets <- resourceReader.getCombinedAlphabets(conf.firstAlphabetPath(), conf.secondAlphabetPath()).compile.toList
//        _ = println(combinedAlphabets)
        _ = resourceWriter.write(combinedAlphabets, conf.outputPath() + "/combined_alphabet.txt").compile.drain.unsafeRunSync()
      } yield ()
      program.unsafeRunSync()

    }.as(ExitCode.Success)
  }
}
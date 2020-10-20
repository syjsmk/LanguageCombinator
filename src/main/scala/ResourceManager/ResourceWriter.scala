package ResourceManager

import java.nio.file.Paths

import cats.effect._
import fs2._

class ResourceWriter {

  def write(data: Stream[IO, String], outputPath: String)(implicit cs: ContextShift[IO]): Stream[IO, Unit] = {
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      data
        .map(line => line + "\n")
        .through(text.utf8Encode)
        .through(io.file.writeAll[IO](Paths.get(outputPath), blocker))
    }
  }
  
  def write(data: List[String], outputPath: String)(implicit cs: ContextShift[IO]): Stream[IO, Unit] = {
    Stream.resource(Blocker[IO]).flatMap { blocker =>
      Stream.emits(data)
        .map(line => line + "\n")
        .through(text.utf8Encode)
        .through(io.file.writeAll[IO](Paths.get(outputPath), blocker))
    }
  }
  
}

package ResourceManager

import java.nio.file.{Path, Paths}

import cats.effect.{Blocker, ContextShift, IO}
import fs2.{Chunk, Stream, io, text}
import cats.effect.IO.contextShift


/**
 * input
 * --first-alphabet-path resources/alphabet.txt
 * --first-voice-path /Users/mz01-soyj/Documents/cv-corpus-5.1-2020-06-22/en/clips
 * --first-csv-path /Users/mz01-soyj/Documents/cv-corpus-5.1-2020-06-22/en/clips
 * --second-alphabet-path resources/deepspeech_alphabet.txt
 * --second-voice-path /workspace/ksponspeech/
 * --second-csv-path /workspace/text_data_creator_for_ds1/output/corpora/ko/clips
 * --output-path output
 * 
 * output
 * dev.csv, other.csv, test.csv, train-all.csv, train.csv, validated.csv 를 생성해야 함
 * dev.csv, test.csv, train.csv 에 값이 있어야 함
 *
 */
class ResourceReader(implicit contextShift: ContextShift[IO]) {

  val chunkSize = 4096

  def readChars(path: String): Stream[IO, String] = {
//    println(path)

    Stream.resource(Blocker[IO]).flatMap { blocker =>
      io.file.readAll[IO](Paths.get(path), blocker, this.chunkSize)
        .through(text.utf8Decode)
        .through(text.lines)
        .filter(line => line != "")
    }
  }

  def getCombinedAlphabets(firstAlphabetPath: String, secondAlphabetPath: String): Stream[IO, String] = {
    this.readChars(firstAlphabetPath) ++ this.readChars(secondAlphabetPath)
  }
  
  

}
package com.lucidchart.aspell

import org.specs2.mutable._
import org.specs2.matcher.{Matcher, AlwaysMatcher}

class AspellSpec extends Specification {
  private def wordSuggestions(word: String, valid: Boolean, matchSuggestions: Matcher[Traversable[String]] = AlwaysMatcher()): Matcher[WordSuggestions] = {
    beLike {
      case WordSuggestions(`word`, `valid`, suggs) => suggs.toSeq must matchSuggestions
    }
  }

  private def wordFound(word: String): Matcher[WordSuggestions] = {
    beLike {
      case WordSuggestions(`word`, true, _) => ok
    }
  }

  "Aspell" should {

    "work" in {
      Aspell.check("", Array(), Array())
      ok
    }


    "treat non-ASCII apostrophes as apostrophes" in {
      Aspell.check("en", Array(
        "I've",
        "I\u2019ve",
        "I\u02bcve",
        "I\u055ave",
        "I\uff07ve"), Array()).toSeq must contain(exactly(
          wordFound("I've"),
          wordFound("I\u2019ve"),
          wordFound("I\u02bcve"),
          wordFound("I\u055ave"),
          wordFound("I\uff07ve")
        ))

      Aspell.check("en", Array("I\u2019v"), Array()).toSeq must contain(
        wordSuggestions("I\u2019v", false, contain("I've"))
      )

      Aspell.check("en", Array("foo\u2019bar"), Array("foo'bar")).toSeq must contain(exactly(
        wordFound("foo\u2019bar")
      ))
    }

  }

}

package com.helperhub.pdfmash

import scala.util.{Failure, Success, Try}

/**
  * Created by joel on 2017-07-28.
  */
object Utils {
  /**
    * Parse a sequence of integers out of a range expression.
    *
    * @param string the expression to parse
    *
    * @return a sequence of integers extracted from the expression
    */
  def parseRange(string: String): Seq[Int] = {
    Try {
      string split "," flatMap { (s) =>
        val r = """(-?\d+)(?:-(-?\d+))?""".r
        val r(a, b) = s
        if (b == null) Seq(a.toInt) else a.toInt to b.toInt
      }
    } match {
      case Success(r) => r
      case Failure(e) => {
        println(s"Range parse error: ${e}")
        Seq.empty
      }
    }
  }

  /**
    * Format a list of integers as a range expression.
    *
    * @param values the integers to represent as a range expression
    *
    * @return the formatted range expression
    */
  def formatRange(values: List[Int]): String = {
    toRangeString(toRangeList(values))
  }

  // https://www.rosettacode.org/wiki/Range_extraction#Scala
  private def spanRange(ls: List[Int]): (List[Int], List[Int]) = {
    var last = ls.head
    ls span { x => val b=x<=last+1; last=x; b }
  }

  private def toRangeList(ls: List[Int]): List[List[Int]] = ls match {
    case Nil => List()
    case _ => spanRange(ls) match {
      case (range, Nil) => List(range)
      case (range, rest) => range :: toRangeList(rest)
    }
  }

  private def toRangeString(ls: List[List[Int]]): String = ls map {r=>
    if(r.size<3) r mkString ","
    else r.head + "-" + r.last
  } mkString ","
}

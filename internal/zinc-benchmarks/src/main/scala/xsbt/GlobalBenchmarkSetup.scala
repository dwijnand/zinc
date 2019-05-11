/*
 * Zinc - The incremental compiler for Scala.
 * Copyright Lightbend, Inc. and Mark Harrah
 *
 * Licensed under Apache License 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0).
 *
 * See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership.
 */

package xsbt

import java.io.File

import xsbt.BenchmarkProjects.Scalac

object GlobalBenchmarkSetup {

  /** Update this list every time you add a new benchmark. */
  val projects = List(Scalac)

  def runSetup(setupDir: File): (Int, String) = {
    val projectsPreparation = projects.map { project =>
      val benchmark = new ZincBenchmark(project)
      project -> benchmark.writeSetup(setupDir)
    }

    val failedToPrepare = projectsPreparation.filter(_._2.isLeft)
    if (failedToPrepare.isEmpty)
      0 -> "Projects have been cloned and prepared. You can now run benchmarks."
    else {
      val failed = failedToPrepare.mkString("\n")
      1 -> s"Unexpected error when running benchmarks:\n$failed"
    }
  }

  def main(args: Array[String]): Unit = {
    def fail(message: String) = {
      println(message)
      System.exit(1)
    }

    if (args.isEmpty)
      fail("Missing directory to host project setups.")
    else if (args.length > 1)
      fail("Too many arguments. Pass the directory to host project setups.")
    else {
      val setupDir = new File(args(0))
      val (exitCode, status) = runSetup(setupDir)
      println(status)
      println("The benchmark setup has finished.")
      System.exit(exitCode)
    }
  }
}

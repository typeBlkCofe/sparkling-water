/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ai.h2o.sparkling.ml.utils

import java.io.{File, FileInputStream}

import ai.h2o.sparkling.utils.SparkSessionUtils
import hex.genmodel.{ModelMojoReader, MojoModel, MojoReaderBackendFactory}
import org.apache.spark.expose.Logging
import org.apache.spark.sql.Row
import org.apache.spark.sql.catalyst.expressions.GenericRow

object Utils extends Logging {
  def getMojoModel(mojoFile: File): MojoModel = {
    try {
      val reader = MojoReaderBackendFactory.createReaderBackend(mojoFile.getAbsolutePath)
      ModelMojoReader.readFrom(reader, true)
    } catch {
      case e: Throwable =>
        logError(s"Reading a mojo model with metadata failed. Trying to load the model without metadata...", e)
        val reader = MojoReaderBackendFactory.createReaderBackend(mojoFile.getAbsolutePath)
        ModelMojoReader.readFrom(reader, false)
    }
  }

  /**
   * Some Spark distributions stage model files without a recognizable extension.
   * H2O MojoReader backend autodetection can reject those paths, so retry from
   * a temporary file with .mojo suffix.
   */
  def getMojoModelWithFallback(mojoFile: File): MojoModel = {
    try {
      getMojoModel(mojoFile)
    } catch {
      case e: IOException if e.getMessage != null && e.getMessage.contains("Invalid file specification") =>
        logWarning(
          s"MOJO backend autodetection failed for '${mojoFile.getAbsolutePath}'. " +
            "Retrying from a temporary .mojo copy.")
        val tempMojoFile = SparkSessionUtils.inputStreamToTempFile(
          new FileInputStream(mojoFile),
          mojoFile.getName,
          ".mojo")
        getMojoModel(tempMojoFile)
    }
  }

  def arrayToRow[T](array: Array[T]): Row = new GenericRow(array.map(_.asInstanceOf[Any]))
}

package com.helperhub.pdfmash

import java.io.File
import java.util
import javax.swing.table.AbstractTableModel

import scala.collection.JavaConverters._

/**
  * Created by joel on 2017-07-27.
  */
class PdfListModel extends AbstractTableModel {
  private val data = new util.LinkedHashMap[File, InputPdf]
  private var dataList: Option[List[InputPdf]] = None



  // Add a new PDF.
  def addPdf(pdf: File): Boolean = {
    val added = !data.containsKey(pdf)

    if (added) {
      val pdfInput = new InputPdf(pdf)
      data.put(pdf, pdfInput)
      dataList = None
      fireTableDataChanged()
    }

    added
  }

  // Remove a PDF if it exists.
  def removePdf(pdf: File): Boolean = {
    val removed = data.remove(pdf) != null

    if (removed) {
      dataList = None
      fireTableDataChanged()
    }

    removed
  }

  override def getColumnCount: Int = 2
  override def getRowCount: Int = data.size

  override def getColumnName(column: Int): String = column match {
    case 0 => "PDF"
    case 1 => "Pages"
    case _ => throw new IndexOutOfBoundsException
  }

  // Fetch a value at the specified indices, caching the content as a
  // List in order to speed up subsequent queries.
  override def getValueAt(rowIndex: Int, columnIndex: Int): AnyRef = {
    if (columnIndex > getColumnCount) {
      throw new IndexOutOfBoundsException()
    } else {
      val row = (dataList match {
        case Some(list) => list
        case None => {
          dataList = Some(data.values.asScala.toList)
          dataList.get
        }
      })(rowIndex)

      columnIndex match {
        case 0 => row.getFile
        case 1 => row.getPages
        case _ => throw new IndexOutOfBoundsException
      }
    }
  }

  // Fetch all of the input PDFs.
  def getInputs: List[InputPdf] = dataList match {
    case Some(list) => list
    case None => {
      dataList = Some(data.values.asScala.toList)
      dataList.get
    }
  }
}

package com.helperhub.pdfmash

import java.awt.{BorderLayout, GridBagConstraints, GridBagLayout}

import java.io.File
import javax.swing.{JPanel, JScrollPane, JTable}

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-27.
  */
class InputPdfList extends JPanel() {
  setLayout(new BorderLayout)

  val dataModel = new PdfListModel
  val table = new JTable(dataModel)
  val pane = new JScrollPane(table)

  add(pane)

  // Add a new PDF.
  def addPdf(pdf: File): Boolean = dataModel.addPdf(pdf)

  // Remove a PDF if it exists.
  def removePdf(pdf: File): Boolean = dataModel.removePdf(pdf)

  // Fetch all of the input PDFs.
  def pdfs: List[InputPdf] = dataModel.getInputs
}

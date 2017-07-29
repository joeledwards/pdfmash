package com.helperhub.pdfmash

import java.io.File
import javax.swing.{BoxLayout, JPanel, JTextField}

import org.apache.pdfbox.pdmodel.PDDocument

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-27.
  */
class InputPdf(pdf: File) extends JPanel() {
  setLayout(new BoxLayout(this, BoxLayout.X_AXIS))

  private val fileTextField: JTextField = new JTextField(pdf.getName)
  private val pagesTextField: JTextField = new JTextField()

  private var pages: List[Int] = List.empty

  // TODO: perform PDF load in a SwingWorker
  val doc = PDDocument.load(pdf)
  val pageCount = doc.getNumberOfPages
  pages = (1 to pageCount).toList

  // Add the components
  add(fileTextField)
  add(pagesTextField)

  // Configure the components
  fileTextField.setEditable(false)

  // TODO: on pages field edit, validate and re-populate pages

  def getFile: File = pdf
  def getPages: List[Int] = pages
  def setPages(newPages: List[Int]): Unit = pages = newPages
}

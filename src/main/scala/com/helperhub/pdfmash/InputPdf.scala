package com.helperhub.pdfmash

import java.io.File
import javax.swing.{BoxLayout, JPanel, JTextField}

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-27.
  */
class InputPdf(pdf: File) extends JPanel() {
  setLayout(new BoxLayout(this, BoxLayout.X_AXIS))

  private val fileTextField: JTextField = new JTextField(pdf.getName)
  private val pagesTextField: JTextField = new JTextField()

  private var pages: List[Int] = List.empty

  // Add the components
  add(fileTextField)
  add(pagesTextField)

  // Configure the components
  fileTextField.setEditable(false)

  // TODO: on pages field edit, validate and re-populate pages

  def getFile: File = pdf
  def getPages: Iterable[Int] = pages
}

package com.helperhub.pdfmash

import java.awt.{BorderLayout, Dimension}
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing.{JButton, JFileChooser, JFrame, JPanel}

import scala.util.Try

/**
  * This is the main window of the Swing UI. Multiple input PDFs can be
  * selected, and pages selected for each. An output PDF location can be
  * specified, then the selected pages for all contributing PDFs will be
  * written to the target location.
  *
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-27.
  */
class PdfMash extends JFrame {
  private val addPdfButton = new JButton("Add PDF")
  private val writePdfButton = new JButton("Write PDF")
  private val mainPanel = new JPanel(new BorderLayout)
  private val inputPdfList = new InputPdfList

  // Layout the UI
  private def init: PdfMash = {
    // Input file controls.
    mainPanel.add(addPdfButton, BorderLayout.NORTH)
    addPdfButton.addActionListener(_ => {
      println("Add PDF Button clicked.")
      selectInputPdf()
    })

    //Input file list
    mainPanel.add(inputPdfList, BorderLayout.CENTER)

    // Output file controls.
    mainPanel.add(writePdfButton, BorderLayout.SOUTH)
    writePdfButton.addActionListener(_ => {
      println("Write PDF Button clicked.")
      selectOutputPdf()
    })

    // Setup and configure the main window.
    this.add(mainPanel)
    this.setPreferredSize(new Dimension(600, 600))
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    this.pack

    this
  }

  // Open a prompt to select a PDF file.
  private def selectPdf(save: Boolean): Option[File] = {
    val chooser = new JFileChooser()
    val filter = new FileNameExtensionFilter("PDF Files", "pdf")
    chooser.setFileFilter(filter)
    (save match {
      case true => chooser.showSaveDialog(this)
      case false => chooser.showOpenDialog(this)
    }) match {
      case JFileChooser.APPROVE_OPTION => Some(chooser.getSelectedFile())
      case _ => None
    }
  }

  // Prompt the user to select an input PDF.
  private def selectInputPdf(): Unit = {
    selectPdf(false) match {
      case None => println("No input file selected.")
      case Some(pdf) => {
        println(s"Adding input from ${pdf.getName}")
        inputPdfList.addPdf(pdf)
      }
    }
  }

  // Prompt the user to select the output PDF location.
  private def selectOutputPdf(): Unit = {
    selectPdf(true) match {
      case None => println("No output file selected.")
      case Some(pdf) => {
        println(s"Writing output to ${pdf.getName}")
        writePdf()
      }
    }
  }

  private def writePdf(): Unit = {

  }

  // Launch the program
  def run: PdfMash = {
    init

    this.setVisible(true)

    this
  }

  private var endCallbacks: List[() => Unit] = List.empty
  def onEnd(callback: () => Unit): PdfMash = {
    endCallbacks = callback :: endCallbacks
    this
  }

  // On shutdown
  private def end: Unit = {
    endCallbacks.foreach(callback => Try(callback()))
  }
}

/**
  * Created by joel on 2017-07-27.
  */
object PdfMash extends App {
  new PdfMash().run.onEnd(() => println("Done"))
}

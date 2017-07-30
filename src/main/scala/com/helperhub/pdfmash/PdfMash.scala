package com.helperhub.pdfmash

import java.awt.{BorderLayout, Color, Dimension}
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter
import javax.swing._
import javax.swing.event.{DocumentEvent, DocumentListener}

import org.apache.pdfbox.pdmodel.PDDocument

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
  private val mainPanel = new JPanel(new BorderLayout)

  private val controlBox = new JPanel(new BorderLayout)
  private val addPdfButton = new JButton("Add PDF")

  private val pagesPanel = new JPanel(new BorderLayout)
  private val pagesLabel = new JLabel("Page Selection:")
  private val pagesTextField = new JTextField()

  private val writePdfButton = new JButton("Write PDF")
  private val inputPdfList = new InputPdfList

  private var activePdf: Option[InputPdf] = None

  // Layout the UI
  private def init: PdfMash = {
    // Input file controls.
    controlBox.add(addPdfButton, BorderLayout.NORTH)
    addPdfButton.addActionListener(_ => {
      println("Add PDF Button clicked.")
      selectInputPdf()
    })

    pagesPanel.add(pagesLabel, BorderLayout.WEST)
    pagesPanel.add(pagesTextField, BorderLayout.CENTER)
    controlBox.add(pagesPanel, BorderLayout.SOUTH)
    pagesTextField.setEditable(false)
    pagesTextField.getDocument.addDocumentListener(new DocumentListener {
      override def removeUpdate(e: DocumentEvent): Unit = updated(e)
      override def changedUpdate(e: DocumentEvent): Unit = updated(e)
      override def insertUpdate(e: DocumentEvent): Unit = updated(e)
      private def updated(e: DocumentEvent): Unit = {
        val len = e.getDocument.getLength
        val doc = e.getDocument.getText(0, len)
        val range = Utils.parseRange(doc).toList
        val max = range.fold(0)((a, b) => if (a < b) b else a )
        println(s"range.isEmpty = ${range.isEmpty}")
        println(s"range.max = ${max}")

        // Validate the range configuration for the selected PDF.
        val rangeValid = activePdf.map(p => !range.isEmpty && max <= p.pageCount).getOrElse(false)

        if (rangeValid) {
          // Update the range selection in the list.
          pagesTextField.setBackground(Color.WHITE)
          activePdf.foreach(pdf => inputPdfList.updatePdfPages(pdf.getFile, range))
          println(s"Range: ${Utils.formatRange(range)}")
        } else {
          pagesTextField.setBackground(Color.YELLOW)
        }
      }
    })

    mainPanel.add(controlBox, BorderLayout.NORTH)

    // Input file list
    mainPanel.add(inputPdfList, BorderLayout.CENTER)
    inputPdfList.onSelect {
      case Some(input) => {
        SwingUtilities.invokeLater { () =>
          activePdf = Some(input)
          pagesTextField.setText(Utils.formatRange(input.getPages))
          pagesTextField.setEditable(true)
        }
      }
      case None => {
        SwingUtilities.invokeLater { () =>
          activePdf = None
          pagesTextField.setEditable(false)
          pagesTextField.setText("")
        }
      }
    }

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
        writePdf(pdf)
      }
    }
  }

  private def writePdf(pdf: File): Unit = {
    val combinedDoc = new PDDocument

    inputPdfList.pdfs.map { inputPdf =>
      (inputPdf, PDDocument.load(inputPdf.getFile))
    } foreach { case (pdf, doc) =>
      pdf.getPages.foreach { page =>
        val index = page - 1
        combinedDoc.addPage(doc.getPage(index))
      }
    }

    combinedDoc.save(pdf)
  }

  // Launch the program
  def run: PdfMash = {
    init

    setVisible(true)

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

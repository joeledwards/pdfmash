package com.helperhub.pdfmash

import java.awt.{BorderLayout, Color, Dimension, FileDialog, Frame}
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
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
    } catch {
      case error: Throwable => error.printStackTrace()
    }

    // Input file controls.
    controlBox.add(Box(addPdfButton, top=5), BorderLayout.NORTH)
    addPdfButton.addActionListener(_ => {
      println("Add PDF Button clicked.")
      selectInputPdf()
    })

    pagesPanel.add(Box(pagesLabel, left=6), BorderLayout.WEST)
    pagesPanel.add(Box(pagesTextField, left=5, right=3), BorderLayout.CENTER)
    controlBox.add(Box(pagesPanel), BorderLayout.SOUTH)
    pagesTextField.setEditable(false)
    pagesTextField.getDocument.addDocumentListener(new DocumentListener {
      override def removeUpdate(e: DocumentEvent): Unit = updated(e)
      override def changedUpdate(e: DocumentEvent): Unit = updated(e)
      override def insertUpdate(e: DocumentEvent): Unit = updated(e)
      private def updated(e: DocumentEvent): Unit = {
        val textLength = e.getDocument.getLength
        val text = e.getDocument.getText(0, textLength)
        val range = Utils.parseRange(text).toList
        val maxPage = range.fold(0)((a, b) => if (a < b) b else a )

        // Validate the range configuration for the selected PDF.
        val rangeValid = activePdf.map(p => !range.isEmpty && maxPage <= p.pageCount).getOrElse(false)

        if (rangeValid) {
          // Update the range selection in the list.
          pagesTextField.setBackground(Color.WHITE)
          activePdf.foreach(pdf => inputPdfList.updatePdfPages(pdf.getFile, range))
        } else {
          pagesTextField.setBackground(Color.YELLOW)
        }
      }
    })

    mainPanel.add(Box(controlBox), BorderLayout.NORTH)

    // Input file list
    mainPanel.add(Box(inputPdfList, top=5, left=6, right=6), BorderLayout.CENTER)
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
    mainPanel.add(Box(writePdfButton, top=3, bottom=2), BorderLayout.SOUTH)
    writePdfButton.addActionListener(_ => {
      println("Write PDF Button clicked.")
      selectOutputPdf()
    })

    // Setup and configure the main window.
    this.add(mainPanel)
    this.setPreferredSize(new Dimension(800, 600))
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    this.pack

    this
  }

  // Open a prompt to select a PDF file using the system's native file selection dialog.
  private def selectPdf(save: Boolean): Option[File] = {
    val (mode, title) = save match {
      case true => (FileDialog.SAVE, "Save PDF")
      case false => (FileDialog.LOAD, "Add PDF")
    }

    val dialog = new FileDialog(null.asInstanceOf[Frame], title,  mode)
    dialog.setVisible(true)
    val directory = dialog.getDirectory
    val filename = dialog.getFile

    if (
      directory == null || filename == null ||
      directory.trim().equals("") || filename.trim().equals("")
    ) {
      None
    } else {
      Some(new File(directory, filename))
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
        JOptionPane.showMessageDialog(
          writePdfButton, "PDF write completed successfully.", "PDF Written", JOptionPane.INFORMATION_MESSAGE
        )
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

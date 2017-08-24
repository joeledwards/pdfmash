package com.helperhub.pdfmash

import java.awt.{BorderLayout, Color}
import java.io.File
import javax.swing.{JPanel, JScrollPane, JTable, ListSelectionModel}

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-27.
  */
class InputPdfList extends JPanel() {
  setLayout(new BorderLayout)

  val dataModel = new PdfListModel
  val table = new JTable(dataModel)
  table.setSelectionBackground(Color.ORANGE)
  table.setSelectionForeground(Color.WHITE)
  val pane = new JScrollPane(table)

  type SelectHandler = (Option[InputPdf]) => Unit
  private var selectHandler: Option[SelectHandler] = None

  /**
    * Register a handler for notifications when the selection changes,
    * supplying it Some(InputPdf) when a selection has been made, and
    * None when the selection has been cleared.
    *
    * @param handler the selection handler
    */
  def onSelect(handler: SelectHandler): Unit = {
    selectHandler = Option(handler)
  }

  table.setDefaultRenderer(classOf[Object], InputPdfCellRenderer)
  table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  table.getSelectionModel.addListSelectionListener { _ =>
    if (table.getSelectedColumn == 0) {
      table.getSelectedRow match {
        case row if row >= 0 => {
          println(s"Row ${row} is selected.")
          val input = dataModel.getInputs(row)
          selectHandler.foreach(_(Some(input)))
        }
        case _ => {
          println(s"No row selected.")
          selectHandler.foreach(_(None))
        }
      }
    }
  }

  add(pane)

  // Add a new PDF.
  def addPdf(pdf: File): Boolean = dataModel.addPdf(pdf)

  // Remove a PDF if it exists.
  def removePdf(pdf: File): Boolean = dataModel.removePdf(pdf)

  // Fetch all of the input PDFs.
  def pdfs: List[InputPdf] = dataModel.getInputs

  // Adjust the selected pages for a PDF.
  def updatePdfPages(pdf: File, pages: List[Int]): Boolean = dataModel.updatePdfPages(pdf, pages)
}

package com.helperhub.pdfmash

import java.awt.{Color, Component}
import javax.swing.{JLabel, JTable}
import javax.swing.table.DefaultTableCellRenderer

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-28.
  */
object InputPdfCellRenderer extends DefaultTableCellRenderer {
  private def plur(count: Int, unit: String): String = count match {
    case 1 => s"1 $unit"
    case n => s"$n ${unit}s"
    case _ => ""
  }

  override def getTableCellRendererComponent(
      table: JTable,
      value: scala.AnyRef,
      isSelected: Boolean,
      hasFocus: Boolean,
      row: Int,
      column: Int
  ): Component = {
    column match {
      case 0 => {
        table.getModel.getValueAt(row, column) match {
          case pdf: InputPdf => {
            val label = new JLabel(s"[${plur(pdf.pageCount, "page")}] ${pdf.getFile}")
            label.setBackground(if (isSelected) table.getSelectionBackground else table.getBackground)
            label
          }
        }
      }
      case 1 => {
        val summary = table.getModel.getValueAt(row, column) match {
          case Nil => "0 pages selected"
          case page :: Nil => s"1 page selected: ${page}"
          case pages: List[Int] => {
            s"${pages.size} pages selected: ${Utils.formatRange(pages)}"
          }
          case _ => "--"
        }

        val label = new JLabel(summary)
        label.setBackground(if (isSelected) table.getSelectionBackground else table.getBackground)
        label
      }
      case _ => {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
      }
    }
  }
}

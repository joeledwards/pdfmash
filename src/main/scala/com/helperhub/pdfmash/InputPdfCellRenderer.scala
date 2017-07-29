package com.helperhub.pdfmash

import java.awt.Component
import javax.swing.{JLabel, JTable}
import javax.swing.table.DefaultTableCellRenderer

/**
  * @author Joel Edwards &lt;joeledwards@gmail.com&gt;
  * @since 2017-07-28.
  */
object InputPdfCellRenderer extends DefaultTableCellRenderer {
  override def getTableCellRendererComponent(
      table: JTable,
      value: scala.AnyRef,
      isSelected: Boolean,
      hasFocus: Boolean,
      row: Int,
      column: Int
  ): Component = {
    if (column == 1) {
      val summary = table.getModel.getValueAt(row, column) match {
        case Nil => "0 pages selected"
        case page :: Nil => s"1 page selected: ${page}"
        case pages: List[Int] => {
          s"${pages.size} pages selected: ${Utils.formatRange(pages)}"
        }
        case _ => "--"
      }

      new JLabel(summary)
    } else {
      super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    }
  }
}

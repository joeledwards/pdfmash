package com.helperhub.pdfmash

import javax.swing.ImageIcon

object Icons {
  private val path = "com/helperhub/pdfmash/resources"

  private def icon(name: String, width: Int, height: Int): ImageIcon = {
    Resources.getImageAsIcon(s"$path/$name.png", width, height)
  }

  def pdf(width: Int = 64, height: Int = 64): ImageIcon = icon("pdf", width, height)
  def pen(width: Int = 64, height: Int = 64): ImageIcon = icon("pen", width, height)
  def plus(width: Int = 64, height: Int = 64): ImageIcon = icon("plus", width, height)
  def trash(width: Int = 64, height: Int = 64): ImageIcon = icon("trash", width, height)
}

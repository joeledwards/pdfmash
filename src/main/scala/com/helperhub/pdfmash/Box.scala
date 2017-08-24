package com.helperhub.pdfmash

import java.awt.{BorderLayout, Component}
import javax.swing.JPanel

case class Box(
  component: Component,
  top: Int = 0,
  right: Int = 0,
  bottom: Int = 0,
  left: Int = 0
) extends JPanel(new BorderLayout) {
  add(component, BorderLayout.CENTER)

  if (top > 0) add(javax.swing.Box.createVerticalStrut(top), BorderLayout.NORTH)
  if (left > 0) add(javax.swing.Box.createHorizontalStrut(left), BorderLayout.WEST)
  if (right > 0) add(javax.swing.Box.createHorizontalStrut(right), BorderLayout.EAST)
  if (bottom > 0) add(javax.swing.Box.createVerticalStrut(bottom), BorderLayout.SOUTH)
}

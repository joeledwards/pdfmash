package com.helperhub.pdfmash

import java.awt.Image
import javax.swing.ImageIcon

object Resources {
  /**
    * Fetch an image resource as an icon.
    *
    * @param name the name of the resource (path relative to root)
    * @param width scale to this width
    * @param height scale to this height
    *
    * @return the icon
    */
  def getImageAsIcon(name: String, width: Int, height: Int): ImageIcon = {
    new ImageIcon(
      new ImageIcon(ClassLoader.getSystemResource(name))
      .getImage()
      .getScaledInstance(height, width, Image.SCALE_SMOOTH)
    )
  }
}

package com.zarniwoop.xpose

import org.apache.log4j.Logger

trait Logging {
  val loggerName = this.getClass.getName
  lazy val logger = Logger.getLogger(loggerName)

  def debug(msg: => String): Unit = {
    if (logger.isDebugEnabled())
      logger.debug(msg)
  }

  def info(msg: => String): Unit = {
    if (logger.isInfoEnabled())
      logger.info(msg)
  }

  def warn(msg: => String): Unit = {
    logger.warn(msg)
  }

  def error(msg: => String): Unit = {
    logger.error(msg)
  }

}
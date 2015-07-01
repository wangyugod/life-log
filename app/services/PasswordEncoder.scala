package services

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

import org.slf4j.LoggerFactory

/**
 * Created by Administrator on 2015/5/20.
 */
trait PasswordEncoder {
  def encode(password: String, salt: String): String
}

class Md5PasswordEncoder extends PasswordEncoder {
  private val logger = LoggerFactory.getLogger(classOf[Md5PasswordEncoder])

  override def encode(password: String, salt: String) = {
    val md = MessageDigest.getInstance("MD5")
    val bytes = md.digest((password + salt).getBytes)
    val encodedPwd = DatatypeConverter.printHexBinary(bytes)
    if (logger.isDebugEnabled()) {
      logger.debug("encoded pwd:" + encodedPwd)
    }
    encodedPwd
  }

}
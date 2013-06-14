package code.snippet

import code.model.User
import net.liftweb.util.Helpers._
import xml.NodeSeq
import net.liftweb.util.PassThru
import omniauth.Omniauth
import net.liftweb.common.Full

/**
 * Created with IntelliJ IDEA.
 * User: j2
 * Date: 13-06-13
 * Time: 05:22 PM
 * To change this template use File | Settings | File Templates.
 */
object Site {
  def render = {
    Omniauth.currentAuth match {
      case Full(omni) => ({
        println(omni.provider)
        println(omni.uid)
        println(omni.firstName)
      })
      case _ =>
        println("AAA")
    }
    User.loggedIn_? match {
      case true =>
        "data-lift-id=social-login *" #> NodeSeq.Empty
      case false =>
        "data-lift-id=social-login *" #> PassThru
    }
  }

}

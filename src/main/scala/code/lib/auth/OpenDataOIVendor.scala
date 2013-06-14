package code.lib.auth

/**
 * Created with IntelliJ IDEA.
 * User: j2
 * Date: 13-06-13
 * Time: 03:44 PM
 * To change this template use File | Settings | File Templates.
 */

import _root_.org.openid4java.discovery.DiscoveryInformation
import _root_.org.openid4java.message.AuthRequest
import net.liftmodules.openid.{OpenIDConsumer, WellKnownEndpoints, WellKnownAttributes, SimpleOpenIDVendor}
import net.liftweb.common.Full

object OpenDataOIVendor extends SimpleOpenIDVendor  {
  def ext(di: DiscoveryInformation, authReq: AuthRequest): Unit = {
    import WellKnownAttributes._
    WellKnownEndpoints.findEndpoint(di) map {ep =>
      ep.makeAttributeExtension(List(Email, FullName, FirstName, LastName)) foreach {ex =>
        authReq.addExtension(ex)
      }
    }
  }
  override def createAConsumer = new OpenIDConsumer[UserType] {
    beforeAuth = Full(ext _)
  }
}

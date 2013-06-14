package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import js.jquery.JQueryArtifacts
import sitemap._
import Loc._
import mapper._

import code.model._
import net.liftmodules.{FoBo, JQueryModule}
import code.lib.model.DbHelper
import net.liftweb.squerylrecord.RecordTypeMode._
import code.lib.auth.OpenDataOIVendor
import omniauth.Omniauth
import omniauth.lib.{GithubProvider, TwitterProvider, FacebookProvider}


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {


    //The FoBo setup and init
    FoBo.InitParam.JQuery=FoBo.JQuery182
    FoBo.InitParam.ToolKit=FoBo.Bootstrap230
    FoBo.InitParam.ToolKit=FoBo.FontAwesome300
    FoBo.InitParam.ToolKit=FoBo.PrettifyJun2011
    FoBo.init()

    // where to search snippet
    LiftRules.addToPackages("code")

    DbHelper.initDB()

    S.addAround(new LoanWrapper {
      override def apply[T](f: => T): T = {
        val result = inTransaction {
          try {
            Right(f)
          } catch {
            case e: LiftFlowOfControlException => Left(e)
          }
        }

        result match {
          case Right(r) => r
          case Left(exception) => throw exception
        }
      }
    })

    // Uncomment the following line to create the database schema
    DbHelper.createSchema()

    // Build SiteMap
    val entries  = Omniauth.sitemap ::: List(
      Menu.i("Home") / "index"
    )

    //LiftRules.setSiteMap(SiteMap(entries:_*))

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMapFunc(() => User.sitemapMutator(SiteMap(entries:_*)))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery172
    JQueryModule.init()

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // What is the function to test if a user is logged in?
    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //Omniauth stuff
    //Supply a list of providers
    Omniauth.init



    //OpenID stuff
    // ToDo doesnt work, needs fix for record instead ofmapper
    //LiftRules.dispatch.append(OpenDataOIVendor.dispatchPF)
    //LiftRules.snippets.append(OpenDataOIVendor.snippetPF)

  }
}

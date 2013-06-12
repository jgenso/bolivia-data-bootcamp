/*
 * Copyright (C) 2013 Genso Iniciativas Web.
 */

package code.lib.model

import net.liftweb.squerylrecord.RecordTypeMode
import RecordTypeMode._
import code.model._
import java.sql.DriverManager
import net.liftweb.http.{Req, LiftRules}
import net.liftweb.util.Props

object DbHelper {


  private def initUsers = {
    val administrator = User.createRecord.validated(true).firstName("Admin").superUser(true).
      uniqueId("KNTMS5Y1TML2XWY3MVWGBUXXEB15VTTM").lastName("Admin").email("juan@genso.com.bo")
      administrator.password.setFromAny("$2a$10$P/QY2p6MVbaRMUibO33CRuhm3sj6YVFDrqNUzaXMcR0k/Hio0hfLe")
      DbSchema.users.insert(List(administrator))
    }

  private def initData = {
    initUsers

  }


  def dropSchema() {
    inTransaction {
      try {
        DbSchema.drop
      } catch {
        case e => e.printStackTrace()
        throw e;
      }
    }
  }

  def createSchema() {
    inTransaction {
      try {
        DbSchema.create
        initData
      } catch {
        case e => e.printStackTrace()
        throw e;
      }
    }
  }

  def initDB() = if (Props.devMode) {
    initH2()
  } else {
    initPostgresql() //ToDo Postgres
  }

  private def initH2() {

    Class.forName("org.h2.Driver")

    import org.squeryl.adapters.H2Adapter
    import net.liftweb.squerylrecord.SquerylRecord
    import org.squeryl.Session

    SquerylRecord.initWithSquerylSession(Session.create(
      DriverManager.getConnection("jdbc:h2:mem:dbname;DB_CLOSE_DELAY=-1", "sa", ""),
      new H2Adapter))

    LiftRules.liftRequest.append({
      case Req("console" ::_, _, _) => false
    })
  }

  private def initPostgresql() {

    Class.forName("org.postgresql.Driver")

    import org.squeryl.Session
    import org.squeryl.adapters._
    import net.liftweb.squerylrecord.SquerylRecord

    def connection = DriverManager.getConnection("jdbc:postgresql://localhost/liftbaseapp")

    SquerylRecord.initWithSquerylSession(Session.create(connection, new PostgreSqlAdapter))
  }
}

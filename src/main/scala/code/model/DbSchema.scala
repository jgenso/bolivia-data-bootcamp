package code.model

import org.squeryl.Schema


object DbSchema extends Schema {

  val users = table[User]("user")


 }

package utils

import java.text.SimpleDateFormat

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.json4s.NoTypeHints
import org.json4s.jackson.Serialization

object PrettyPrint {
  implicit val formats = Serialization.formats(NoTypeHints)
  val mapper = new ObjectMapper().registerModule(DefaultScalaModule)
  val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
  mapper.setDateFormat(df);

  def toJsonString(so: AnyRef): String = {
    mapper.writeValueAsString(so)
  }
}

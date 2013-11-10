package net.groovygrevling

import org.scalatra.json.JacksonJsonSupport
import org.scalatra.ScalatraFilter
import org.json4s.{Formats, DefaultFormats}


trait PubchessStack extends ScalatraFilter with JacksonJsonSupport {

  protected implicit val jsonFormats: Formats = DefaultFormats

  before() {
    contentType = formats("json")
  }

  notFound {
    status = 404
  }


}

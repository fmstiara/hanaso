package s2s_apis

import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.{HeaderNames, MimeTypes}

import scala.concurrent.Future

trait S2sPostable {
  val S2S_BASE_URL = "https://server1-dot-hanaso-283706.an.r.appspot.com/"

  def post(method: String, requestParamJson: JsValue, ws: WSClient): Future[WSResponse] = {
    val slackRequestUrl: String = s"$S2S_BASE_URL$method"
    ws.url(slackRequestUrl)
      .addHttpHeaders(
        HeaderNames.CONTENT_TYPE -> MimeTypes.JSON,
      )
      .post(requestParamJson)
  }
}

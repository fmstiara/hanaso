package slack_apis

import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.{HeaderNames, MimeTypes}

import scala.concurrent.Future

trait SlackPostable {
  val SLACK_API_BASE_URL = "https://slack.com/api/"

  def post(method: String, slackApiToken: String, requestParamJson: JsValue, ws: WSClient): Future[WSResponse] = {
    val slackRequestUrl: String = s"$SLACK_API_BASE_URL$method"
    ws.url(slackRequestUrl)
      .addHttpHeaders(
        HeaderNames.CONTENT_TYPE -> MimeTypes.JSON,
        HeaderNames.AUTHORIZATION -> s"Bearer $slackApiToken"
      )
      .post(requestParamJson)
  }

  def statusPost(responseUrl: String, status: Int, slackApiToken: String, ws: WSClient): Future[WSResponse] = {
    ws.url(responseUrl)
      .addHttpHeaders(
        HeaderNames.CONTENT_TYPE -> MimeTypes.JSON,
        HeaderNames.AUTHORIZATION -> s"Bearer $slackApiToken"
      )
      .post(Json.obj("status" -> status))
  }

}

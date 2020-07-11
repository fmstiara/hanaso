package teams_apis

import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}
import play.mvc.Http.{HeaderNames, MimeTypes}

import scala.concurrent.Future
import scala.util.Properties

trait TeamsPostable {
  val TEAMS_TOKEN_BASE_URL = "https://login.microsoftonline.com/common/oauth2/v2.0/token"
  val GRAPH_API_BASE_URL = "https://graph.microsoft.com/v1.0/"

  def token(ws: WSClient): Future[WSResponse] = {
    val client_id = Properties.envOrElse("CLIENT_ID", "NO_TOKEN")
    val client_secret = Properties.envOrElse("CLIENT_SECRET", "NO_TOKEN")
    val refresh_token = Properties.envOrElse("REFRESH_TOKEN", "NO_TOKEN")

    ws.url(TEAMS_TOKEN_BASE_URL)
      .addHttpHeaders(
        HeaderNames.CONTENT_TYPE -> "application/x-www-form-urlencoded"
      )
      .post(s"client_id=$client_id&grant_type=refresh_token&scope=openid%20offline_access%20https%3A%2F%2Fgraph.microsoft.com%2Fonlinemeetings.readwrite&redirect_url=http://localhost:10101/authorized&client_secret=$client_secret&refresh_token=$refresh_token")
  }

  def post(method: String, accessToken: String, requestParamJson: JsValue, ws: WSClient): Future[WSResponse] = {
    val teamsRequestUrl: String = s"$GRAPH_API_BASE_URL$method"

    ws.url(teamsRequestUrl)
      .addHttpHeaders(
        HeaderNames.CONTENT_TYPE -> MimeTypes.JSON,
        HeaderNames.AUTHORIZATION -> s"Bearer $accessToken"
      )
      .post(requestParamJson)
  }
}

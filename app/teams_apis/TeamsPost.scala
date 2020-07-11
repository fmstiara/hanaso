package teams_apis

import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TeamsPost(val ws: WSClient) extends TeamsPostable with TeamsResponseJsonParsable {
  def getAccessToken(): Future[TokenResponse] = {
    token(ws).flatMap{ response =>
      Future.fromTry(
        parse[TokenResponse](response.json)
      )
    }
  }

  def getTeamsLink(accessToken: String): Future[TeamsResponse] = {
    val requestParamJson = Json.toJson(TeamsRequest("Hanaso"))

    post("me/onlineMeetings/", accessToken, requestParamJson, ws).flatMap{ response =>
      Future.fromTry(
        parse[TeamsResponse](response.json)
      )
    }
  }
}



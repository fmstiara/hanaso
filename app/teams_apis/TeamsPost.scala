package teams_apis

import java.util.logging.Logger
import play.api.libs.json._
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

class TeamsPost(val ws: WSClient)(implicit logger: Logger) extends TeamsPostable with TeamsResponseJsonParsable {
  def getAccessToken(): Future[TokenResponse] = {
    token(ws).flatMap{ response =>
      logger.info(s"response from MSTeams:${response.body}")
      Future.fromTry(
        parse[TokenResponse](response.json)
      )
    }
  }

  def getTeamsLink(accessToken: String)(implicit logger: Logger): Future[TeamsResponse] = {
    val requestParamJson = Json.toJson(TeamsRequest("Hanaso"))

    post("me/onlineMeetings/", accessToken, requestParamJson, ws).flatMap{ response =>
      logger.info(s"response from MSGraph:${response.body}")
      Future.fromTry(
        parse[TeamsResponse](response.json)
      )
    }
  }
}



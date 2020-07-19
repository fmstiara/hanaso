package slack_apis

import java.util.logging.Logger

import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.util.Properties

class SlackPost(val ws: WSClient) extends SlackPostable with SlackResponseJsonParsable {
  val slackApiToken = Properties.envOrElse("SLACK_API_TOKEN", "NO_TOKEN") //Fringe

  def postDm(channelId: String, targetId: String, callType: Int, teamsUrl:String = "")(implicit logger: Logger): Future[PostedMessageResponse] = {

    val postTextRequest = new PostTextRequest(channelId, targetId, callType, teamsUrl)
    val requestParamJson = Json.toJson(postTextRequest.get())

    post("chat.postMessage", slackApiToken, requestParamJson, ws).flatMap{ response =>
      logger.info(s"response from slack: ${response.body}")
      Future.fromTry(
        parse[PostedMessageResponse](response.json)
      )
    }
  }

  def statusOkPost(responseUrl: String): Future[WSResponse] = {
    statusPost(responseUrl, 200, slackApiToken, ws)
  }
}

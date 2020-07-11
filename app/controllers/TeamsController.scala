package controllers

import java.util.logging.Logger

import javax.inject.Inject
import play.api.libs.json.{Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import s2s_apis.S2sPost
import slack_apis.errors.InvalidParamException
import teams_apis.TeamsPost
import slack_apis.SlackPost
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.{Future}



class TeamsController @Inject()(cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {
  def post(): Action[AnyContent] =
    Action.async { request =>
      val logger = Logger.getLogger("play")
      logger.info("-------- [START] TeamsController.post() --------")

      val payload = request.body.asFormUrlEncoded.get("payload").head
      val responseUrl = (Json.parse(payload) \ "response_url").as[String]

      logger.info(payload)

      val actionType = (Json.parse(payload) \\ "block_id").head.as[String]
      val slackPoster = new SlackPost(ws)
      slackPoster.statusOkPost(responseUrl)

      actionType match {
        case "openTeamsLink" =>
          Future("Hanaso").map(Ok(_))

        case "sendMessageToTalk" =>
          logger.info("---------- [S2SPOST] ---------")
          val actionValue = Json.parse(
            (Json.parse(payload)  \\ "value").head.as[String]
          )
          val s2sPoster = new S2sPost(ws)
          s2sPoster.postValue(actionValue)
          Future("Hanaso").map(Ok(_))

        case "confirmAbleToTalk" =>
          logger.info("---------- [TeamsLinkPost] ----------")
          val actionValue = Json.parse(
            (Json.parse(payload)  \\ "value").head.as[String]
          )
          val fromUserId = (actionValue \ "from_user_id").as[String]
          val toUserId = (actionValue \ "to_user_id").as[String]
          val isOk = (actionValue \ "is_ok").as[Boolean]

          if(isOk){
            logger.info("--------- 相手がOKのとき --------")
            val teamsPoster = new TeamsPost(ws)

            val postedMessage = for {
              accessToken <- teamsPoster.getAccessToken()
              teamsUrl <- teamsPoster.getTeamsLink(accessToken.access_token)
              response <- Future.sequence(
                Seq(
                  slackPoster.postDm(fromUserId, toUserId, 0, teamsUrl.joinUrl),
                  slackPoster.postDm(toUserId, fromUserId, 1, teamsUrl.joinUrl)
                )
              )
            } yield {
              response
            }

            // 現状、エラーメッセージを受け取れない。
            postedMessage.recover{
              case e: Exception => InternalServerError(Json.obj("error" -> e.getMessage))
            }
            Future("Hanaso").map(Ok(_))

          } else {
            logger.info("--------- 相手がNGのとき --------")
            val postedMessage =  slackPoster.postDm(fromUserId, toUserId, 2, "")

            // 現状、エラーメッセージを受け取れない。
            postedMessage
              .map( response =>
                Ok(
                  Json.obj("text" -> response.message.text)
                )
              )
              .recover{
                case InvalidParamException(message) => BadRequest(Json.obj("error" -> message))
                case e: Exception => InternalServerError(Json.obj("error" -> e.getMessage))
              }
          }

        case _ =>
          Future("Hanaso").map( _ => Ok(
            Json.obj("error" -> "block_idが正しくない")
          ))
      }
    }

}

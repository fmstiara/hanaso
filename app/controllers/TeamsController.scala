package controllers

import java.util.logging.Logger

import javax.inject.Inject
import play.api.libs.json.{JsObject, JsString, JsValue, Json}
import play.api.libs.ws.WSClient
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import s2s_apis.S2sPost
import slack_apis.errors.InvalidParamException
import teams_apis.TeamsPost
import slack_apis.SlackPost

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future



class TeamsController @Inject()(cc: ControllerComponents, ws: WSClient) extends AbstractController(cc) {
  def post(): Action[AnyContent] =
    Action.async { request =>
      implicit val logger = Logger.getLogger("play")
      logger.info("-------- [START] TeamsController.post() --------")

      val payload = request.body.asFormUrlEncoded.get("payload").head
      val responseUrl = (Json.parse(payload) \ "response_url").as[String]

      logger.info(payload)
      logger.info(responseUrl)

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

          val newValue: JsValue = actionValue.as[JsObject] + ("delete_message_url" -> JsString(responseUrl))

          val s2sPoster = new S2sPost(ws)
          val res = s2sPoster.postValue(newValue)
          res.foreach(v => logger.info(s"${v.body}"))
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
                  slackPoster.postDm(toUserId, fromUserId, 1, teamsUrl.joinUrl),
                  slackPoster.deleteMessage(responseUrl)
                )
              )
            } yield {
              response
            }

            postedMessage.foreach(res => logger.info(res.toString()))

            // 現状、エラーメッセージを受け取れない。残念。。。
            postedMessage.recover{
              case e: Exception => InternalServerError(Json.obj("error" -> e.getMessage))
            }
            Future("Hanaso").map(Ok(_))

          } else {
            logger.info("--------- 相手がNGのとき --------")
            val postedMessage = for {
              response <- Future.sequence(
                Seq(
                  slackPoster.postDm(fromUserId, toUserId, 2, ""),
                  slackPoster.postDm(toUserId, fromUserId, 3, ""),
                  slackPoster.deleteMessage(responseUrl)
                )
              )
            } yield {
              response
            }

            // 現状、エラーメッセージを受け取れない。残念。。。
            postedMessage.recover{
              case e: Exception => InternalServerError(Json.obj("error" -> e.getMessage))
            }
            Future("Hanaso").map(Ok(_))
          }

        case _ =>
          Future("Hanaso").map( _ => Ok(
            Json.obj("error" -> "block_idが正しくない")
          ))
      }
    }

}

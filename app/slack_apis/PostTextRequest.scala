package slack_apis

import play.api.libs.json.{JsValue, Json}

class PostTextRequest(channel: String, target: String, callType: Int, url: String) {
  val CALL_MESSAGE = s"<@$target>さんは大丈夫みたいですよ :blush: 早速話しましょう :laughing:"
  val CALLED_MESSAGE = s"では、<@$target>さんとお話しましょう :laughing:"
  val CALL_FAILED_MESSAGE = s"<@$target> は今空いてないみたいです...:cry:改めて時間を約束しましょう :hugging_face:"
  val CALLED_FAILED_MESSAGE = s"かしこまりました :blush: もし必要であれば、改めて<@$target>さんと時間を約束しましょう"

  def createRequest(message: String): JsValue =
    Json.obj(
      "channel" -> channel,
      "text" -> "Hanaso",
      "blocks" -> Json.arr(
        Json.obj(
          "type" -> "section",
          "text" -> Json.obj(
            "type" -> "mrkdwn",
            "text" -> message
          )
        ),
        Json.obj(
          "type" -> "actions",
          "block_id" -> "openTeamsLink",
          "elements" -> Json.arr(
            Json.obj(
              "type" -> "button",
              "text" -> Json.obj(
                "type" -> "plain_text",
                "text" -> "Teams会議へ"
              ),
              "url" -> url
            )
          )
        )
      )
    )

  def createFailedRequest(message: String): JsValue =
    Json.obj(
      "channel" -> channel,
      "text" -> "Hanaso",
      "blocks" -> Json.arr(
        Json.obj(
          "type" -> "section",
          "text" -> Json.obj(
            "type" -> "mrkdwn",
            "text" -> message
          )
        )
      )
    )

  def get(): JsValue = {
    callType match {
      case 0 => createRequest(CALL_MESSAGE)
      case 1 => createRequest(CALLED_MESSAGE)
      case 2 => createFailedRequest(CALL_FAILED_MESSAGE)
      case 3 => createFailedRequest(CALLED_FAILED_MESSAGE)
      case _ => Json.obj(
        "channel" -> channel,
        "text" -> "",
        "blocks" -> Json.arr(
          Json.obj(
            "type" -> "section",
            "text" -> Json.obj(
              "type" -> "mrkdwn",
              "text" -> CALL_MESSAGE
            )
          )
        )
      )
    }
  }
}


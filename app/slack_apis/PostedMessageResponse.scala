package slack_apis

import play.api.libs.json.{ Json, Reads }

case class PostedMessageResponse(message: PostedText)

object PostedMessageResponse {
  implicit val reads: Reads[PostedMessageResponse] =
    Json.reads[PostedMessageResponse]
}

case class PostedText(text: String)

object PostedText {
  implicit val reads: Reads[PostedText] =
    Json.reads[PostedText]
}
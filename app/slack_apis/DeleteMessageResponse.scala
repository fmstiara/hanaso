package slack_apis

import play.api.libs.json.{Json, Reads}

case class DeleteMessageResponse(ok: Boolean)

object DeleteMessageResponse {
  implicit val reads: Reads[DeleteMessageResponse] =
    Json.reads[DeleteMessageResponse]
}


package slack_apis

import play.api.libs.json.{ Json, Reads }

case class SlackErrorResponse(error: String)

object SlackErrorResponse {
  implicit val reads: Reads[SlackErrorResponse] = Json.reads[SlackErrorResponse]
}
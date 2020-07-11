package teams_apis

import play.api.libs.json.{Json, Reads}

case class TeamsResponse(startDateTime: String, endDateTime: String, joinUrl: String)

object TeamsResponse {
  implicit val reads: Reads[TeamsResponse] =
    Json.reads[TeamsResponse]
}


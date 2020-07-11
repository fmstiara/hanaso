package teams_apis

import play.api.libs.json.{Json, Reads}

case class TeamsErrorResponse(error: String)

object TeamsErrorResponse {
  implicit val reads: Reads[TeamsErrorResponse] = Json.reads[TeamsErrorResponse]
}
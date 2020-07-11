package teams_apis

import play.api.libs.json.JsonNaming.SnakeCase
import play.api.libs.json.{ Json, JsonConfiguration, Writes }

case class TeamsRequest(subject: String = "Hanaso")

object TeamsRequest {
  implicit val config = JsonConfiguration(SnakeCase)
  implicit val writes: Writes[TeamsRequest] =
    Json.writes[TeamsRequest]
}

package teams_apis

import play.api.libs.json.{Json, Reads}

case class TokenResponse(token_type: String, scope: String, access_token: String, refresh_token: String, id_token: String, expires_in: Int, ext_expires_in: Int)

object TokenResponse {
  implicit val reads: Reads[TokenResponse] =
    Json.reads[TokenResponse]
}


package teams_apis

import play.api.libs.json.{
  JsError, JsSuccess, JsValue, Json, Reads
}
import teams_apis.errors.InvalidParamException

import scala.util.{
  Failure, Success, Try
}

trait TeamsResponseJsonParsable {
  def parse[A](responseJson: JsValue)(implicit reads: Reads[A]): Try[A] = {
    Json.fromJson[A](responseJson) match {
      case JsSuccess(result, _) =>
        Success(result)
      case _: JsError =>
        Failure(
          new RuntimeException(
            "Failed to parse JSON"
          )
        )
    }
  }
}


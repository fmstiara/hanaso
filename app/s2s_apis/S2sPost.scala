package s2s_apis

import play.api.Logger
import play.api.libs.json._
import play.api.libs.ws.{WSClient, WSResponse}

import scala.concurrent.Future

class S2sPost(val ws: WSClient) extends S2sPostable {
  def postValue(requestParamJson: JsValue): Future[WSResponse] = {
    post("slack_post", requestParamJson, ws)
  }
}

package pw.anisimov.tinker.web

import pw.anisimov.tinker.api._
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol

/**
 * Trait for JSON marshalling protocol
 */
trait JsonProtocol extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val AddNodeFormat = jsonFormat1(AddNode)
  implicit val ChangeTimeoutFormat = jsonFormat1(ChangeTimeout)
  implicit val NodeStatusFormat = jsonFormat2(NodeStatus)
  implicit val NodesStatusFormat = jsonFormat2(NodesStatus)
  implicit val RemoveNodeFormat = jsonFormat1(RemoveNode)

  implicit val GeneralResponseFormat = jsonFormat1(GeneralResponse)
  implicit val NodesStatusResponseFormat = jsonFormat1(NodesStatusResponse)
}

sealed trait RestResponse

case class GeneralResponse(status: Boolean) extends RestResponse

case class NodesStatusResponse(nodesStatus: NodesStatus) extends RestResponse

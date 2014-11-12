package pw.anisimov.tinker.api

/**
 * Container for node status message
 * @param nodeStatus node status collection
 * @param timeout current timeout of the nodes
 */
case class NodesStatus(nodeStatus: List[NodeStatus], timeout: Long)

/**
 * Status of the current node
 * @param msgPerSecond messages per second node can digest
 * @param name name of the node
 */
case class NodeStatus(msgPerSecond: Long, name: String)

/**
 * Object to ask for current node statuses
 */
case object GetNodesStatus
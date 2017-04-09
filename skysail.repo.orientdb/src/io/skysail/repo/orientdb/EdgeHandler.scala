package io.skysail.repo.orientdb

import com.tinkerpop.blueprints.impls.orient._
import com.tinkerpop.blueprints.Vertex
import java.util.Collection
import java.util.stream.StreamSupport
import com.tinkerpop.blueprints.Direction
import java.util.stream.Collectors
import scala.collection.JavaConverters._

class EdgeHandler(fn: Function[Any, OrientVertex], db: OrientGraph) {
  def handleEdges(entity: Any, vertex: Vertex, properties: Map[String, Object], key: String): Unit = {
    val field = entity.getClass().getDeclaredField(key);
    val theType = field.getType();
    val edges = properties.get(key);
    if (edges == null) {
      return ;
    }
    if (classOf[Collection[_]].isAssignableFrom(theType)) {
      val method = entity.getClass().getMethod("get" + key.substring(0, 1).toUpperCase() + key.substring(1));
      val references = method.invoke(entity).asInstanceOf[Collection[_]]
      val edgesToDelete = StreamSupport.stream(vertex.getEdges(Direction.OUT, key).spliterator(), false)
        .collect(Collectors.toList());
      for (referencedObject <- references.asScala) {
        //                if (referencedObject.getId() != null) {
        //                    val edge = match(vertex, db.getVertex(referencedObject.getId()), edgesToDelete, key);
        //                    if (edge.isPresent()) {
        //                        edgesToDelete.remove(edge.get());
        //                    }
        //                }
      }

      //            edgesToDelete.stream().forEach(edge -> {
      //                Vertex targetVertex = edge.getVertex(Direction.IN);
      //                Iterable<Edge> vertexInEdges = targetVertex.getEdges(Direction.IN);
      //                Iterator<Edge> iterator = vertexInEdges.iterator();
      //                iterator.next();
      //                db.removeEdge(edge);
      //                if (!iterator.hasNext()) {
      //                    db.removeVertex(targetVertex);
      //                }
      //            });
      //
      //            for (Entity referencedObject : references) {
      //                OrientVertex target = fn.apply(referencedObject);
      //                Iterable<Edge> existingEdges = vertex.getEdges(Direction.OUT, key);
      //                if (edgeDoesNotExistYet(existingEdges, vertex, target, key)) {
      //                    db.addEdge(null, vertex, target, key);
      //                }
      //            }
    } else if (classOf[String].isAssignableFrom(theType)) {
      //            removeOldReferences(vertex, key);
      //            addReference(vertex, properties, key, edges);
    }

  }
}
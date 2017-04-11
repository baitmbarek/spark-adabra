package cogroup

import model._
import org.apache.spark.rdd.RDD

object CoGroupThings {
  def groupWebEvents(pagesNavRDD: RDD[PageNav], pagesActionsRDD: RDD[PageAction], lignesCommandeRDD: RDD[LigneCommande]): RDD[CoGroupedSession] = {

    pagesNavRDD.keyBy(_.idSession)
      .cogroup(pagesActionsRDD.keyBy(_.idSession), lignesCommandeRDD.keyBy(_.idSession))
      .map(g => CoGroupedSession(g._1, g._2._2.find(_.typeAction == "AUTHENTICATION").map(_.clientId), g._2._1.toSeq, g._2._2.toSeq, g._2._3.toSeq))

  }

  def groupWebBM(groupedSessionsRDD : RDD[CoGroupedSession], ticketsCaisseRDD: RDD[TicketCaisse]) = {
    groupedSessionsRDD.keyBy(cg => cg.idClient.map(ic => "C-"+ic).getOrElse("S-"+cg.idSession))
      .cogroup(ticketsCaisseRDD.keyBy(tc => Option(tc.idClient).filter(_.nonEmpty).map(ic => tc.idTicket.take(2)+ic).getOrElse(tc.idTicket)))
      .map(g => CoGroupedData(g._1, g._2._1.toSeq, g._2._2.toSeq))
  }

}

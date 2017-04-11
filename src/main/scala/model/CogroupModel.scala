package model

case class CoGroupedSession(
                           idSession: String,
                           idClient: Option[String],
                           pagesNav: Seq[PageNav],
                           pagesActions: Seq[PageAction],
                           lignesCommande: Seq[LigneCommande]
                             )

case class CoGroupedData(
                        idClient: String,
                        sessions: Seq[CoGroupedSession],
                        tickets: Seq[TicketCaisse]
                          )
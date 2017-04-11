package model

import java.sql.Timestamp

case class StgClient(idClient: String, visitesWeb: Seq[StgVisiteWeb], ticketsCaisse: Seq[StgTicketCaisse])
case class StgVisiteWeb(idSession: String, pagesVisitees: Seq[StgPageCategorie], commande: Option[StgCommande])
case class StgPageCategorie(horotime: Timestamp, section: String, sousCategories: Seq[StgPageSousCategorie], actions: Seq[StgAction])
case class StgPageSousCategorie(horotime: Timestamp, sousSection: String, produits: Seq[StgPageProduit], actions:Seq[StgAction])
case class StgPageProduit(horotime: Timestamp, produit: String, coProduit: String, actions: Seq[StgAction])
case class StgAction(horotime: Timestamp, typeAction: String)

case class StgCommande(idCommande: String,
                        idMagasin: String,
                        modeLivraison: String,
                        lignesCommande: Seq[StgLigneCommande],
                        montantTicket: Double,
                        horotime: Timestamp
                        )

case class StgLigneCommande(produitCategorie: String,
                            produitSousCategorie: String,
                            produit: String,
                            produitCode: String,
                            prixUnitaire: Double,
                            quantite: Int,
                            prixLigneCommande: Double,
                            reductionLigneCommande: Double)

case class StgTicketCaisse(idTicket: String,
                           idMagasin: String,
                           idEmploye: String,
                            lignesCommande: Seq[StgLigneCommande],
                           reductionsTicket: Double,
                           montantTicket: Double,
                           horotime: Timestamp)

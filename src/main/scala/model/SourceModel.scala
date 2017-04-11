package model

case class PageNav(horotime: String, horodate: String, idSession: String, clientId: String, typePage: String, section: String, page: String, produit: String, coProduit: String)

case class PageAction(horotime: String, horodate: String, idSession: String, clientId: String, typeAction: String, coProduit: String)

case class LigneCommande(idCommande: String,
                    idClient: String,
                        idSession: String,
                    idMagasin: String,
                    modeLivraison: String,
                    produitCategorie: String,
                    produitSousCategorie: String,
                    produit: String,
                    produitCode: String,
                    prixUnitaire: String,
                    quantite: String,
                    prixLigneCommande: String,
                    reductionLigneCommande: String,
                    montantTicket: String,
                    horotime: String,
                    horodate: String
                     )

case class TicketCaisse(idTicket: String,
                     idClient: String,
                     idMagasin: String,
                     idEmploye: String,
                     produitCategorie: String,
                     produitSousCategorie: String,
                     produit: String,
                     produitCode: String,
                     prixUnitaire: String,
                     quantite: String,
                     prixCommande: String,
                     reductionCommande: String,
                     reductionsTicket: String,
                     montantTicket: String,
                     horotime: String,
                     horodate: String
                      )

package model

import java.sql.Timestamp

case class HClient(idClient: String,
                    visites: Seq[HVisite])


case class HVisite(idVisite: String,
                    pagesVisitees: Seq[HPageCategorie],
                    produitsPanier: Seq[HProduit],
                    media: String,
                    commande: Option[HCommande]
                   )

case class HPageCategorie(horotime: Timestamp,
                          categorie: String,
                          sousCategories: Seq[HPageSousCategorie],
                          actions: Seq[HAction])

case class HPageSousCategorie(horotime: Timestamp,
                              sousSection: String,
                              produits: Seq[HPageProduit],
                              actions:Seq[HAction])

case class HPageProduit(horotime: Timestamp, produit: String, coProduit: String, actions: Seq[HAction])

case class HAction(horotime: Timestamp, typeAction: String)

case class HProduit(coProduit: String, liProduit: String, retirePanier: Boolean)

case class HCommande(idCommande: String,
                     modeLivraison: String,
                      magasin: String,
                      employe: Option[String],
                      lignesCommande: Seq[HLigneCommande],
                      montantCommande: Double,
                      horotime: Timestamp
                      )

case class HLigneCommande(produitCategorie: String,
                          produitSousCategorie: String,
                          produit: String,
                          produitCode: String,
                          prixUnitaire: Double,
                          quantite: Int,
                          prixLigneCommande: Double,
                          reductionLigneCommande: Double)
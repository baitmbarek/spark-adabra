package mapping

import java.sql.Timestamp

import model._

object Mapping {
  def enrich(stgClient: StgClient): HClient = {
    val idClient = stgClient.idClient
    
    val visitesWeb: Seq[HVisite] = stgClient.visitesWeb.map(v => {
      val idVisite = "web-"+v.idSession
      val pagesVisitees = v.pagesVisitees.map(pv => HPageCategorie(
        horotime = pv.horotime,
        categorie = pv.section,
        sousCategories = pv.sousCategories.map(sc => {
          HPageSousCategorie(horotime = sc.horotime,
            sousSection = sc.sousSection,
            produits = sc.produits.map(p => {
              HPageProduit(horotime = p.horotime, produit = p.produit, coProduit = p.coProduit, actions = p.actions.map(toHAction))
            }),
            actions = sc.actions.map(toHAction))
        }),
        actions = pv.actions.map(toHAction)
      ))

      HVisite(
        idVisite = idVisite,
        pagesVisitees = pagesVisitees,
        produitsPanier = pagesVisitees.flatMap(_.sousCategories).flatMap(_.produits).map(p => HProduit(coProduit = p.coProduit, liProduit = p.produit, retirePanier = p.actions.exists(_.typeAction.contains("REM")))),
        media = "web",
        commande = v.commande.map(cmd => HCommande(idCommande = cmd.idCommande,
          modeLivraison = cmd.modeLivraison,
          magasin = cmd.idMagasin,
          employe = None,
          lignesCommande = cmd.lignesCommande.map(lc => HLigneCommande(produitCategorie = lc.produitCategorie,
            produitSousCategorie = lc.produitSousCategorie,
            produit = lc.produit,
            produitCode = lc.produitCode,
            prixUnitaire = lc.prixUnitaire,
            quantite = lc.quantite,
            prixLigneCommande = lc.prixLigneCommande,
            reductionLigneCommande = lc.reductionLigneCommande)
          ),
          montantCommande = cmd.montantTicket,
          horotime = cmd.horotime)
        )
      )
    })

    val visitesMagasin: Seq[HVisite] = stgClient.ticketsCaisse.map(t => {
      HVisite(idVisite = t.idTicket,
        pagesVisitees = Nil,
        produitsPanier = t.lignesCommande.map(lc => HProduit(coProduit = lc.produitCode, liProduit = lc.produit, retirePanier = false)),
        media = "Magasin",
        commande = Some(HCommande(
          idCommande = t.idTicket,
          modeLivraison = "Retrait Magasin",
          magasin = t.idMagasin,
          employe = Option(t.idEmploye),
          lignesCommande = t.lignesCommande.map(lc => HLigneCommande(
            produitCategorie = lc.produitCategorie,
            produitSousCategorie = lc.produitSousCategorie,
            produit = lc.produit,
            produitCode = lc.produitCode,
            prixUnitaire = lc.prixUnitaire,
            quantite = lc.quantite,
            prixLigneCommande = lc.prixLigneCommande,
            reductionLigneCommande = lc.reductionLigneCommande
          )),
          montantCommande = t.montantTicket,
          horotime = t.horotime
        ))
      )
    })

    HClient(
      idClient = idClient,
      visites = visitesWeb ++ visitesMagasin
    )
  }

  private def toHAction(action: StgAction): HAction = HAction(action.horotime, action.typeAction)
}

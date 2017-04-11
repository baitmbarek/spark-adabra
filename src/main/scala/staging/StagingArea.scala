package staging

import java.sql.Timestamp

import model._

object StagingArea {

  def structure(cogroupedData: CoGroupedData) = {
    val idClient = cogroupedData.idClient

    StgClient(idClient = idClient, visitesWeb = cogroupedData.sessions.map(buildStgVisiteWeb), ticketsCaisse = cogroupedData.tickets.groupBy(_.idTicket).values.map(buildStgTicketsCaisse).toSeq)
  }

  private def buildStgVisiteWeb(sess: CoGroupedSession): StgVisiteWeb = {
    val idSession = sess.idSession

    val zero = (sess.pagesActions.sortBy(_.horotime), Seq.empty[PageWithAction])
    val pagesWithActions = sess.pagesNav.sortBy(_.horotime).foldLeft(zero)({
      case (state: (Seq[PageAction], Seq[PageWithAction]), p: PageNav) =>

        val newAssociations = PageWithAction(p, state._1.takeWhile(_.horotime.toLong < p.horotime.toLong))
        (state._1.drop(newAssociations.actions.size), state._2 :+ newAssociations)
    })



    val _pagesCategorie = pagesWithActions._2.filter(t => Option(t.pageNav.section).isDefined && !Option(t.pageNav.page).isDefined)
    val _pagesSousCategorie = pagesWithActions._2.filter(t => Option(t.pageNav.page).isDefined && !Option(t.pageNav.produit).isDefined)
    val _pagesProduit = pagesWithActions._2.filter(t => Option(t.pageNav.produit).isDefined)


    val pagesProduit = _pagesProduit.map(pp =>
      pp.pageNav.page -> StgPageProduit(horotime = new Timestamp(pp.pageNav.horotime.toLong),
        produit = pp.pageNav.produit, coProduit = pp.pageNav.coProduit, actions = pp.actions.map(actionToStgAction))
    )
    val pagesSousCategorie = _pagesSousCategorie.map(sc =>
      sc.pageNav.section -> StgPageSousCategorie(horotime = new Timestamp(sc.pageNav.horotime.toLong),
        sousSection = sc.pageNav.page,
        produits = pagesProduit.filter(_._1 == sc.pageNav.page).map(_._2),
        actions = sc.actions.map(actionToStgAction)
      )
    )
    val pagesCategorie = _pagesCategorie.map(pc =>
      StgPageCategorie(horotime = new Timestamp(pc.pageNav.horotime.toLong),
        section = pc.pageNav.section,
        sousCategories = pagesSousCategorie.filter(_._1 == pc.pageNav.section).map(_._2),
        actions = pc.actions.map(actionToStgAction)
      )
    )


    val commande = sess.lignesCommande.groupBy(_.idCommande).values.map(lcs => StgCommande(idCommande = lcs.head.idCommande,
      idMagasin = lcs.head.idMagasin,
      modeLivraison = lcs.head.modeLivraison,
      lignesCommande = lcs.map(ligneCommandeToStg),
      montantTicket = lcs.head.montantTicket.toDouble,
      horotime = new Timestamp(lcs.head.horotime.toLong)
    )).headOption

    StgVisiteWeb(idSession = idSession, pagesVisitees = pagesCategorie, commande = commande)

  }


  private def actionToStgAction(a: PageAction) = StgAction(
    horotime = new Timestamp(a.horotime.toLong), typeAction = a.typeAction
  )
  private def ligneCommandeToStg(lc: LigneCommande) = StgLigneCommande(
    produitCategorie = lc.produitCategorie,
    produitSousCategorie = lc.produitSousCategorie,
    produit = lc.produit,
    produitCode = lc.produitCode,
    prixUnitaire = lc.prixUnitaire.toDouble,
    quantite = lc.quantite.toInt,
    prixLigneCommande = lc.prixLigneCommande.toDouble,
    reductionLigneCommande = lc.reductionLigneCommande.toDouble
  )

  private def buildStgTicketsCaisse(tcs: Seq[TicketCaisse]): StgTicketCaisse = {

    val tc = tcs.head
    StgTicketCaisse(idTicket = tc.idTicket,
      idMagasin = tc.idMagasin,
      idEmploye = tc.idEmploye,
      lignesCommande = tcs.map(t => StgLigneCommande(
        produitCategorie = t.produitCategorie,
        produitSousCategorie = t.produitSousCategorie,
        produit = t.produit,
        produitCode = t.produitCode,
        prixUnitaire = t.prixUnitaire.toDouble,
        quantite = t.quantite.toInt,
        prixLigneCommande = t.prixCommande.toDouble,
        reductionLigneCommande = t.reductionCommande.toDouble
      )),
      reductionsTicket = tc.reductionsTicket.toDouble,
      montantTicket = tc.montantTicket.toDouble,
      horotime = new Timestamp(tc.horotime.toLong)
    )
  }

}

case class PageWithAction(pageNav: PageNav, actions: Seq[PageAction])
package main

import cogroup.CoGroupThings
import mapping.Mapping
import model._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import staging.StagingArea

object Main {
  def main(args: Array[String]) {
    val ss = SparkSession.builder().master("local[*]").appName("Sparkadabra").getOrCreate()
    import ss.implicits._

    def loadCSVs(ss: SparkSession, path: String) = {
      ss.read.format("org.apache.spark.sql.execution.datasources.csv.CSVFileFormat")
        .option("header","true")
        .option("basePath",s"${path}")
        .load(s"${path}/horodate=*/**/*.csv")
    }
    val branch = 0

    val lignesCommandeRDD = loadCSVs(ss, s"data/branch=${branch}/dataset=evt_command_lines/")
      .as[LigneCommande]
      .rdd

    val pagesActionsRDD = loadCSVs(ss, s"data/branch=${branch}/dataset=evt_pages_actions/")
      .as[PageAction]
      .rdd

    val pagesNavsRDD = loadCSVs(ss, s"data/branch=${branch}/dataset=evt_pages_nav/")
      .as[PageNav]
      .rdd

    val ticketsCaisseRDD = loadCSVs(ss, s"data/branch=${branch}/dataset=evt_tickets_caisse/")
      .as[TicketCaisse]
      .rdd

    //STEP 1 : Groupement des éléments WEB à la maille Session
    val webEventsRDD: RDD[CoGroupedSession] = CoGroupThings.groupWebEvents(pagesNavsRDD, pagesActionsRDD, lignesCommandeRDD)

    //STEP 2 : Groupement des éléments WEB et Achats en magasin à la maille Visiteur
    val cogroupedElementsRDD = CoGroupThings.groupWebBM(webEventsRDD, ticketsCaisseRDD)

    //STEP 3 : Phase de Staging (traitements techniques !) : Restructuration de l'information, éventuels nettoyages, typage des données
    val stagedRDD: RDD[StgClient] = cogroupedElementsRDD.map(StagingArea.structure)

    //STEP 4 : Création d'un modèle métier : Modèle unifié et commun aux visites, enrichissements de données, règles de gestion, ...
    val mappedRDD: RDD[HClient] = stagedRDD.map(Mapping.enrich)

    // Ecriture en parquet du modèle métier
    //ss.createDataFrame(mappedRDD).toDF.write.mode("overwrite").parquet("webhdfs://quickstart.cloudera:50070/devoxx/hClient")
    ss.createDataFrame(mappedRDD).toDF.write.mode("overwrite").parquet("data/devoxx/hClient")

    ss.stop

  }
}

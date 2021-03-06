import org.apache.spark.mllib.linalg.Vectors

import org.apache.spark.mllib.clustering.KMeans

import org.apache.spark.sql.functions._
 
val data = sc.textFile("file:///home/hadoop-user/Desktop/IPL/BattingStats.csv")
val header = data.first
val rows = data.filter(l => l != header)


case class CC1(Pos:Int, Player:String, Mat:Int, Inns:Int, NO:Int, Runs:Long, HS:Int, Avg:Double, BF:Long, SR:Double, Hundreds:Int, Fifties:Int, Fours:Int, Sixes:Int)

val allSplit = rows.map(line => line.split(","))



val allData = allSplit.map( p => CC1( p(0).toInt, p(1).toString, p(2).trim.toInt, p(3).trim.toInt, p(4).trim.toInt, p(5).trim.toLong, p(6).trim.toInt, p(7).trim.toDouble, p(8).trim.toLong, p(9).trim.toDouble, p(10).trim.toInt, p(11).trim.toInt, p(12).trim.toInt, p(13).trim.toInt))



val allDF = allData.toDF()


val rowsRDD = allDF.rdd.map(r => (r.getInt(0), r.getString(1), r.getInt(2), r.getInt(3), r.getInt(4), r.getLong(5), r.getInt(6), r.getDouble(7), r.getLong(8), r.getDouble(9), r.getInt(10), r.getInt(11), r.getInt(12), r.getInt(13) ))

rowsRDD.cache()
val vectors = allDF.rdd.map(r => Vectors.dense(r.getInt(3), r.getInt(4), r.getLong(5), r.getInt(6), r.getDouble(9)))

vectors.cache()
val kMeansModel = KMeans.train(vectors, 5, 20)



kMeansModel.clusterCenters.foreach(println)



val predictions = rowsRDD.map{r => (r._2, kMeansModel.predict(Vectors.dense(r._4, r._5, r._6, r._7, r._10) ))}



val predDF = predictions.toDF("Player", "CLUSTER")

val t = allDF.join(predDF, "Player")
t.filter("CLUSTER=0").repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save("file:///home/hadoop-user/Desktop/IPL/Batting/cluster0.csv")
t.filter("CLUSTER=1").repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save("file:///home/hadoop-user/Desktop/IPL/Batting/cluster1.csv")
t.filter("CLUSTER=2").repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save("file:///home/hadoop-user/Desktop/IPL/Batting/cluster2.csv")
t.filter("CLUSTER=3").repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save("file:///home/hadoop-user/Desktop/IPL/Batting/cluster3.csv")
t.filter("CLUSTER=4").repartition(1).write.format("com.databricks.spark.csv").option("header", "true").save("file:///home/hadoop-user/Desktop/IPL/Batting/cluster4.csv")


System.exit(0)

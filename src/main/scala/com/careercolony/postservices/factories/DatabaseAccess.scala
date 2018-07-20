package com.careercolony.postservices.factories

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._
import reactivemongo.api.{DefaultDB, MongoConnection, MongoDriver}
import reactivemongo.bson.{BSONBinary, BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONElement, BSONInteger, BSONObjectID, BSONString, Macros, document}
import reactivemongo.api.collections.bson.BSONCollection
import org.neo4j.driver.v1._
import com.typesafe.config.ConfigFactory
import java.security.MessageDigest

import scala.collection.mutable.MutableList
import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.kafka.{ConsumerMessage, ProducerSettings}
import akka.kafka.scaladsl.Producer
import akka.stream.scaladsl.Source
import akka.stream.ActorMaterializer
import akka.Done
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, ByteArraySerializer, StringDeserializer, StringSerializer}
import java.io.FileOutputStream

import akka.http.scaladsl.model.{HttpResponse, Multipart, StatusCodes}
import akka.stream.Materializer
import akka.util.ByteString
import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings
import akka.http.scaladsl.model.HttpMethods._
import reactivemongo.bson.Subtype.GenericBinarySubtype

import scala.collection.immutable


trait DatabaseAccess {

  val config = ConfigFactory.load("application.conf")

  implicit val kafkasys: ActorSystem = ActorSystem("Post-Akka-Service")
  implicit val kafkamaterializer = ActorMaterializer.create(kafkasys)


  import ExecutionContext.Implicits.global // use any appropriate context

  
  def getFileName(memberID:String, actualFileName: Option[String]): String = {
    import java.util.{Base64, UUID}
    val fileName: String = "profile_avatar"+ memberID.toString
    val filePath: String = "/assets/uploads/avatar/"
    val base64FileName: String = Base64.getEncoder.encodeToString(fileName.getBytes())
    val fileFormat: String = "jpg"
    filePath + base64FileName + "." + fileFormat
  }

  def returnAvatarName(document: BSONDocument): String = {
    val elements: List[BSONElement] = document.elements.toList
    val fileName: Option[String] = elements.find(_.name=="filename").map(_.value.asInstanceOf[BSONString].value)
        "{" + "\n\t\"avatar\": \"" + fileName.getOrElse("None") + "\"" + "\n}"
  }

  

  def processFileNew(fileData: Multipart.FormData, id: Option[Int] = None): Future[String] = {
    val postID: Option[Int] = id
    // Base64 encode
      val text = "this.jpg"
      val bytesEncoded = java.util.Base64.getEncoder.encode(text.getBytes())

      // Base64 decode
      val textDecoded = new String(java.util.Base64.getDecoder.decode(bytesEncoded))
      println(bytesEncoded)
    fileData.parts.mapAsync(1) { bodyPart ⇒
      bodyPart.name match {
        case "file" =>
          val fileName: String = getFileName(postID.toString, bodyPart.filename)
          new java.io.File(fileName).createNewFile()
          val fileOutput: FileOutputStream = new FileOutputStream(fileName)
          bodyPart.entity.dataBytes.runFold(Array.empty[Byte])((array: Array[Byte], byteString: ByteString) => {
            val byteArray: Array[Byte] = byteString.toArray
            fileOutput.write(byteArray)
            array ++ byteArray
          })
            .map(binaryDAta => {
              fileOutput.close()

               BSONDocument(
               //"file" -> BSONBinary(binaryDAta, GenericBinarySubtype),
                "thumbnail_url" -> BSONString("/Users/carlnjoku/image-uploadssss/" + fileName),
                "filename" -> BSONString(fileName)
                
              )

            })
      }
    }.runFold(BSONDocument())((x, y) => x.merge(y))
      .map(bsonData => {
        returnAvatarName(bsonData)
        
      })
  }


 }

object DatabaseAccess extends DatabaseAccess





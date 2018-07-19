package com.careercolony.postservices.routes

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Directive1, Route}

import akka.stream.ActorMaterializer
import com.careercolony.postservices.factories.{DatabaseAccess, ProfileBgUpload, FileUpload}
import spray.json.DefaultJsonProtocol

import ch.megard.akka.http.cors.CorsDirectives._
import ch.megard.akka.http.cors.CorsSettings

import akka.http.scaladsl.model.HttpMethods._
import scala.collection.immutable

import scala.collection.mutable.MutableList;
import spray.json._;

import java.util.concurrent.TimeUnit

import java.io.FileOutputStream
import java.util.UUID

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpResponse, Multipart, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.Materializer
import akka.util.ByteString
import scala.concurrent.ExecutionContextExecutor




object PostJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  
  
}

trait Service extends DatabaseAccess with ProfileBgUpload with FileUpload {

  import PostJsonSupport._

  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit def executor: ExecutionContextExecutor
  val logger = Logging(system, getClass)

  implicit def myExceptionHandler = {
    ExceptionHandler {
      case e: ArithmeticException =>
        extractUri { uri =>
          complete(HttpResponse(StatusCodes.InternalServerError,
            entity = s"Data is not persisted and something went wrong"))
        }
    }
  }


  val settings = CorsSettings.defaultSettings.copy(allowedMethods = immutable.Seq(GET, PUT, POST, HEAD, OPTIONS))
  val userRoutes: Route = cors(settings){
    post {
      //path("new-post") {
      path("upload" / "avatar" / "memberID"/ IntNumber) { memberID =>
        (post & entity(as[Multipart.FormData])) { fileData =>
        complete {
          processFileNew(fileData, Some(memberID)).map { response =>
            HttpResponse(StatusCodes.OK, entity = response)
            }.recover {
              case ex: Exception => HttpResponse(StatusCodes.InternalServerError, entity = "Error in file uploading")
            }
          }
        }
      } ~ path("upload" / "profileBg" / "memberID" / IntNumber) { memberID =>
        (post & entity(as[Multipart.FormData])) { fileData =>
          complete {
            processProfileBg(fileData, Some(memberID)).map { response =>
              HttpResponse(StatusCodes.OK, entity = response)
            }.recover {
              case ex: Exception => HttpResponse(StatusCodes.InternalServerError, entity = "Error in file uploading")
            }
          }
        }
      } ~ path("image" / "upload" / "file") {
        (post & entity(as[Multipart.FormData])) { fileData =>
        complete {
          processFile(fileData).map { response =>
            HttpResponse(StatusCodes.OK, entity = response)
            }.recover {
              case ex: Exception => HttpResponse(StatusCodes.InternalServerError, entity = "Error in file uploading")
            }
          }
        }
      } ~ path("video" / "upload" / "file") {
        (post & entity(as[Multipart.FormData])) { fileData =>
        complete {
          processFileNew(fileData).map { response =>
            HttpResponse(StatusCodes.OK, entity = response)
            }.recover {
              case ex: Exception => HttpResponse(StatusCodes.InternalServerError, entity = "Error in file uploading")
            }
          }
        }
      } 
    }

  }
}

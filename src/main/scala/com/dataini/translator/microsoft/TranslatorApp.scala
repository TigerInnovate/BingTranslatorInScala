package com.dataini.translator.microsoft

/**
 * Created by guanwang on 10/8/14.
 */

import scalaj.http.{HttpOptions, Http}
import net.liftweb.json._

object TranslatorApp {


  def main(args: Array[String]) {

    val translated = args.length match {
      case l:Int if l > 0 => Translator.translate(args(0))
      case _ => ""
    }
    //"古老斑驳的石库门，高脚杯、咖啡吧和老唱片里都流淌着香艳的上海情，去看看小洋楼的小资情调，再品品地道上海菜中的上海味道。"

    println((translated))

  }

  object Translator {

    def translate(input: String) = {

      val headerValue = "Bearer " + AdminAccessToken.getAccessToken

      val res = Http("http://api.microsofttranslator.com/v2/Http.svc/Translate")
        .params("text" -> input,
          "from" -> "zh-CN",
          "to" -> "en")
        .headers("Authorization" -> headerValue)
        .options(HttpOptions.connTimeout(10000), HttpOptions.readTimeout(10000))
        .asXml

      res.text

    }
  }

  object AdminAccessToken {

    case class TokenResult(token_type: String, access_token: String, expires_in: String, scope: String)

    def getAccessToken: String = {

      val response = Http.post("https://datamarket.accesscontrol.windows.net/v2/OAuth2-13")
        .params("grant_type" -> "client_credentials",
          "client_id" -> "chinese_spot_trans",
          "client_secret" -> "k7YvXYgGABYJCOWgpWOGWXLciIcZaGatxRivcwWefi0=",
          "scope" -> "http://api.microsofttranslator.com")
        .options(HttpOptions.connTimeout(10000), HttpOptions.readTimeout(10000))
        .asString

      implicit val formats = DefaultFormats

      val r = parse(response).extract[TokenResult]

      r.access_token
    }
  }

}

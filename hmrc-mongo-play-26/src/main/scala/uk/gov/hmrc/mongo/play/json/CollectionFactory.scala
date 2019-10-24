/*
 * Copyright 2019 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.mongo.play.json

import org.bson.codecs.configuration.CodecRegistries
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{MongoCollection, MongoDatabase}
import play.api.libs.json.Format

import scala.reflect.ClassTag

trait CollectionFactory {

  def collection[A: ClassTag](
    db            : MongoDatabase,
    collectionName: String,
    domainFormat  : Format[A],
    extraFormats  : Seq[Format[_]] = Seq.empty
    ): MongoCollection[A] = {
      val codices =
        Codecs.playFormatCodec(domainFormat) :: extraFormats.toList.map(f => Codecs.playFormatCodec(f))
      db.getCollection[A](collectionName)
        .withCodecRegistry(
          CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(codices: _*),
            DEFAULT_CODEC_REGISTRY))
    }
}

object CollectionFactory extends CollectionFactory

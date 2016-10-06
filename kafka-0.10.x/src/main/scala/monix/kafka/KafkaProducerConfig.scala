/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://github.com/monixio/monix-kafka
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

package monix.kafka

import java.io.File
import java.util.Properties
import com.typesafe.config.{Config, ConfigFactory}
import monix.kafka.config._
import scala.concurrent.duration._

/** The Kafka Producer config.
  *
  * For the official documentation on the available configuration
  * options, see
  * [[https://kafka.apache.org/documentation.html#producerconfigs Producer Configs]]
  * on `kafka.apache.org`.
  *
  * @param bootstrapServers is the `bootstrap.servers` setting
  *        and represents the list of servers to connect to.
  *
  * @param acks is the `acks` setting and represents
  *        the number of acknowledgments the producer requires the leader to
  *        have received before considering a request complete.
  *        See [[monix.kafka.config.Acks Acks]].
  *
  * @param bufferMemoryInBytes is the `buffer.memory` setting and
  *        represents the total bytes of memory the producer
  *        can use to buffer records waiting to be sent to the server.
  *
  * @param compressionType is the `compression.type` setting and specifies
  *        what compression algorithm to apply to all the generated data
  *        by the producer. The default is none (no compression applied).
  *
  * @param retries is the `retries` setting. A value greater than zero will
  *        cause the client to resend any record whose send fails with
  *        a potentially transient error.
  *
  * @param sslKeyPassword is the `ssl.key.password` setting and represents
  *        the password of the private key in the key store file.
  *        This is optional for client.
  *
  * @param sslKeyStorePassword is the `ssl.keystore.password` setting,
  *        being the password of the private key in the key store file.
  *        This is optional for client.
  *
  * @param sslKeyStoreLocation is the `ssl.keystore.location` setting and
  *        represents the location of the key store file. This is optional
  *        for client and can be used for two-way authentication for client.
  *
  * @param sslTrustStoreLocation is the `ssl.truststore.location` setting
  *        and is the location of the trust store file.
  *
  * @param sslTrustStorePassword is the `ssl.truststore.password` setting
  *        and is the password for the trust store file.
  *
  * @param batchSizeInBytes is the `batch.size` setting.
  *        The producer will attempt to batch records together into fewer
  *        requests whenever multiple records are being sent to the
  *        same partition. This setting specifies the maximum number of
  *        records to batch together.
  *
  * @param clientId is the `client.id` setting,
  *        an id string to pass to the server when making requests.
  *        The purpose of this is to be able to track the source of
  *        requests beyond just ip/port by allowing a logical application
  *        name to be included in server-side request logging.
  *
  * @param connectionsMaxIdleTime is the `connections.max.idle.ms` setting
  *        and specifies how much time to wait before closing idle connections.
  *
  * @param lingerTime is the `linger.ms` setting
  *        and specifies to buffer records for more efficient batching,
  *        up to the maximum batch size or for the maximum `lingerTime`.
  *        If zero, then no buffering will happen, but if different
  *        from zero, then records will be delayed in absence of load.
  *
  * @param maxBlockTime is the `max.block.ms` setting.
  *        The configuration controls how long `KafkaProducer.send()` and
  *        `KafkaProducer.partitionsFor()` will block. These methods can be
  *        blocked either because the buffer is full or metadata unavailable.
  *
  * @param maxRequestSizeInBytes is the `max.request.size` setting
  *        and represents the maximum size of a request in bytes.
  *        This is also effectively a cap on the maximum record size.
  *
  * @param partitionerClass is the `partitioner.class` setting
  *        and represents a class that implements the
  *        `org.apache.kafka.clients.producer.Partitioner` interface.
  *
  * @param receiveBufferInBytes is the `receive.buffer.bytes` setting
  *        being the size of the TCP receive buffer (SO_RCVBUF) to use
  *        when reading data.
  *
  * @param requestTimeout is the `request.timeout.ms` setting,
  *        a configuration the controls the maximum amount of time
  *        the client will wait for the response of a request.
  *
  * @param saslKerberosServiceName is the `sasl.kerberos.service.name` setting,
  *        being the Kerberos principal name that Kafka runs as.
  *
  * @param saslMechanism is the `sasl.mechanism` setting, being the SASL
  *        mechanism used for client connections. This may be any mechanism
  *        for which a security provider is available.
  *
  * @param securityProtocol is the `security.protocol` setting,
  *        being the protocol used to communicate with brokers.
  *
  * @param sendBufferInBytes is the `send.buffer.bytes` setting,
  *        being the size of the TCP send buffer (SO_SNDBUF) to use
  *        when sending data.
  *
  * @param sslEnabledProtocols is the `ssl.enabled.protocols` setting,
  *        being the list of protocols enabled for SSL connections.
  *
  * @param sslKeystoreType is the `ssl.keystore.type` setting,
  *        being the file format of the key store file.
  *
  * @param sslProtocol is the `ssl.protocol` setting,
  *        being the SSL protocol used to generate the SSLContext.
  *        Default setting is TLS, which is fine for most cases.
  *        Allowed values in recent JVMs are TLS, TLSv1.1 and TLSv1.2. SSL,
  *        SSLv2 and SSLv3 may be supported in older JVMs, but their usage
  *        is discouraged due to known security vulnerabilities.
  *
  * @param sslProvider is the `ssl.provider` setting,
  *        being the name of the security provider used for SSL connections.
  *        Default value is the default security provider of the JVM.
  *
  * @param sslTruststoreType is the `ssl.truststore.type` setting, being
  *        the file format of the trust store file.
  *
  * @param reconnectBackoffTime is the `reconnect.backoff.ms` setting.
  *        The amount of time to wait before attempting to reconnect to a
  *        given host. This avoids repeatedly connecting to a host in a
  *        tight loop. This backoff applies to all requests sent by the
  *        consumer to the broker.
  *
  * @param retryBackoffTime is the `retry.backoff.ms` setting.
  *        The amount of time to wait before attempting to retry a failed
  *        request to a given topic partition. This avoids repeatedly
  *        sending requests in a tight loop under some failure scenarios.
  *
  * @param metadataMaxAge is the `metadata.max.age.ms` setting.
  *        The period of time in milliseconds after which we force a
  *        refresh of metadata even if we haven't seen any partition
  *        leadership changes to proactively discover any new brokers
  *        or partitions.
  *
  * @param monixSinkParallelism is the `monix.producer.sink.parallelism`
  *        setting indicating how many requests the [[KafkaProducerSink]]
  *        can execute in parallel.
  */
case class KafkaProducerConfig(
  bootstrapServers: List[String],
  acks: Acks,
  bufferMemoryInBytes: Int,
  compressionType: CompressionType,
  retries: Int,
  sslKeyPassword: Option[String],
  sslKeyStorePassword: Option[String],
  sslKeyStoreLocation: Option[String],
  sslTrustStoreLocation: Option[String],
  sslTrustStorePassword: Option[String],
  batchSizeInBytes: Int,
  clientId: String,
  connectionsMaxIdleTime: FiniteDuration,
  lingerTime: FiniteDuration,
  maxBlockTime: FiniteDuration,
  maxRequestSizeInBytes: Int,
  partitionerClass: Option[PartitionerName],
  receiveBufferInBytes: Int,
  requestTimeout: FiniteDuration,
  saslKerberosServiceName: Option[String],
  saslMechanism: String,
  securityProtocol: SecurityProtocol,
  sendBufferInBytes: Int,
  sslEnabledProtocols: List[SSLProtocol],
  sslKeystoreType: String,
  sslProtocol: SSLProtocol,
  sslProvider: Option[String],
  sslTruststoreType: String,
  reconnectBackoffTime: FiniteDuration,
  retryBackoffTime: FiniteDuration,
  metadataMaxAge: FiniteDuration,
  monixSinkParallelism: Int) {

  def toProperties: Properties = {
    val props = new Properties()
    for ((k,v) <- toMap; if v != null) props.put(k,v)
    props
  }

  def toMap: Map[String,String] = Map(
    "bootstrap.servers" -> bootstrapServers.mkString(","),
    "acks" -> acks.id,
    "buffer.memory" -> bufferMemoryInBytes.toString,
    "compression.type" -> compressionType.id,
    "retries" -> retries.toString,
    "ssl.key.password" -> sslKeyPassword.orNull,
    "ssl.keystore.password" -> sslKeyStorePassword.orNull,
    "ssl.keystore.location" -> sslKeyStoreLocation.orNull,
    "ssl.truststore.password" -> sslTrustStorePassword.orNull,
    "ssl.truststore.location" -> sslTrustStoreLocation.orNull,
    "batch.size" -> batchSizeInBytes.toString,
    "client.id" -> clientId,
    "connections.max.idle.ms" -> connectionsMaxIdleTime.toMillis.toString,
    "linger.ms" -> lingerTime.toMillis.toString,
    "max.block.ms" -> maxBlockTime.toMillis.toString,
    "max.request.size" -> maxRequestSizeInBytes.toString,
    "partitioner.class" -> partitionerClass.map(_.className).orNull,
    "receive.buffer.bytes" -> receiveBufferInBytes.toString,
    "request.timeout.ms" -> requestTimeout.toMillis.toString,
    "sasl.kerberos.service.name" -> saslKerberosServiceName.orNull,
    "sasl.mechanism" -> saslMechanism,
    "security.protocol" -> securityProtocol.id,
    "send.buffer.bytes" -> sendBufferInBytes.toString,
    "ssl.enabled.protocols" -> sslEnabledProtocols.map(_.id).mkString(","),
    "ssl.keystore.type" -> sslKeystoreType,
    "ssl.protocol" -> sslProtocol.id,
    "ssl.provider" -> sslProvider.orNull,
    "ssl.truststore.type" -> sslTruststoreType,
    "reconnect.backoff.ms" -> reconnectBackoffTime.toMillis.toString,
    "retry.backoff.ms" -> retryBackoffTime.toMillis.toString,
    "metadata.max.age.ms" -> metadataMaxAge.toMillis.toString
  )
}

object KafkaProducerConfig {
  lazy val default: KafkaProducerConfig =
    apply(ConfigFactory.load("monix/kafka/default.conf"))

  def load(): KafkaProducerConfig =
    Option(System.getProperty("config.file")).map(f => new File(f)) match {
      case Some(file) if file.exists() =>
        loadFile(file, includeDefaults = true)
      case None =>
        Option(System.getProperty("config.resource")) match {
          case Some(resource) =>
            loadResource(resource, includeDefaults = true)
          case None =>
            default
        }
    }

  def loadResource(resourceBaseName: String, includeDefaults: Boolean = true): KafkaProducerConfig = {
    def default = ConfigFactory.load("monix/kafka/default.conf")
    val config = ConfigFactory.load(resourceBaseName)
    if (!includeDefaults) apply(config) else
      apply(config.withFallback(default))
  }

  def loadFile(file: File, includeDefaults: Boolean = true): KafkaProducerConfig = {
    def default = ConfigFactory.load("monix/kafka/default.conf")
    val config = ConfigFactory.parseFile(file).resolve()
    if (!includeDefaults) apply(config) else
      apply(config.withFallback(default))
  }

  def apply(config: Config): KafkaProducerConfig =
    apply("kafka", config)

  def apply(rootPath: String, config: Config): KafkaProducerConfig = {
    def getOptString(path: String): Option[String] =
      if (config.hasPath(path)) Option(config.getString(path))
      else None

    KafkaProducerConfig(
      bootstrapServers = config.getString(s"$rootPath.bootstrap.servers").trim.split("\\s*,\\s*").toList,
      acks = Acks(config.getString(s"$rootPath.acks")),
      bufferMemoryInBytes = config.getInt(s"$rootPath.buffer.memory"),
      compressionType = CompressionType(config.getString(s"$rootPath.compression.type")),
      retries = config.getInt(s"$rootPath.retries"),
      sslKeyPassword = getOptString(s"$rootPath.ssl.key.password"),
      sslKeyStorePassword = getOptString(s"$rootPath.ssl.keystore.password"),
      sslKeyStoreLocation = getOptString(s"$rootPath.ssl.keystore.location"),
      sslTrustStorePassword = getOptString(s"$rootPath.ssl.truststore.password"),
      sslTrustStoreLocation = getOptString(s"$rootPath.ssl.truststore.location"),
      batchSizeInBytes = config.getInt(s"$rootPath.batch.size"),
      clientId = config.getString(s"$rootPath.client.id"),
      connectionsMaxIdleTime = config.getInt(s"$rootPath.connections.max.idle.ms").millis,
      lingerTime = config.getInt(s"$rootPath.linger.ms").millis,
      maxBlockTime = config.getInt(s"$rootPath.max.block.ms").millis,
      maxRequestSizeInBytes = config.getInt(s"$rootPath.max.request.size"),
      partitionerClass = getOptString(s"$rootPath.partitioner.class").filter(_.nonEmpty).map(PartitionerName.apply),
      receiveBufferInBytes = config.getInt(s"$rootPath.receive.buffer.bytes"),
      requestTimeout = config.getInt(s"$rootPath.request.timeout.ms").millis,
      saslKerberosServiceName = getOptString(s"$rootPath.sasl.kerberos.service.name"),
      saslMechanism = config.getString(s"$rootPath.sasl.mechanism"),
      securityProtocol = SecurityProtocol(config.getString(s"$rootPath.security.protocol")),
      sendBufferInBytes = config.getInt(s"$rootPath.send.buffer.bytes"),
      sslEnabledProtocols = config.getString(s"$rootPath.ssl.enabled.protocols").split("\\s*,\\s*").map(SSLProtocol.apply).toList,
      sslKeystoreType = config.getString(s"$rootPath.ssl.keystore.type"),
      sslProtocol = SSLProtocol(config.getString(s"$rootPath.ssl.protocol")),
      sslProvider = getOptString(s"$rootPath.ssl.provider"),
      sslTruststoreType = config.getString(s"$rootPath.ssl.truststore.type"),
      reconnectBackoffTime = config.getInt(s"$rootPath.reconnect.backoff.ms").millis,
      retryBackoffTime = config.getInt(s"$rootPath.retry.backoff.ms").millis,
      metadataMaxAge = config.getInt(s"$rootPath.metadata.max.age.ms").millis,
      monixSinkParallelism = config.getInt(s"$rootPath.monix.producer.sink.parallelism")
    )
  }
}

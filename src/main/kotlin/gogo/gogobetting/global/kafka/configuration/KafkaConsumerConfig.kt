package gogo.gogobetting.global.kafka.configuration

import gogo.gogobetting.global.kafka.consumer.BatchAdditionTempPointFailedConsumer
import gogo.gogobetting.global.kafka.consumer.BatchCancelDeleteTempPointFailedConsumer
import gogo.gogobetting.global.kafka.consumer.MatchBettingFailedConsumer
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.listener.AcknowledgingMessageListener

@EnableKafka
@Configuration
class KafkaConsumerConfig(
    private val kafkaProperties: KafkaProperties
) {

    @Bean
    fun matchBettingFailedEventListenerContainerFactory(listener: MatchBettingFailedConsumer): ConcurrentKafkaListenerContainerFactory<String, String> =
        makeFactory(listener)

    @Bean
    fun batchAdditionTempPointFailedEventListenerContainerFactory(listener: BatchAdditionTempPointFailedConsumer): ConcurrentKafkaListenerContainerFactory<String, String> =
        makeFactory(listener)

    @Bean
    fun batchCancelDeleteTempPointFailedEventListenerContainerFactory(listener: BatchCancelDeleteTempPointFailedConsumer): ConcurrentKafkaListenerContainerFactory<String, String> =
        makeFactory(listener)

    private fun makeFactory(listener: AcknowledgingMessageListener<String, String>): ConcurrentKafkaListenerContainerFactory<String, String> {
        return ConcurrentKafkaListenerContainerFactory<String, String>().apply {
            consumerFactory = consumerFactory()
            containerProperties.ackMode = kafkaProperties.listener.ackMode
            setConcurrency(3)
            containerProperties.messageListener = listener
            containerProperties.pollTimeout = 5000
        }
    }

    private fun consumerFactory(): ConsumerFactory<String, String> {
        return DefaultKafkaConsumerFactory(kafkaProperties.buildConsumerProperties(null))
    }
}

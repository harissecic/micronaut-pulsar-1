/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.pulsar

import io.micronaut.pulsar.annotation.PulsarReader
import org.apache.pulsar.client.api.PulsarClient
import org.apache.pulsar.client.api.Reader
import org.apache.pulsar.client.api.Schema
import spock.lang.Stepwise

import javax.inject.Singleton
import java.util.concurrent.TimeUnit

@Stepwise
class PulsarReaderSpec extends PulsarAwareTest {

    static final String PULSAR_READER_TEST_TOPIC = "public/default/simple-reader"

    static {
        PulsarDefaultContainer.createNonPartitionedTopic(PulsarReaderSpec.PULSAR_READER_TEST_TOPIC)
    }

    @Singleton
    static class ReaderRequester {
        private Reader<String> stringReader

        ReaderRequester(@PulsarReader(PulsarReaderSpec.PULSAR_READER_TEST_TOPIC) Reader<String> stringReader) {
            this.stringReader = stringReader
        }
    }

    void "test simple reader"() {
        given:
        def topic = "persistent://$PulsarReaderSpec.PULSAR_READER_TEST_TOPIC"
        def producer = context.getBean(PulsarClient).newProducer(Schema.STRING).topic(topic)
                .producerName("string-producer").create()
        def stringReader = context.getBean(ReaderRequester.class).stringReader
        def message = "This is a message"
        def messageId = producer.send(message)

        when:
        def receivedMessage = stringReader.readNext(60, TimeUnit.SECONDS)

        then:
        messageId == receivedMessage.messageId
        message == receivedMessage.value

        cleanup:
        stringReader.close()
        producer.close()
    }
}

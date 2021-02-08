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

import io.micronaut.context.ApplicationContext
import io.micronaut.core.util.CollectionUtils
import io.micronaut.core.util.StringUtils
import io.micronaut.runtime.server.EmbeddedServer
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

abstract class PulsarAwareTest extends Specification {

    @Shared
    @AutoCleanup
    ApplicationContext context

    @Shared
    @AutoCleanup
    EmbeddedServer embeddedServer

    @Shared
    @AutoCleanup
    PulsarDefaultContainer pulsarContainer // will trigger close and dispose containers

    def setupSpec() {
        embeddedServer = ApplicationContext.run(EmbeddedServer,
                CollectionUtils.mapOf("pulsar.service-url", PulsarDefaultContainer.PULSAR_CONTAINER.pulsarBrokerUrl),
                StringUtils.EMPTY_STRING_ARRAY
        )
        context = embeddedServer.applicationContext
    }
}

package io.heimui.demo

import io.heimui.core.data.repository.MockHeimScreenRepository
import io.heimui.core.domain.model.component.ContainerComponent
import io.heimui.core.domain.model.component.TextComponent
import io.heimui.core.domain.repository.HeimScreenResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/**
 * Level 4 of the extension model: replacing `HeimScreenRepository` outright.
 *
 * Everything above it — the theme, the providers, the custom components — assumes the SDK does
 * the fetching. It does not have to. The repository is an interface, and swapping it means the
 * SDK never opens a socket: payloads can come from an existing GraphQL or gRPC stack, from
 * assets bundled in the binary, or from a fixture like the one here.
 *
 * That last case is the one worth having in a test suite. UI tests that hit a real server are
 * slow and flaky for reasons that have nothing to do with the code under test; a fixture
 * repository makes a screen's rendering deterministic and offline.
 */
class OfflineRepositoryTest {

    private val fixtures = mapOf(
        "offline/welcome.json" to """
            {
              "id": "offline_welcome",
              "version": "1.0.0",
              "title": "Offline",
              "root": {
                "type": "container",
                "id": "root",
                "padding": { "horizontal": 16, "top": 24 },
                "children": [
                  { "type": "text", "id": "headline", "text": "Served with no network" }
                ]
              }
            }
        """.trimIndent()
    )

    private fun repository() = MockHeimScreenRepository(
        jsonProvider = { screenId -> fixtures[screenId] }
    )

    @Test
    fun `a fixture repository renders a screen with no network at all`() = runTest {
        val result = repository().getScreen("offline/welcome.json").first()

        val screen = assertIs<HeimScreenResult.Success>(result).screen
        assertEquals("offline_welcome", screen.id)

        val root = assertIs<ContainerComponent>(screen.root)
        assertEquals("Served with no network", assertIs<TextComponent>(root.children[0]).text)

        // The payload used per-side padding, so this also pins that the object form survives the
        // whole DTO → domain path, not just the serializer.
        assertEquals(16, root.padding.start)
        assertEquals(24, root.padding.top)
        assertEquals(0, root.padding.bottom)
    }

    @Test
    fun `an unknown screen fails without pretending it succeeded`() = runTest {
        val results = repository().getScreen("offline/missing.json").toList()

        // One terminal emission, and it is an error. A fixture repository that silently returned
        // an empty screen would make a broken test look like a passing one.
        assertEquals(1, results.size)
        assertIs<HeimScreenResult.Error>(results.single())
    }
}

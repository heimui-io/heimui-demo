package io.heimui.demo

import io.heimui.core.domain.model.action.DismissAction
import io.heimui.core.domain.model.action.NavigateAction
import io.heimui.core.domain.model.action.OpenUrlAction
import io.heimui.core.domain.model.action.ShowSnackbarAction
import io.heimui.demo.domain.model.DemoDestination
import io.heimui.demo.presentation.DemoNavigationViewModel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

/**
 * Navigation is the integration seam with HeimUI, so it is the part of the app most worth testing.
 * None of this needs a device: the ViewModel exists precisely so the flow can be exercised without
 * a Compose harness.
 */
class DemoNavigationViewModelTest {

    private fun viewModel() = DemoNavigationViewModel().apply { onSplashFinished() }

    @Test
    fun `splash is replaced and never stacked`() {
        val vm = viewModel()
        assertEquals(DemoDestination.Hub, vm.destination.value)
        // Backing out of the hub must not reveal the splash again.
        assertFalse(vm.canNavigateBack)
    }

    @Test
    fun `navigate without an extension opens a vertical`() {
        val vm = viewModel()
        vm.onHeimAction(NavigateAction(screenId = "ecommerce"))

        val destination = assertIs<DemoDestination.Vertical>(vm.destination.value)
        assertEquals("ecommerce", destination.verticalId)
    }

    @Test
    fun `navigate to a json screen carries its params to the destination`() {
        val vm = viewModel()
        vm.onHeimAction(
            NavigateAction(
                screenId = "ecommerce/product_detail.json",
                params = mapOf("product_id" to "sku_neural_x1"),
            )
        )

        val destination = assertIs<DemoDestination.Detail>(vm.destination.value)
        assertEquals("ecommerce/product_detail.json", destination.screenId)
        // These become queryParams on the fetch, which is how the backend knows which item.
        assertEquals("sku_neural_x1", destination.params["product_id"])
    }

    @Test
    fun `back returns to where the screen was opened from rather than the hub`() {
        val vm = viewModel()
        vm.onHeimAction(NavigateAction(screenId = "ecommerce"))
        vm.onHeimAction(NavigateAction(screenId = "ecommerce/product_detail.json"))

        vm.onBack()

        // Regression: a single "current destination" jumped straight back to the hub, skipping
        // the vertical the detail was opened from.
        val destination = assertIs<DemoDestination.Vertical>(vm.destination.value)
        assertEquals("ecommerce", destination.verticalId)
    }

    @Test
    fun `dismiss pops the stack exactly like back`() {
        val vm = viewModel()
        vm.onHeimAction(NavigateAction(screenId = "fintech"))
        vm.onHeimAction(NavigateAction(screenId = "ecommerce/product_detail.json"))

        // The payload says "close this screen". It cannot know what is underneath — the host does.
        vm.onHeimAction(DismissAction)

        assertEquals("fintech", assertIs<DemoDestination.Vertical>(vm.destination.value).verticalId)
    }

    @Test
    fun `back from the hub keeps the hub rather than emptying the stack`() {
        val vm = viewModel()
        vm.onBack()
        assertEquals(DemoDestination.Hub, vm.destination.value)
    }

    @Test
    fun `a deep link navigates just like a navigate action`() {
        val vm = viewModel()
        vm.onHeimAction(OpenUrlAction(url = "heimui://showcase/paywall"))

        assertEquals("paywall", assertIs<DemoDestination.Vertical>(vm.destination.value).verticalId)
    }

    @Test
    fun `an external url is left alone`() {
        val vm = viewModel()
        vm.onHeimAction(OpenUrlAction(url = "https://heimui.io/terms"))

        // Opening it is the SDK's job, subject to its scheme policy. Navigation must not react.
        assertEquals(DemoDestination.Hub, vm.destination.value)
    }

    @Test
    fun `actions the SDK already handled do not move navigation`() {
        val vm = viewModel()
        vm.onHeimAction(ShowSnackbarAction(message = "saved"))

        assertEquals(DemoDestination.Hub, vm.destination.value)
    }

    @Test
    fun `navigating twice to the same destination does not stack a duplicate`() {
        val vm = viewModel()
        vm.onHeimAction(NavigateAction(screenId = "food"))
        vm.onHeimAction(NavigateAction(screenId = "food"))

        vm.onBack()

        // A double tap must not require two presses of back to undo.
        assertEquals(DemoDestination.Hub, vm.destination.value)
    }

    @Test
    fun `a blank destination is ignored`() {
        val vm = viewModel()
        vm.onHeimAction(NavigateAction(screenId = "   "))

        assertEquals(DemoDestination.Hub, vm.destination.value)
        assertTrue(vm.backStack.value.size == 1)
    }
}

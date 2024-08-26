package com.emall.net.network.model.addToCartGuestRequest.configurableProducts

import com.emall.net.network.model.addToCartRequest.configurableProducts.ConfigurableItemOption

data class ExtensionAttributesConfigGuest(
    val configurable_item_options: List<ConfigurableItemOption>
)
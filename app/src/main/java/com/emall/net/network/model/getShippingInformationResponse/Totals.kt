package com.emall.net.network.model.getShippingInformationResponse

data class Totals(
    val base_currency_code: String,
    val base_discount_amount: Double,
    val base_grand_total: Double,
    val base_shipping_amount: Double,
    val base_shipping_discount_amount: Double,
    val base_shipping_incl_tax: Double,
    val base_shipping_tax_amount: Double,
    val base_subtotal: Double,
    val base_subtotal_with_discount: Double,
    val base_tax_amount: Double,
    val coupon_code: String,
    val discount_amount: Double,
    val extension_attributes: ExtensionAttributes,
    val grand_total: Double,
    val items: List<Item>,
    val items_qty: Double,
    val quote_currency_code: String,
    val shipping_amount: Double,
    val shipping_discount_amount: Double,
    val shipping_incl_tax: Double,
    val shipping_tax_amount: Double,
    val subtotal: Double,
    val subtotal_incl_tax: Double,
    val subtotal_with_discount: Double,
    val tax_amount: Double,
    val total_segments: ArrayList<TotalSegment>,
    val weee_tax_applied_amount: Any
)
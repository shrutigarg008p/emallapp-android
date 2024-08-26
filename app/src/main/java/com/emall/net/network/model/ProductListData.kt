package com.emall.net.network.model

data class ProductListData (

	val name : String,
	val id : Int,
	val sku : String,
	val price : Double,
	val old_price : Double,
	val img : String,
	val created : String,
	val category_name : String,
	val salable : Boolean
)
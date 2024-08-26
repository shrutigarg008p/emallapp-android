package com.emall.net.network.model.dashboardUsed

import com.emall.net.network.model.dashboard.*
import com.google.gson.annotations.SerializedName

data class DashboardResponseDataForUsedItem(
    val data_best_deal_month: List<DataBestDealMonth>,
    val data_best_deals: List<DataBestDeal>,
    val data_brand_logo_slider: DataBrandLogoSlider,
    val data_flash_sale: List<DataFlashSale>,
    val data_hot_sale: List<DataHotSale>,
    @SerializedName("data_mid-banner-eight")
    val data_mid_banner_eight: ArrayList<DataMidBannerEight>,
    @SerializedName("data_mid-banner-five")
    val data_mid_banner_five: ArrayList<DataMidBannerFive>,
    @SerializedName("data_mid-banner-four")
    val data_mid_banner_four: ArrayList<DataMidBannerFour>,
    @SerializedName("data_mid-banner-seven")
    val data_mid_banner_seven: ArrayList<DataMidBannerSeven>,
    @SerializedName("data_mid-banner-six")
    val data_mid_banner_six: ArrayList<DataMidBannerSix>,
    @SerializedName("data_mid-banner-three")
    val data_mid_banner_three: ArrayList<DataMidBannerThree>,
    @SerializedName("data_mid-banner-two")
    val data_mid_banner_two: ArrayList<DataMidBannerTwo>,// change
    val data_mid_banner_one: List<DataMidBannerOne>,
    val data_popular_search: List<DataPopularSearch>,
    val data_top_offers: List<DataTopOffer>,
    val data_top_ten_selected: List<DataTopTenSelected>,
    val do_not_use: DoNotUse,
    val main_banner: List<MainBanner>,
    val view_all: ViewAll
)
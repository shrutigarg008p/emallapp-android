package com.emall.net.network.api

import com.emall.net.model.RaiseAnIssueQueryParams
import com.emall.net.network.model.ProductListResponse
import com.emall.net.network.model.absher.AbsherRequestParams
import com.emall.net.network.model.absher.AbsherResponse
import com.emall.net.network.model.absher.AbsherVerifyOTPRequestParams
import com.emall.net.network.model.acceptBidding.AddBidToAuctionResponseData
import com.emall.net.network.model.addAddressRequest.AddAddressParams
import com.emall.net.network.model.addAddressResponse.AddAddressResponseData
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequest
import com.emall.net.network.model.addAuctionToWishList.AddAuctionToWishListResponse
import com.emall.net.network.model.addEvaluationToWishList.AddEvaluationToWishListResponse
import com.emall.net.network.model.addProduct.AddProductResponseDataNew
import com.emall.net.network.model.addSellerAddress.AddressParams
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.AddToCartGuestConfigurableParams
import com.emall.net.network.model.addToCartGuestRequest.simpleProducts.AddToCartGuestSimpleParams
import com.emall.net.network.model.addToCartRequest.configurableProducts.AddToCartConfigurableParams
import com.emall.net.network.model.addToCartRequest.simpleProducts.AddToCartSimpleParams
import com.emall.net.network.model.addToCartResponse.configurableProducts.AddToCartConfigurableResponse
import com.emall.net.network.model.addToCartResponse.simpleProducts.AddToCartSimpleResponse
import com.emall.net.network.model.addressList.AddressListResponse
import com.emall.net.network.model.adminToken.AdminTokenParams
import com.emall.net.network.model.auctionDetail.AuctionDetailResponseData
import com.emall.net.network.model.auctionDeviceWonCheckout.AuctionDeviceWonCheckoutResponseData
import com.emall.net.network.model.auctionDevices.AuctionDevicesResponseData
import com.emall.net.network.model.auctionDevicesWon.AuctionDevicesWonResponseData
import com.emall.net.network.model.auctionOrders.AuctionOrderResponse
import com.emall.net.network.model.auctionWishList.AuctionWishListResponseData
import com.emall.net.network.model.auctions.auctionList.AuctionResponseData
import com.emall.net.network.model.auctions.createNewEvaluationForTap.CreateNewEvaluationResponseData
import com.emall.net.network.model.bankList.BankListResponseData
import com.emall.net.network.model.biddingDetails.BiddingResponseData
import com.emall.net.network.model.cancelOrder.CancelOrderResponse
import com.emall.net.network.model.cartRetainRequest.CartRetainParams
import com.emall.net.network.model.cartRetainResponse.CartRetainResponseData
import com.emall.net.network.model.categoryList.CategoryListResponseData
import com.emall.net.network.model.changePasswordRequest.ChangePasswordRequest
import com.emall.net.network.model.changePasswordResponse.ChangePasswordResponse
import com.emall.net.network.model.chat.SendMessage
import com.emall.net.network.model.chat.channelList.ChannelListResponseData
import com.emall.net.network.model.chat.createChannel.CreateChannelResponseData
import com.emall.net.network.model.chat.viewChannelMessages.ChannelMessagesResponseData
import com.emall.net.network.model.codRequest.CodParams
import com.emall.net.network.model.codResponse.CodResponseData
import com.emall.net.network.model.configurableProduct.ConfigurableProductResponse
import com.emall.net.network.model.contactUsRequest.ContactUsRequest
import com.emall.net.network.model.contactUsResponse.ContactUsResponse
import com.emall.net.network.model.createNewPassword.CreateNewPasswordResponse
import com.emall.net.network.model.createdProduct.CreatedProductResponseData
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.dashboard.DashboardResponseData
import com.emall.net.network.model.dashboardUsed.DashboardResponseDataForUsed
import com.emall.net.network.model.ecommerceLogin.LoginRequestParams
import com.emall.net.network.model.ecommerceLoginSignUp.GetCountryDetail
import com.emall.net.network.model.ecommerceLoginSignUp.LoginDataResponse
import com.emall.net.network.model.ecommerceLoginSignUp.SendOTPResponse
import com.emall.net.network.model.ecommerceLoginSignUp.VerifyOTPResponse
import com.emall.net.network.model.evaluationAcceptDetail.AcceptEvaluationResponseData
import com.emall.net.network.model.evaluationDetail.EvaluationDetailResponseData
import com.emall.net.network.model.evaluationDeviceWonCheckout.EvaluationWonCheckoutResponseData
import com.emall.net.network.model.evaluationDevices.EvaluationDevicesResponseData
import com.emall.net.network.model.evaluationDevicesDetail.EvaluationDeviceDetailResponseData
import com.emall.net.network.model.evaluationDevicesWon.EvaluationDevicesWonResponseData
import com.emall.net.network.model.evaluationOrders.EvaluationOrderResponse
import com.emall.net.network.model.evaluationReports.EvaluationReportResponse
import com.emall.net.network.model.evaluationUserAddress.AddressRequestBodyParams
import com.emall.net.network.model.evaluationUserAddress.EvaluationAddressListResponse
import com.emall.net.network.model.evaluationUserAddress.addUpdateNewAddress.EvaluationAddNewAddressResponse
import com.emall.net.network.model.evaluationWishList.EvaluationWishListResponse
import com.emall.net.network.model.faq.FAQResponseData
import com.emall.net.network.model.faqEcomResponse.FaqEcomResponse
import com.emall.net.network.model.filter.*
import com.emall.net.network.model.forgotPassword.ForgotPasswordResponse
import com.emall.net.network.model.getCartItems.CartItemResponse
import com.emall.net.network.model.getCurrencyResponse.GetCurrencyResponseData
import com.emall.net.network.model.getFilterDataRequest.GetFilterDataRequest
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponse
import com.emall.net.network.model.getFilterNavigationListRequest.GetFilterNavigationListRequest
import com.emall.net.network.model.getFilterNavigationListResponse.GetFilterNavigationListResponse
import com.emall.net.network.model.getFilterNavigationRequest.GetFilterNavigationRequest
import com.emall.net.network.model.getFilterNavigationResponse.GetFilterNavigationResponse
import com.emall.net.network.model.getIssueDetail.IssueDetailResponseData
import com.emall.net.network.model.getIssuesList.IssuesListResponseData
import com.emall.net.network.model.getProfileRequest.GetProfileRequest
import com.emall.net.network.model.getProfileResponse.GetProfileResponse
import com.emall.net.network.model.getSerialNumber.SerialNumberWebViewResponse
import com.emall.net.network.model.getShippingInformationRequest.GetShippingInformationParams
import com.emall.net.network.model.getShippingInformationResponse.GetShippingInformationResponseData
import com.emall.net.network.model.getShippingMethodsRequest.GetShippingMethodsParams
import com.emall.net.network.model.getShippingMethodsResponse.GetShippingMethodsResponseData
import com.emall.net.network.model.guestToken.GuestTokenResponse
import com.emall.net.network.model.login.LoginResponse
import com.emall.net.network.model.numberVerifiedRequest.NumberVerifiedRequest
import com.emall.net.network.model.numberVerifiedResponse.NumberVerifiedResponse
import com.emall.net.network.model.orderListResponse.OrderListResponse
import com.emall.net.network.model.payment.PaymentRequestBody
import com.emall.net.network.model.payment.stc.OTPValue
import com.emall.net.network.model.payment.stc.StcPaymentResponseData
import com.emall.net.network.model.payment.tap.tapVerify.TapVerifyResponseData
import com.emall.net.network.model.paymentsList.PaymentListResponseData
import com.emall.net.network.model.productCategories.ProductCategoriesResponseData
import com.emall.net.network.model.productDetail.ProductDetailResponse
import com.emall.net.network.model.products.ProductResponse
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.reOrderRequest.ReOrderRequest
import com.emall.net.network.model.reOrderResponse.ReOrderResponse
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequest
import com.emall.net.network.model.searchRequest.SearchRequestParams
import com.emall.net.network.model.searchResponse.SearchResponse
import com.emall.net.network.model.sellerUserAddress.SellerUserAddressListResponse
import com.emall.net.network.model.sellerUserAddress.getAddressById.SellerAddressByIdResponse
import com.emall.net.network.model.sendOtpResponse.SendOtpResponse
import com.emall.net.network.model.sendTapPaymentRequest.SendTapPaymentParams
import com.emall.net.network.model.sendTapPaymentResponse.SendTapPaymentResponseData
import com.emall.net.network.model.setCurrencyToQuoteRequest.SetCurrencyToQuoteRequest
import com.emall.net.network.model.setCurrencyToQuoteResponse.SetCurrencyToQuoteResponse
import com.emall.net.network.model.setPaymentMethodRequest.SetPaymentMethodParams
import com.emall.net.network.model.shipmentDetail.ShipmentDetailResponseData
import com.emall.net.network.model.shipmentGenerate.ShipmentGenerateResponseData
import com.emall.net.network.model.shipmentList.ShipmentListResponseData
import com.emall.net.network.model.shipmentTrackingDetail.TrackShipmentDetailResponseData
import com.emall.net.network.model.signUpResponse.SignUpResponse
import com.emall.net.network.model.staticPagesResponse.StaticPagesResponseData
import com.emall.net.network.model.stcPaymentOtpRequest.StcPaymentOtpRequestParams
import com.emall.net.network.model.stcPaymentOtpResponse.StcPaymentOtpResponseData
import com.emall.net.network.model.trackShipments.TrackShipmentsResponse
import com.emall.net.network.model.updateAddressRequest.UpdateAddressParams
import com.emall.net.network.model.updateAddressResponse.UpdateAddressResponseData
import com.emall.net.network.model.updateBankDetails.BankRequestParams
import com.emall.net.network.model.updateCartItems.UpdateCartItemParams
import com.emall.net.network.model.updateCartItemsGuest.UpdateCartItemGuestParams
import com.emall.net.network.model.updateProfileRequest.UpdateProfileRequest
import com.emall.net.network.model.updateProfileResponse.UpdateProfileResponse
import com.emall.net.network.model.verifyOtpResponse.VerifyOtpResponse
import com.emall.net.network.model.verifyStcPaymentOtpRequest.VerifyStcPaymentOtpParams
import com.emall.net.network.model.verifyStcPaymentOtpResponse.VerifyStcPaymentOtpResponseData
import com.emall.net.network.model.verifyTapPaymentRequest.VerifyTapPaymentParams
import com.emall.net.network.model.verifyTapPaymentResponse.VerifyTapPaymentResponseData
import com.emall.net.network.model.viewAll.ViewAllResponse
import com.emall.net.network.model.wishlistAddRemoveUntickResponse.WishListAddRemoveUntickResponseData
import com.emall.net.network.model.wishlistResponse.WishlistResponseData
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

@JvmSuppressWildcards
interface ApiService {

    @POST("login_ecommerce")
    suspend fun getRecommerceLogin(
        @Body userData: LoginRequestParams,
        @Header("Content-Language") locale: String
    ): LoginResponse

    // PRODUCT CATEGORIES
    @GET("seller/products/categories")
    suspend fun getProductCategories(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ProductCategoriesResponseData

    // GET BRAND BY CATEGORY
    @GET("common/getBrandByCategory/{id}")
    suspend fun getBrandByCategory(
        @Path("id") categoryId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): BrandsResponseData

    // GET MODEL BY BRAND
    @GET("common/getModelByBrand/{category_id}/{brand_id}")
    suspend fun getModelByBrand(
        @Path("category_id") categoryId: Int,
        @Path("brand_id") brandId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ModelResponseData

    // GET VARIANT BY MODEL
    @GET("common/getVariantByModel/{id}")
    suspend fun getVariantByModel(
        @Path("id") modelId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): VariantsResponseData

    // GET PRODUCT
    @GET("seller/products/create/{id}")
    suspend fun createProduct(
        @Path("id") categoryId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): CreatedProductResponseData

    // ADD PRODUCT TO CATEGORY
    @Multipart
    @POST("seller/products/create/{category_id}")
    suspend fun addProductToCategory(
        @Header("Content-Language") locale: String,
        @Path("category_id") categoryId: Int,
        @Header("Authorization") auth: String,
        @Part("submit_type") submitType: RequestBody,
        @Part("brand_id") brandId: RequestBody,
        @Part("model_id") modelId: RequestBody,
        @Part("variant_id") variantId: RequestBody,
        @Part additionalImages: Array<MultipartBody.Part>,
        @Part("serial_no") serialNumber: RequestBody,
        @Part serialNumberImage: MultipartBody.Part,
        @Part("product_description") productDescription: RequestBody,
        @Part("product_short_description") productShortDescription: RequestBody,
        @PartMap questions: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part("starts_at") startsAt: RequestBody,
    ): AddProductResponseDataNew

    /*// CREATE A NEW AUCTION FOR TAP
    @POST("seller/products/{product_id}/auctions/store")
    suspend fun createNewAuctionForTAP(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Body paymentRequestBody: PaymentRequestBody
    ): CreateNewAuctionTapResponseData

    // CREATE A NEW EVALUATION FOR TAP
    @POST("seller/products/{product_id}/evaluations/store")
    suspend fun createNewEvaluationForTAP(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Body paymentRequestBody: PaymentRequestBody
    ): CreateNewAuctionTapResponseData

    // TAP VERIFY FOR AUCTION
    @POST("seller/products/{product_id}/auctions/{auction_id}/tap_pay_verify")
    suspend fun tapVerifyForAuctions(
        @Path("product_id") productId: Int,
        @Path("auction_id") AuctionId: Int,
        @Header("Authorization") auth: String,
        @Body chargeId: TAPChargeId
    ): TapVerifyResponseData

    // TAP VERIFY FOR EVALUATION
    @POST("seller/products/{product_id}/evaluations/{auction_id}/tap_pay_verify")
    suspend fun tapVerifyForEvaluations(
        @Path("product_id") productId: Int,
        @Path("auction_id") AuctionId: Int,
        @Header("Authorization") auth: String,
        @Body chargeId: TAPChargeId
    ): TapVerifyResponseData

    // TAP RE-PAYMENT FOR AUCTION
    @GET("seller/products/{product_id}/auctions/{auction_id}/tap_pay_regenerate")
    suspend fun tapFailedPaymentForAuction(
        @Path("product_id") productId: Int,
        @Path("auction_id") AuctionId: Int,
        @Header("Authorization") auth: String
    ): CreateNewAuctionTapResponseData

    // TAP RE-PAYMENT FOR EVALUATION
    @GET("seller/products/{product_id}/evaluations/{auction_id}/tap_pay_regenerate")
    suspend fun tapFailedPaymentForEvaluation(
        @Path("product_id") productId: Int,
        @Path("auction_id") AuctionId: Int,
        @Header("Authorization") auth: String
    ): CreateNewEvaluationResponseData
*/
    // CREATE A NEW AUCTION FOR STC
    @POST("seller/products/{product_id}/auctions/store")
    suspend fun createNewAuctionForSTC(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Body paymentRequestBody: PaymentRequestBody,
        @Header("Content-Language") locale: String
    ): StcPaymentResponseData

    // CREATE A NEW EVALUATION FOR STC
    @POST("seller/products/{product_id}/evaluations/store")
    suspend fun createNewEvaluationForSTC(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Body paymentRequestBody: PaymentRequestBody,
        @Header("Content-Language") locale: String
    ): CreateNewEvaluationResponseData

    // STC VERIFY FOR AUCTION
    @POST("seller/products/{product_id}/auctions/{auction_id}/stc_pay_otp_verify")
    suspend fun stcOTPVerifyForAuctions(
        @Path("product_id") productId: Int,
        @Path("auction_id") AuctionId: Int,
        @Header("Authorization") auth: String,
        @Body otpValue: OTPValue,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    // STC VERIFY FOR EVALUATION
    @POST("seller/products/{product_id}/evaluations/{evaluation_id}/stc_pay_otp_verify")
    suspend fun stcOTPVerifyForEvaluations(
        @Path("product_id") productId: Int,
        @Path("evaluation_id") evaluationId: Int,
        @Header("Authorization") auth: String,
        @Body otpValue: OTPValue,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    // STC RE-PAYMENT FOR AUCTION
    @GET("seller/products/{product_id}/auctions/{auction_id}/stc_pay_otp_generate")
    suspend fun stcFailedPaymentForAuction(
        @Path("product_id") productId: Int,
        @Path("auction_id") auctionId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): StcPaymentResponseData

    // STC RE-PAYMENT FOR 3EVALUATION
    @GET("seller/products/{product_id}/evaluations/{evaluation_id}/stc_pay_otp_generate")
    suspend fun stcFailedPaymentForEvaluations(
        @Path("product_id") productId: Int,
        @Path("evaluation_id") evaluationId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): CreateNewEvaluationResponseData

    // GET PRODUCTS FOR AUCTIONS
    @GET("seller/products?auction=1")
    suspend fun getProductsForAuctions(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ProductResponse

    // GET PRODUCTS FOR EVALUATIONS
    @GET("seller/products?evaluation=1")
    suspend fun getProductsForEvaluation(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ProductResponse

    // GET COUNTRIES
    @GET("common/getCountries")
    suspend fun getCountries(
        @Header("Authorization") auth: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Header("Content-Language") locale: String
    ): CountriesResponseData

    // GET STATES BY COUNTRY
    @GET("common/getStatesByCountry/{country_code}")
    suspend fun getStatesByCountry(
        @Header("Authorization") auth: String,
        @Path("country_code") countryCode: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Header("Content-Language") locale: String
    ): StatesResponseData

    // GET SHIPMENT LIST
    @GET("seller/shipments")
    suspend fun getShipmentList(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ShipmentListResponseData

    // AUCTION LIST
    @GET("seller/products/{product_id}/auctions")
    suspend fun getAuctionList(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): AuctionResponseData

    // Auction Details, View Bidding
    @GET("seller/products/{product_id}/auctions/{auction_id}/show")
    suspend fun getBiddingDetails(
        @Path("product_id") productId: Int,
        @Path("auction_id") auctionId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): BiddingResponseData

    //Evaluation List
    @GET("evaluator/evaluation/devices?page=1")
    suspend fun getEvaluationDevices(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationDevicesResponseData

    //Evaluation Address List
    @GET("evaluator/addresses")
    suspend fun getEvaluationAddressList(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationAddressListResponse

    //Evaluation Add Address
    @POST("evaluator/addresses")
    suspend fun addAddressForEvaluationUser(
        @Header("Authorization") auth: String,
        @Body addressRequestBodyParams: AddressRequestBodyParams,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    //Evaluation Update Address
    @POST("evaluator/addresses/{address_id}?_method=PATCH")
    suspend fun updateAddressForEvaluationUser(
        @Path("address_id") addressId: Int,
        @Header("Authorization") auth: String,
        @Body addressRequestBodyParams: AddressRequestBodyParams,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    //Evaluation Delete Address
    @POST("evaluator/addresses/{address_id}?_method=DELETE")
    suspend fun deleteAddressForEvaluationUser(
        @Path("address_id") addressId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    // Seller Address List
    @GET("seller/addresses")
    suspend fun getSellerAddressList(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): SellerUserAddressListResponse

    // Seller Add Address
    @POST("seller/addresses")
    suspend fun addSellerAddress(
        @Header("Authorization") auth: String,
        @Body addressParams: AddressParams,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    //Seller Update Address
    @POST("seller/addresses/{address_id}?_method=PATCH")
    suspend fun updateAddressForSeller(
        @Path("address_id") addressId: Int,
        @Header("Authorization") auth: String,
        @Body addressRequestBodyParams: AddressRequestBodyParams,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    //Seller Delete Address
    @POST("seller/addresses/{address_id}?_method=DELETE")
    suspend fun deleteAddressForSeller(
        @Path("address_id") addressId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationAddNewAddressResponse

    //Seller Address By ID
    @GET("seller/addresses/{address_id}")
    suspend fun getSellerAddressById(
        @Path("address_id") addressId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): SellerAddressByIdResponse

    // Send Absher OTP
    @POST("absher/send?testing_sent={testingParam}")
    suspend fun sendAbsherOTP(
        @Path("testingParam") testingParam: String,
        @Header("Authorization") auth: String,
        @Body absherRequestParams: AbsherRequestParams,
        @Header("Content-Language") locale: String
    ): AbsherResponse

    //Verify Absher OTP
    @POST("seller/absher/verify")
    suspend fun verifyAbsherOTP(
        @Header("Authorization") auth: String,
        @Body absherVerifyOTPRequestParams: AbsherVerifyOTPRequestParams,
        @Header("Content-Language") locale: String
    ): AbsherResponse

    @GET("evaluator/auction/devices")
    suspend fun getAuctionDevices(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): AuctionDevicesResponseData

    @GET("evaluator/auction/devices/{auction_id}/show")
    suspend fun getAuctionDeviceDetail(
        @Header("Authorization") auth: String,
        @Path("auction_id") auctionId: Int,
        @Header("Content-Language") locale: String
    ): AuctionDetailResponseData

    @GET("evaluator/evaluation/devices/{evaluation_id}/show")
    suspend fun getEvaluationDeviceDetail(
        @Header("Authorization") auth: String,
        @Path("evaluation_id") auctionId: Int,
        @Header("Content-Language") locale: String
    ): EvaluationDeviceDetailResponseData

    @POST("evaluator/auction/{auction_id}/bid")
    suspend fun addBidToAuction(
        @Header("Authorization") auth: String,
        @Path("auction_id") auctionId: Int,
        @Body amount: JsonObject,
        @Header("Content-Language") locale: String
    ): AddBidToAuctionResponseData

    @GET("common/getSABankList")
    suspend fun getBankList(
        @Header("Authorization") auth: String,
        @Query("email") email: String,
        @Query("password") password: String,
        @Header("Content-Language") locale: String
    ): BankListResponseData

    @POST("seller/bank")
    suspend fun updateBankDetails(
        @Header("Authorization") auth: String,
        @Body requestParams: BankRequestParams,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("seller/products/acceptABidding/{bidding_id}")
    suspend fun acceptBid(
        @Header("Authorization") auth: String,
        @Path("bidding_id") biddingId: Int,
        @Header("Content-Language") locale: String
    ): AddBidToAuctionResponseData

    @GET("evaluator/auction/deviceswon")
    suspend fun auctionDevicesWon(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): AuctionDevicesWonResponseData

    @POST("seller/shipments/{product_id}")
    suspend fun generateShipment(
        @Path("product_id") productId: Int,
        @Query("address") address: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ShipmentGenerateResponseData

    @GET("evaluator/auction/deviceswon/{auctionWonId}/checkout")
    suspend fun auctionDevicesWonCheckOut(
        @Path("auctionWonId") auctionWonId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): AuctionDeviceWonCheckoutResponseData

    @Multipart
    @POST("evaluator/auction/deviceswon/{auctionWonId}/payment")
    suspend fun auctionDeviceWonPayment(
        @Path("auctionWonId") auctionWonId: Int,
        @Header("Authorization") auth: String,
        @Part("shipping_id") shippingId: RequestBody,
        @Part paymentSlip: MultipartBody.Part,
        @Part("amount") amount: RequestBody,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("evaluator/auction/deviceswon/{device_won_id}/proceedcheckout")
    suspend fun auctionDeviceWonProceedCheckout(
        @Path("device_won_id") deviceWonId: Int,
        @Header("Authorization") auth: String,
        @Query("shipping_id") shippingId: Int,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("seller/shipments/{shipment_id}/show")
    suspend fun getShipmentDetail(
        @Path("shipment_id") shipmentId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ShipmentDetailResponseData

    // AUCTION LIST
    @GET("seller/products/{product_id}/evaluations")
    suspend fun getEvaluationList(
        @Path("product_id") productId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationDetailResponseData

    @GET("seller/products/{product_id}/evaluations/{evaluation_id}/show")
    suspend fun getAcceptedEvaluationDetail(
        @Path("product_id") productId: Int,
        @Path("evaluation_id") evaluationId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): AcceptEvaluationResponseData

    @GET("seller/products/acceptAnEvaluation/{accepted_evaluation_id}")
    suspend fun acceptEvaluation(
        @Path("accepted_evaluation_id") acceptedEvaluationId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("evaluator/evaluation/{evaluation_id}/evaluate")
    suspend fun addEvaluationAmount(
        @Path("evaluation_id") evaluationId: Int,
        @Header("Authorization") auth: String,
        @Body amount: JsonObject,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("evaluator/evaluation/requests")
    suspend fun getEvaluationWonDevices(
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationDevicesWonResponseData

    @POST("evaluator/evaluation/requests/{evaluation_device_won_id}/proceedcheckout")
    suspend fun evaluationDeviceWonProceedCheckout(
        @Path("evaluation_device_won_id") evaluationDeviceWonId: Int,
        @Header("Authorization") auth: String,
        @Query("shipping_id") shippingId: Int,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("evaluator/evaluation/requests/{evaluation_device_won_id}/checkout")
    suspend fun getSellerBankDetailsForEvaluation(
        @Path("evaluation_device_won_id") evaluationDeviceWonId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): EvaluationWonCheckoutResponseData

    @Multipart
    @POST("evaluator/evaluation/requests/{evaluation_device_won_id}/payment")
    suspend fun offlinePaymentForEvaluation(
        @Path("evaluation_device_won_id") evaluationWonId: Int,
        @Header("Authorization") auth: String,
        @Part("shipping_id") shippingId: RequestBody,
        @Part paymentSlip: MultipartBody.Part,
        @Part("amount") amount: RequestBody,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("seller/products/acceptAnEvaluation/{product_id}")
    suspend fun acceptRejectAnEvaluation(
        @Header("Authorization") auth: String,
        @Path("product_id") productId: Int,
        @Query("reeval_action") action: String,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("evaluator/evaluation/requests/{evaluation_request_id}/reevaluate")
    suspend fun reEvaluateRequest(
        @Path("evaluation_request_id") evaluationRequestId: Int,
        @Header("Authorization") auth: String,
        @QueryMap(encoded = true) reEvaluationParams: HashMap<String, String>,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("evaluator/evaluation/requests/{evaluation_request_id}/returnshipping")
    suspend fun returnDeviceShipment(
        @Path("evaluation_request_id") evaluationRequestId: Int,
        @Header("Authorization") auth: String,
        @Header("Content-Language") locale: String
    ): ShipmentGenerateResponseData

    // Raise an issue
    @POST("rc/submitissue")
    suspend fun raiseAnIssue(
        @Header("Authorization") token: String,
        @Body raiseAnIssueQueryParams: RaiseAnIssueQueryParams,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("rc")
    suspend fun getIssues(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): IssuesListResponseData

    @GET("rc/{issue_id}/viewissue")
    suspend fun getIssueDetail(
        @Header("Authorization") token: String,
        @Path("issue_id") issueId: Int,
        @Header("Content-Language") locale: String
    ): IssueDetailResponseData

    @POST("rc/closeissue")
    suspend fun closeIssue(
        @Header("Authorization") token: String,
        @Query("issue_id") issueId: Int,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @POST("seller/products/{product_id}/{item_type}s/{item_id}/change_status")
    suspend fun changeItemStatus(
        @Header("Authorization") token: String,
        @Path("product_id") productId: Int,
        @Path("item_type") itemType: String,
        @Path("item_id") itemId: Int,
        @Query("abandoned_status") abandonedStatus: Int,
        @Query("reason") reason: String,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("chat")
    suspend fun getChannelList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): ChannelListResponseData

    @GET("chat/view_channel/{channel_id}")
    suspend fun getChannelMessages(
        @Header("Authorization") token: String,
        @Path("channel_id") channelId: Int,
        @Header("Content-Language") locale: String
    ): ChannelMessagesResponseData

    @POST("chat/create_channel")
    suspend fun createChannel(
        @Header("Authorization") token: String,
        @Body rawString: JSONObject,
        @Header("Content-Language") locale: String
    ): CreateChannelResponseData

    @POST("chat/messages/{channel_id}")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Path("channel_id") channelId: Int,
        @Body message: SendMessage,
        @Header("Content-Language") locale: String
    ): TapVerifyResponseData

    @GET("payments")
    suspend fun getPaymentsList(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Header("Content-Language") locale: String
    ): PaymentListResponseData

    @GET("cms/serialnumber/{brand_id}")
    suspend fun showSerialNumberPage(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String,
        @Path("brand_id") brandId: Int
    ): SerialNumberWebViewResponse

    @GET("shipments")
    suspend fun getShipmentTrackingList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): TrackShipmentsResponse

    @GET("shipments/track/{shipment_id}")
    suspend fun getShipmentTrackingDetail(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String,
        @Path("shipment_id") shipmentId: Int
    ): TrackShipmentDetailResponseData

    @GET("evaluator/faqs")
    suspend fun getFAQ(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): FAQResponseData

    @GET("evaluator/auction/wishlists")
    suspend fun getAuctionWishList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): AuctionWishListResponseData

    @POST("evaluator/auction/togglewishlist")
    suspend fun toggleAuctionWishList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String,
        @Body productId: JsonObject
    ): AddAuctionToWishListResponse

    @GET("evaluator/auction/orders")
    suspend fun getAuctionOrders(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): AuctionOrderResponse

    @GET("evaluator/evaluation/orders")
    suspend fun getEvaluationOrders(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): EvaluationOrderResponse

    @GET("evaluator/evaluation/reports")
    suspend fun getEvaluationReports(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): EvaluationReportResponse

    @GET("evaluator/evaluation/wishlists")
    suspend fun getEvaluationWishList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String
    ): EvaluationWishListResponse

    @POST("evaluator/evaluation/togglewishlist")
    suspend fun toggleEvaluationToWishList(
        @Header("Authorization") token: String,
        @Header("Content-Language") locale: String,
        @Body productId: JsonObject
    ): AddEvaluationToWishListResponse

    /**
     * E-commerce API
     */

    // E-Commerce Dashboard
    @GET("rest/V1/homepagecontent?")
    suspend fun getDashboardDetails(
        @Query("store_code") storeCode: Int,
        @Query("type") deviceType: String
    ): DashboardResponseData

    // E-Commerce Dashboard
    @GET("rest/V1/homepagecontent?")
    suspend fun getDashboardDetailsForUsed(
        @Query("store_code") storeCode: Int,
        @Query("type") deviceType: String
    ): DashboardResponseDataForUsed

    // PRODUCT CATEGORIES
    @GET("rest/{language}/V1/countrylist")
    suspend fun getCountryDetail(@Path("language") language: String
    ): GetCountryDetail

    // generate OTP (login via OTP)/ change/reset password by mobile
    @GET("vsms/otp/sendLogin")
    suspend fun generateOTP(@QueryMap(encoded = true) numberparam: HashMap<String, String>): SendOTPResponse

    // verify otp (login via OTP)
    @GET("vsms/otp/sendLogin/{mobile}/{otp}/1")
    suspend fun verifyOTP(
        @Path("mobile") mobile: String,
        @Path("otp") otp: String
    ): ProductCategoriesResponseData

    // create new password
    @POST("rest/V1/changepassword")
    suspend fun createNewPassword(@Body createAccountRequestParams: RequestBody): CreateNewPasswordResponse

    // reset/change password by email
    @POST("rest/V1/forgotpassword")
    suspend fun changePasswordByEmail(@Body createAccountRequestParams: RequestBody): ForgotPasswordResponse

    // verify otp for reset/change password by mobile
    @GET("vsms/otp/verifyforgotpassword")
    suspend fun changePasswordByMobile(@QueryMap(encoded = true) numberparam: HashMap<String, String>): VerifyOTPResponse

    // VERIFY FORGET PASSWORD
    @GET("vsms/otp/verifylogin")
    suspend fun verifyLoginViaOTP(@QueryMap(encoded = true) numberparam: HashMap<String, String>): VerifyOTPResponse

    @GET("rest/{language}/V1/categorylist?")
    suspend fun getCategoryList(
        @Path("language") language: String,
        @Query("store_code") storeCode: Int,
        @Query("type") type: String

    ): CategoryListResponseData

    @GET("rest/{language}/V1/productinfo/{product_id}")
    suspend fun getProductInfo(
        @Path("product_id") productId: Int,
        @Path("language") language: String
    ): ProductDetailResponse

    @GET("rest/{language}/V1/productconfig/{product_sku}")
    suspend fun getConfigurableDetails(
        @Path("product_sku") productSku: String,
        @Path("language") language: String
    ): ConfigurableProductResponse

    @GET("rest/{language}/V1/productlist/{product_id}/{product_type}/{current_page}/{sort_by}/{sort_order}")
    suspend fun getProductList(
        @Path("language") language: String,
        @Path("product_id") productId: Int,
        @Path("product_type") productType: String,
        @Path("current_page") currentPage: Int,
        @Path("sort_by") sortBy: String,
        @Path("sort_order") sortOrder: String): ProductListResponse

    @POST("rest/V1/carts/mine")
    suspend fun getQuoteId(
        @Header("Authorization") token: String,
        @Body quoteIdParams: QuoteIdParams
    ): String

    @POST("rest/V1/carts/mine/items")
    suspend fun addToCartSimple(
        @Header("Authorization") token: String,
        @Body addToCartSimpleParams: AddToCartSimpleParams
    ): AddToCartSimpleResponse

    // GET PRODUCT
    @GET("rest/V1/api/customguesttoken")
    suspend fun getGuestToken(): GuestTokenResponse

    @POST("rest/{language}/V1/guest-carts/{{quote_id}}/items")
    suspend fun addToCartGuestSimple(
        @Path("quote_id") quoteId: Int?,
        @Path("language") language: String,
        @Body addToCartGuestSimpleParams: AddToCartGuestSimpleParams
    ): AddToCartSimpleResponse

    @POST("rest/{language}/V1/carts/mine/items")
    suspend fun addToCartConfigurable(
        @Path("language") language: String,
        @Header("Authorization") token: String,
        @Body addToCartConfigurableParams: AddToCartConfigurableParams
    ): AddToCartConfigurableResponse

    @POST("rest/{language}/V1/guest-carts/{{quote_id}}/items")
    suspend fun addToCartGuestConfigurable(
        @Path("quote_id") quoteId: Int?,
        @Body addToCartGuestConfigurableParams: AddToCartGuestConfigurableParams,
        @Path("language") language: String
    ): AddToCartConfigurableResponse

    // Admin token
    @POST("rest/V1/integration/admin/token")
    suspend fun getAdminToken(@Body adminTokenParams: AdminTokenParams): String

    // Customer Login
    @Headers("Content-Type: application/json")
    @POST("rest/{language}/V1/getcustomertoken")
    suspend fun getCustomerToken(
        @Path("language") language: String,
        @Body customerTokenParams: CustomerTokenParams
    ): String

    // Customer Registration
    @POST("rest/{language}/V1/createaccount")
    suspend fun createAccount(
        @Body createAccountRequestParams: RequestBody,
        @Path("language") language: String
    ): SignUpResponse

    @POST("rest/{language}/V1/customerlogin")
    fun getEcommerceLogin(
        @Path("language") language: String,
        @Body createAccountRequestParams: RequestBody
    ): Call<LoginDataResponse>

    //    get cart item
    @GET("rest/{language}/V1/api/customercart/{customer_id}")
    suspend fun getCartItems(
        @Path("customer_id") customerId: Int,
        @Path("language") language: String
    ): CartItemResponse

    //    get cart item for guest
    @GET("rest/{language}/V1/api/guestcart/{quote_id}")
    suspend fun getCartItemsGuest(
        @Path("quote_id") quoteId: Int,
        @Path("language") language: String
    ): CartItemResponse

    //  update cart item
    @PUT("rest/{language}/V1/carts/mine/items/{item_id}")
    suspend fun updateCartItems(
        @Header("Authorization") token: String,
        @Path("item_id") itemId: String,
        @Body UpdateCartItemParams: UpdateCartItemParams,
        @Path("language") language: String
    ): AddToCartSimpleResponse

    // item delete from cart
    @DELETE("rest/{language}/V1/carts/{quote_id}/items/{item_id}")
    suspend fun deleteItem(
        @Header("Authorization") token: String,
        @Path("quote_id") quoteId: Int,
        @Path("item_id") itemId: String,
        @Path("language") language: String
    ): Boolean

    //  update cart item guest
    @PUT("rest/{language}/V1/guest-carts/{quote_id}/items/{item_id}")
    suspend fun updateCartItemsGuest(
        @Path("quote_id") quoteId: Int,
        @Path("item_id") itemId: String,
        @Body UpdateCartItemGuestParams: UpdateCartItemGuestParams,
        @Path("language") language: String
    ): AddToCartSimpleResponse

    // item delete from cart for guest
    @DELETE("rest/{language}/V1/guest-carts/{mask_key}/items/{item_id}")
    suspend fun deleteItemGuest(
        @Path("mask_key") maskKey: String,
        @Path("item_id") itemId: String,
        @Path("language") language: String
    ): Boolean

    //    get addressList
    @GET("rest/V1/api/addresslist/{customer_id}")
    suspend fun getAddressList(
        @Path("customer_id") customerId: Int
    ): AddressListResponse

    @POST("rest/{language}/V1/carts/mine/estimate-shipping-methods")
    suspend fun getShippingMethods(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Body getShippingMethodsParams: GetShippingMethodsParams,
        @Path("language") language: String
    ): GetShippingMethodsResponseData

    @POST("rest/{language}/V1/carts/mine/shipping-information")
    suspend fun getShippingInformation(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Body getShippingInformationParams: GetShippingInformationParams,
        @Path("language") language: String
    ): GetShippingInformationResponseData

    @PUT("rest/{language}/V1/carts/{quote_id}/coupons/{coupon_code}")
    suspend fun addCouponCode(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Path("quote_id") quoteId: String,
        @Path("coupon_code") couponCode: String,
        @Path("language") language: String
    ): Boolean

    @DELETE("rest/{language}/V1/carts/{quote_id}/coupons")
    suspend fun removeCouponCode(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Path("quote_id") quoteId: String,
        @Path("language") language: String
    ): Boolean

    @POST("rest/{language}/V1/carts/mine/payment-information")
    suspend fun setPaymentMethod(
        @Header("Authorization") token: String,
        @Header("Cookie") cookie: String,
        @Body setPaymentMethodParams: SetPaymentMethodParams,
        @Path("language") language: String
    ): String

    @POST("rest/V1/api/codpayment")
    suspend fun codPayment(
        @Body codParams: CodParams,
        @Header("Cookie") cookie: String
    ): CodResponseData

    @POST("rest/V1/api/sendstcotp")
    suspend fun stcPaymentgetOtp(
        @Body stcPaymentOtpRequestParams: StcPaymentOtpRequestParams,
        @Header("Cookie") cookie: String
    ): StcPaymentOtpResponseData

    @POST("rest/V1/api/getstcresponse")
    suspend fun verifyStcPaymentOtp(
        @Body verifyStcPaymentOtpParams: VerifyStcPaymentOtpParams,
        @Header("Cookie") cookie: String,
    ): VerifyStcPaymentOtpResponseData

    @POST("rest/V1/api/stccartretain")
    suspend fun cartRetain(
        @Body cartRetainParams: CartRetainParams,
    ): CartRetainResponseData

    @POST("rest/V1/api/sendtap")
    suspend fun sendTapPayment(
        @Body sendTapPaymentParams: SendTapPaymentParams
    ): SendTapPaymentResponseData

    @POST("rest/V1/api/responsetap")
    suspend fun verifyTapPayment(
        @Body verifyTapPaymentParams: VerifyTapPaymentParams
    ): VerifyTapPaymentResponseData

    //    add address
    @POST("rest/V1/api/addaddress")
    suspend fun addAddress(
        @Body addAddressParams: AddAddressParams,
    ): AddAddressResponseData

    //    update address
    @POST("rest/{language}/V1/api/editaddress")
    suspend fun updateAddress(
        @Body updateAddressParams: UpdateAddressParams,
        @Path("language") language: String
    ): UpdateAddressResponseData

    // delete address
    @DELETE("rest/{language}/V1/addresses/{address_id}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("address_id") address_id: String,
        @Path("language") language: String
    ): Boolean

    // get wishlist items
    @GET("rest/{language}/V1/api/mywishlist/{customer_id}")
    suspend fun getWishlistItems(
        @Path("customer_id") customerId: Int,
        @Path("language") language: String
    ): WishlistResponseData

    // add to wishlist
    @POST("rest/V1/api/addtowishlist")
    suspend fun addToWishList(
        @Body addAndUntickToWishListRequest: AddAndUntickWishListRequest,
    ): WishListAddRemoveUntickResponseData

    // remove from wishlist
    @POST("rest/V1/api/removetowishlist")
    suspend fun removeFromWishlist(
        @Body removeFromWishlistRequest: RemoveFromWishlistRequest,
    ): WishListAddRemoveUntickResponseData

    // add to wishlist
    @POST("rest/{language}/V1/api/untickwishlist")
    suspend fun unTickWishlist(
        @Body addAndUntickToWishListRequest: AddAndUntickWishListRequest,
        @Path("language") language: String,
    ): WishListAddRemoveUntickResponseData

    // GET CURRENCY
    @GET("rest/V1/directory/currency")
    suspend fun getCurrencyCodes(): GetCurrencyResponseData

    // search api
    @POST("rest/{language}/V1/getsearchresult")
    suspend fun search(
        @Body searchRequestParams: SearchRequestParams,
        @Path("language") language: String
    ): SearchResponse

    //  get order list
    @GET("rest/{language}/V1/api/myorderlist/{customer_id}")
    suspend fun getOrderList(
        @Path("customer_id") customerId: Int,
        @Path("language") language: String
    ): OrderListResponse

    //  cancel order
    @GET("rest/V1/api/ordercancel/{order_id}")
    suspend fun cancelOrder(
        @Path("order_id") orderId: Int
    ): CancelOrderResponse

    //  reorder
    @POST("rest/{language}/V1/api/reorder")
    suspend fun reOrder(
        @Body reOrderRequest: ReOrderRequest,
        @Path("language") language: String
    ): ReOrderResponse

    //  static pages data
    @GET("rest/{language}/V1/api/getstaticpage/{page}")
    suspend fun getStaticPageData(
        @Path("page") page: String,
        @Path("language") language: String
    ): StaticPagesResponseData

    //    get FAQ for ecommerce
    @GET("rest/{language}/V1/api/getfaq")
    suspend fun getFaqEcom(
        @Path("language") language: String
    ): FaqEcomResponse

    //  contact us
    @POST("rest/{language}/V1/api/contactus")
    suspend fun contactUs(
        @Body contactUsRequest: ContactUsRequest,
        @Path("language") language: String
    ): ContactUsResponse

    @POST("rest/{language}/V1/viewall")
    suspend fun viewAllProducts(
        @Body jsonObject: JsonObject,
        @Path("language") language: String,
    ): ViewAllResponse

    //    get profile
    @POST("rest/{language}/V1/api/getprofile")
    suspend fun getProfile(
        @Body getProfileRequest: GetProfileRequest,
        @Path("language") language: String
    ): GetProfileResponse

    //    update profile
    @POST("rest/{language}/V1/api/updateprofile")
    suspend fun updateProfile(
        @Body updateProfileRequest: UpdateProfileRequest,
        @Path("language") language: String
    ): UpdateProfileResponse

    //  change password
    @POST("rest/V1/changepassword")
    suspend fun changePassword(
        @Body changePasswordRequest: ChangePasswordRequest,
    ): ChangePasswordResponse

//    get filter data
    @POST("rest/{language}/V1/getfilterdata")
    suspend fun getFilterData(
        @Body getFilterDataRequest: GetFilterDataRequest,
        @Path("language") language: String
    ): GetFilterDataResponse

//    get filter navigation
    @POST("rest/{language}/V1/filternavigation")
    suspend fun getFilterNavigation(
    @Body getFilterNavigationRequest: GetFilterNavigationRequest,
    @Path("language") language: String,
): GetFilterDataResponse

    //    get filter navigation list
    @POST("rest/{language}/V1/filternavigationlist")
    suspend fun getFilterNavigationList(
        @Body getFilterNavigationListRequest: GetFilterNavigationListRequest,
        @Path("language") language: String,
    ): GetFilterNavigationListResponse

    //   is Number Verified
    @POST("rest/V1/api/isnumberverified")
    suspend fun isNumberVerified(
        @Body numberVerifiedRequest: NumberVerifiedRequest,
    ): NumberVerifiedResponse

    //   send otp
    @GET("vsms/otp/send")
    suspend fun sendOtp(
        @Query("mobile") mobileNumber: String,
        @Query("resend") resend: Int,
        @Query("customer_id") customerId: Int,
        @Query("isWeb") isWeb: Int
    ): SendOtpResponse

    //   verify otp
    @GET("vsms/otp/verify")
    suspend fun verifyOtp(
        @Query("mobile") mobileNumber: String,
        @Query("otp") otp: String
    ): VerifyOtpResponse

    //    set currency to quote
    @POST("rest/{language}/V1/api/setquotecurrency")
    suspend fun setCurrencyToQuote(
        @Body setCurrencyToQuoteRequest: SetCurrencyToQuoteRequest,
        @Path("language") language: String,
        @Header("Cookie") cookie: String
    ): SetCurrencyToQuoteResponse

}

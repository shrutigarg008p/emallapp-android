package com.emall.net.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.emall.net.model.RaiseAnIssueQueryParams
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.absher.AbsherRequestParams
import com.emall.net.network.model.absher.AbsherVerifyOTPRequestParams
import com.emall.net.network.model.addAddressRequest.AddAddressParams
import com.emall.net.network.model.addAndUntickWishlistRequest.AddAndUntickWishListRequest
import com.emall.net.network.model.addSellerAddress.AddressParams
import com.emall.net.network.model.addToCartGuestRequest.configurableProducts.AddToCartGuestConfigurableParams
import com.emall.net.network.model.addToCartGuestRequest.simpleProducts.AddToCartGuestSimpleParams
import com.emall.net.network.model.addToCartRequest.configurableProducts.AddToCartConfigurableParams
import com.emall.net.network.model.addToCartRequest.simpleProducts.AddToCartSimpleParams
import com.emall.net.network.model.adminToken.AdminTokenParams
import com.emall.net.network.model.cartRetainRequest.CartRetainParams
import com.emall.net.network.model.changePasswordRequest.ChangePasswordRequest
import com.emall.net.network.model.chat.SendMessage
import com.emall.net.network.model.codRequest.CodParams
import com.emall.net.network.model.contactUsRequest.ContactUsRequest
import com.emall.net.network.model.customerToken.CustomerTokenParams
import com.emall.net.network.model.ecommerceLogin.LoginRequestParams
import com.emall.net.network.model.evaluationUserAddress.AddressRequestBodyParams
import com.emall.net.network.model.getFilterDataRequest.GetFilterDataRequest
import com.emall.net.network.model.getFilterNavigationListRequest.GetFilterNavigationListRequest
import com.emall.net.network.model.getFilterNavigationRequest.GetFilterNavigationRequest
import com.emall.net.network.model.getProfileRequest.GetProfileRequest
import com.emall.net.network.model.getShippingInformationRequest.GetShippingInformationParams
import com.emall.net.network.model.getShippingMethodsRequest.GetShippingMethodsParams
import com.emall.net.network.model.numberVerifiedRequest.NumberVerifiedRequest
import com.emall.net.network.model.payment.PaymentRequestBody
import com.emall.net.network.model.payment.stc.OTPValue
import com.emall.net.network.model.quoteId.QuoteIdParams
import com.emall.net.network.model.reOrderRequest.ReOrderRequest
import com.emall.net.network.model.removeFromWishlistRequest.RemoveFromWishlistRequest
import com.emall.net.network.model.searchRequest.SearchRequestParams
import com.emall.net.network.model.sendTapPaymentRequest.SendTapPaymentParams
import com.emall.net.network.model.setCurrencyToQuoteRequest.SetCurrencyToQuoteRequest
import com.emall.net.network.model.setPaymentMethodRequest.SetPaymentMethodParams
import com.emall.net.network.model.stcPaymentOtpRequest.StcPaymentOtpRequestParams
import com.emall.net.network.model.updateAddressRequest.UpdateAddressParams
import com.emall.net.network.model.updateBankDetails.BankRequestParams
import com.emall.net.network.model.updateCartItems.UpdateCartItemParams
import com.emall.net.network.model.updateCartItemsGuest.UpdateCartItemGuestParams
import com.emall.net.network.model.updateProfileRequest.UpdateProfileRequest
import com.emall.net.network.model.verifyStcPaymentOtpRequest.VerifyStcPaymentOtpParams
import com.emall.net.network.model.verifyTapPaymentRequest.VerifyTapPaymentParams
import com.emall.net.utils.Resource
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject

class MainViewModel(private val apiService: ApiService) : ViewModel() {

    // Login fragment
    fun getRecommerceLogin(userRequestFields: LoginRequestParams, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getRecommerceLogin(
                            userRequestFields,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }

        }

    // Sell fragment
    fun getProductCategories(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getProductCategories(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // ProductInfo fragment
    fun getBrandByCategory(categoryId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getBrandByCategory(
                            categoryId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // ProductInfo fragment
    fun getModelByBrand(categoryId: Int, brandId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getModelByBrand(
                            categoryId,
                            brandId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // ProductInfo fragment
    fun getVariantByModel(modelId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getVariantByModel(
                            modelId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // SerialNumberFragment
    fun createProduct(categoryId: Int, token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.createProduct(categoryId, token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // SummaryFragment
    fun addProductToCategory(
        language: String,
        categoryId: Int,
        token: String,
        submitType: RequestBody,
        brandId: RequestBody,
        modelId: RequestBody,
        variantId: RequestBody,
        additionalImages: Array<MultipartBody.Part>,
        serialNo: RequestBody,
        serialNumberImage: MultipartBody.Part,
        description: RequestBody,
        shortDescription: RequestBody,
        questions: Map<String, RequestBody>,
        startsAt: RequestBody,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addProductToCategory(
                        language,
                        categoryId,
                        token,
                        submitType,
                        brandId,
                        modelId,
                        variantId,
                        additionalImages,
                        serialNo,
                        serialNumberImage,
                        description,
                        shortDescription,
                        questions,
                        startsAt
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Auction Fragment
    fun getProductsForAuctions(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getProductsForAuctions(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Auction Fragment
    fun getProductsForEvaluation(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getProductsForEvaluation(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    /*  fun getProductDetail(productId: Int, token: String) = liveData(Dispatchers.IO) {
          emit(Resource.loading(data = null))
          try {
              emit(Resource.success(data = apiService.getProductDetail(productId, token)))
          } catch (exception: Exception) {
              emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
          }
      }
  */

    fun getCountries(token: String, email: String, password: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCountries(
                            token,
                            email,
                            password,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getStatesByCountry(
        token: String,
        countryCode: String,
        email: String,
        password: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getStatesByCountry(
                            token,
                            countryCode, email, password, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // AuctionDetailFragment
    fun getAuctionList(productId: Int, token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAuctionList(productId, token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getEvaluationDevices(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationDevices(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    /*// PaymentFragment
    fun createNewAuctionForTAP(productId: Int,token: String, paymentRequestBody: PaymentRequestBody)= liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.createNewAuctionForTAP(productId,token,paymentRequestBody)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }*/

    // PaymentFragment
    fun createNewAuctionForSTC(
        productId: Int,
        token: String,
        paymentRequestBody: PaymentRequestBody,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.createNewAuctionForSTC(
                        productId,
                        token,
                        paymentRequestBody,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    /* // PaymentFragment
     fun createNewEvaluationForTAP(productId: Int,token: String, paymentRequestBody: PaymentRequestBody)= liveData(Dispatchers.IO) {
         emit(Resource.loading(data = null))
         try {
             emit(Resource.success(data = apiService.createNewEvaluationForTAP(productId,token,paymentRequestBody)))
         } catch (exception: Exception) {
             emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
         }
     }*/

    // PaymentFragment
    fun createNewEvaluationForSTC(
        productId: Int,
        token: String,
        paymentRequestBody: PaymentRequestBody,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.createNewEvaluationForSTC(
                        productId,
                        token,
                        paymentRequestBody,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    /*// PaymentFragment
    fun tapVerifyForAuction(productId: Int, auctionId: Int, chargeId: TAPChargeId, token: String)= liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.tapVerifyForAuctions(productId,auctionId,token,chargeId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }
*/
    // PaymentFragment
    fun stcOTPVerifyForAuction(
        productId: Int,
        auctionId: Int,
        otpValue: OTPValue,
        token: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.stcOTPVerifyForAuctions(
                            productId,
                            auctionId,
                            token,
                            otpValue,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    /*  // PaymentFragment
      fun tapVerifyForEvaluation(productId: Int, auctionId: Int, chargeId: TAPChargeId, token: String)= liveData(Dispatchers.IO) {
          emit(Resource.loading(data = null))
          try {
              emit(Resource.success(data = apiService.tapVerifyForEvaluations(productId,auctionId,token,chargeId)))
          } catch (exception: Exception) {
              emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
          }
      }
  */
    // PaymentFragment
    fun stcOTPVerifyForEvaluation(
        productId: Int,
        evaluationId: Int,
        otpValue: OTPValue,
        token: String,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.stcOTPVerifyForEvaluations(
                        productId,
                        evaluationId,
                        token,
                        otpValue,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    /*  // AuctionDetailFragment
      fun tapFailedPaymentForAuction(productId: Int, auctionId: Int, token: String) = liveData(Dispatchers.IO) {
          emit(Resource.loading(data = null))
          try {
              emit(Resource.success(data = apiService.tapFailedPaymentForAuction(productId,auctionId,token)))
          } catch (exception: Exception) {
              emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
          }
      }

      // AuctionDetailFragment
      fun tapFailedPaymentForEvaluation(productId: Int, auctionId: Int, token: String) = liveData(Dispatchers.IO) {
          emit(Resource.loading(data = null))
          try {
              emit(Resource.success(data = apiService.tapFailedPaymentForEvaluation(productId,auctionId,token)))
          } catch (exception: Exception) {
              emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
          }
      }
  */
    // AuctionDetailFragment
    fun stcFailedPaymentForAuction(
        productId: Int,
        auctionId: Int,
        token: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.stcFailedPaymentForAuction(
                            productId,
                            auctionId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // AuctionDetailFragment
    fun stcFailedPaymentForEvaluation(
        productId: Int,
        evaluationId: Int,
        token: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.stcFailedPaymentForEvaluations(
                            productId,
                            evaluationId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getEvaluationAddressList(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationAddressList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun addAddressForEvaluationUser(
        token: String,
        addressRequestBodyParams: AddressRequestBodyParams,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addAddressForEvaluationUser(
                        token,
                        addressRequestBodyParams,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun updateAddressForEvaluationUser(
        addressId: Int,
        token: String,
        addressRequestBodyParams: AddressRequestBodyParams,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.updateAddressForEvaluationUser(
                        addressId,
                        token,
                        addressRequestBodyParams,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun deleteAddressForEvaluationUser(addressId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.deleteAddressForEvaluationUser(
                            addressId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getSellerAddressList(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getSellerAddressList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun addSellerAddress(token: String, addressParams: AddressParams, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.addSellerAddress(
                            token,
                            addressParams,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun updateAddressForSeller(
        addressId: Int,
        token: String,
        addressRequestBodyParams: AddressRequestBodyParams,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.updateAddressForSeller(
                        addressId,
                        token,
                        addressRequestBodyParams,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun deleteAddressForSeller(addressId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.deleteAddressForSeller(
                            addressId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getSellerAddressById(addressId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getSellerAddressById(
                            addressId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getAdminToken(adminTokenParams: AdminTokenParams) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAdminToken(adminTokenParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getCustomerToken(customerTokenParams: CustomerTokenParams, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCustomerToken(
                            language,
                            customerTokenParams
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun sendAbsherOTP(
        token: String,
        absherRequestParams: AbsherRequestParams,
        testingParam: String,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.sendAbsherOTP(
                        testingParam,
                        token,
                        absherRequestParams,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun verifyAbsherOTP(
        token: String,
        absherVerifyOTPRequestParams: AbsherVerifyOTPRequestParams,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.verifyAbsherOTP(
                            token,
                            absherVerifyOTPRequestParams,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getBiddingDetail(productId: Int, auctionId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getBiddingDetails(
                            productId,
                            auctionId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getAuctionDevices(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAuctionDevices(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getAuctionDevicesDetail(auctionId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getAuctionDeviceDetail(
                            token,
                            auctionId,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun addBidToAuction(amount: JsonObject, auctionId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.addBidToAuction(
                            token,
                            auctionId,
                            amount,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getBankList(
/*loginRequestParams: LoginRequestParams*/
        email: String,
        password: String,
        token: String,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.getBankList(
                        token,/*loginRequestParams*/
                        email,
                        password,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun updateBankDetails(bankRequestParams: BankRequestParams, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.updateBankDetails(
                            token,
                            bankRequestParams,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun acceptBid(biddingId: Int, token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.acceptBid(token, biddingId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getAuctionDevicesWon(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.auctionDevicesWon(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun generateShipment(productId: Int, addressId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.generateShipment(
                            productId,
                            addressId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun auctionDevicesWon(auctionWonId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.auctionDevicesWonCheckOut(
                            auctionWonId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun auctionDeviceWonPayment(
        auctionWonId: Int,
        token: String,
        shippingId: RequestBody,
        paymentSlip: MultipartBody.Part,
        amount: RequestBody,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.auctionDeviceWonPayment(
                        auctionWonId,
                        token,
                        shippingId,
                        paymentSlip,
                        amount,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun auctionDeviceWonProceedCheckout(
        auctionWonId: Int,
        token: String,
        shippingId: Int,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.auctionDeviceWonProceedCheckout(
                            auctionWonId,
                            token,
                            shippingId,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getShipmentList(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getShipmentList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getShipmentDetail(shipmentId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getShipmentDetail(
                            shipmentId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getEvaluationList(productId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getEvaluationList(
                            productId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getAcceptedEvaluationDetail(
        productId: Int,
        evaluationId: Int,
        token: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getAcceptedEvaluationDetail(
                            productId,
                            evaluationId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun acceptEvaluation(acceptedEvaluationId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.acceptEvaluation(
                            acceptedEvaluationId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getEvaluationDevicesDetail(evaluationId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getEvaluationDeviceDetail(
                            token,
                            evaluationId,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun addEvaluationAmount(
        evaluationId: Int,
        token: String,
        amount: JsonObject,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.addEvaluationAmount(
                            evaluationId,
                            token,
                            amount,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getEvaluationWonDevices(token: String, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationWonDevices(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun evaluationDeviceWonProceedCheckout(
        evaluationWonId: Int,
        token: String,
        shippingId: Int,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.evaluationDeviceWonProceedCheckout(
                            evaluationWonId,
                            token,
                            shippingId,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun getSellerBankDetailsForEvaluation(evaluationWonId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getSellerBankDetailsForEvaluation(
                            evaluationWonId,
                            token,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    fun evaluationDeviceWonPayment(
        evaluationWonId: Int,
        token: String,
        shippingId: RequestBody,
        paymentSlip: MultipartBody.Part,
        amount: RequestBody,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.offlinePaymentForEvaluation(
                        evaluationWonId,
                        token,
                        shippingId,
                        paymentSlip,
                        amount,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getDashboardDetails(storeCode: Int, deviceType: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getDashboardDetails(storeCode, deviceType)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getDashBoardDetailsForUsed(storeCode: Int, deviceType: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getDashboardDetailsForUsed(storeCode, deviceType)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Login fragment
    fun getCountryDetail( language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getCountryDetail(language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Login fragment
    fun createAccount(requestbody: RequestBody, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.createAccount(
                            requestbody,
                            language
                        )
                    )
                )

            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // Login fragment
    fun verifyOTP(phonenumber: String, otp: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.verifyOTP(phonenumber, otp)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // reset password activity
    fun generateOTP(numberparam: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.generateOTP(numberparam)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // verify OTP for changed password by mobile
    fun verifyPasswordByMobileOTP(numberparam: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.changePasswordByMobile(numberparam)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Login fragment
    fun verifyLoginViaOTP(numberparam: HashMap<String, String>) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.verifyLoginViaOTP(numberparam)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // Login fragment
    fun createNewPassword(requestBody: RequestBody) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.createNewPassword(requestBody)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // reset password activity
    fun changePasswordByEmail(requestBody: RequestBody) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.changePasswordByEmail(requestBody)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getEcommerceLogin(requestBody: RequestBody, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEcommerceLogin(language, requestBody)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    category listing
    fun getCategoryList(store_code: Int, type: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCategoryList(
                            language,store_code,
                            type
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get Product info
    fun productInfo(productId: Int, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getProductInfo(
                            productId, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get  Configurable Product Data
    fun configurableProductDetails(productSku: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getConfigurableDetails(
                            productSku, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    accept/reject evaluation
    fun acceptRejectAnEvaluation(productId: Int, action: String, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.acceptRejectAnEvaluation(
                            token, productId, action, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    re-evaluation request
    fun reEvaluateRequest(
        reEvaluationRequestId: Int,
        token: String,
        reEvaluationParams: HashMap<String, String>,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.reEvaluateRequest(
                            reEvaluationRequestId, token, reEvaluationParams, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    return device
    fun returnDeviceShipment(reEvaluationRequestId: Int, token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.returnDeviceShipment(
                            reEvaluationRequestId, token, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get  Product ListData
    fun getProductListData(language: String,productId: Int,productType: String,currentPage: Int,sortBy: String, sortOrder: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getProductList(language,productId,productType,currentPage,sortBy,sortOrder)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    getQuote Id for Logged In user
    fun getQuoteId(token: String, quoteIdParams: QuoteIdParams): LiveData<Resource<String>> =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.getQuoteId(token, quoteIdParams)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    add simple product to cart for Logged In User
    fun addToCartSimple(token: String, AddToCartSimpleParams: AddToCartSimpleParams) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.addToCartSimple(
                            token,
                            AddToCartSimpleParams
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get Guest Token
    fun getGuestToken() =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getGuestToken()
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    add simple product to cart for Guest User
    fun addToCartGuestSimple(
        language: String,
        quoteId: Int?,
        AddToCartGuestSimpleParams: AddToCartGuestSimpleParams,
    ): LiveData<Resource<Any>> = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addToCartGuestSimple(
                        quoteId,
                        language,
                        AddToCartGuestSimpleParams
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //        add configurable product to cart for Logged In User
    fun addToCartConfigurable(
        language: String,
        token: String,
        AddToCartConfigurableParams: AddToCartConfigurableParams,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addToCartConfigurable(
                        language,
                        token,
                        AddToCartConfigurableParams
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    add configurable product to cart for Guest User
    fun addToCartGuestConfigurable(
        quoteId: Int?,
        AddToCartGuestConfigurableParams: AddToCartGuestConfigurableParams,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addToCartGuestConfigurable(
                        quoteId,
                        AddToCartGuestConfigurableParams,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    get cart items
    fun getCartItems(customerId: Int, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getCartItems(customerId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    get cart items for guest
    fun getCartItemsGuest(quoteId: Int, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCartItemsGuest(
                            quoteId, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }


    //    update cart items
    fun updateCartItems(
        token: String,
        itemId: String,
        UpdateCartItemParams: UpdateCartItemParams,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.updateCartItems(
                        token,
                        itemId,
                        UpdateCartItemParams, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    //    delete item from cart
    fun deleteItem(token: String, quoteId: Int, itemId: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.deleteItem(
                            token,
                            quoteId,
                            itemId,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    update cart items for guest
    fun updateCartItemsGuest(
        quoteId: Int,
        itemId: String,
        UpdateCartItemGuestParams: UpdateCartItemGuestParams,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.updateCartItemsGuest(
                        quoteId,
                        itemId,
                        UpdateCartItemGuestParams, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    delete item from cart for guest
    fun deleteItemGuest(maskKey: String, itemId: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.deleteItemGuest(maskKey, itemId, language)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //raise an issue
    fun raiseAnIssue(
        token: String,
        raiseAnIssueQueryParams: RaiseAnIssueQueryParams,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.raiseAnIssue(
                            token,
                            raiseAnIssueQueryParams,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // get issues list
    fun getIssueList(token: String, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.getIssues(token, language)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // get issue detail
    fun getIssueDetail(token: String, issueId: Int, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.getIssueDetail(token, issueId, language)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // close an issue
    fun closeAnIssue(token: String, issueId: Int, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.closeIssue(token, issueId, language)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // change auction/evaluation status
    fun changeItemStatus(
        token: String,
        productId: Int,
        itemType: String,
        itemId: Int,
        status: Int,
        reason: String,
        language: String
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.changeItemStatus(
                            token,
                            productId,
                            itemType,
                            itemId,
                            status,
                            reason,
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get address List
    fun getAddressList(customerId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAddressList(customerId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }


    //    get Shipping methods
    fun getShippingMethods(
        token: String,
        cookie: String,
        GetShippingMethodsParams: GetShippingMethodsParams,
        language: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getShippingMethods(
                            token, cookie,
                            GetShippingMethodsParams, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get Shipping information
    fun getShippingInformation(
        token: String,
        cookie: String,
        GetShippingInformationParams: GetShippingInformationParams,
        language: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getShippingInformation(
                            token, cookie,
                            GetShippingInformationParams, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    add coupon code
    fun addCouponCode(
        token: String,
        cookie: String,
        quoteId: String,
        couponCode: String,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.addCouponCode(
                        token,
                        cookie,
                        quoteId,
                        couponCode, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //    remove coupon code
    fun removeCouponCode(
        token: String,
        cookie: String,
        quoteId: String,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.removeCouponCode(
                        token, cookie,
                        quoteId, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  set Payment methods
    fun setPaymentMethod(
        token: String,
        cookie: String,
        SetPaymentMethodParams: SetPaymentMethodParams,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.setPaymentMethod(
                        token, cookie,
                        SetPaymentMethodParams, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  cod payment
    fun codPayment(
        CodParams: CodParams,cookies: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.codPayment(CodParams,cookies)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  stc payment get otp
    fun stcPaymentgetOtp(
        StcPaymentOtpRequestParams: StcPaymentOtpRequestParams,
        cookie: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.stcPaymentgetOtp(StcPaymentOtpRequestParams,
                cookie)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  stc payment verify otp
    fun verifyStcPaymentOtp(
        VerifyStcPaymentOtpParams: VerifyStcPaymentOtpParams,
        cookie: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.verifyStcPaymentOtp(VerifyStcPaymentOtpParams,
                cookie)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  stc payment verify otp
    fun cartRetain(
        CartRetainParams: CartRetainParams,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.cartRetain(CartRetainParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  send tap payment
    fun sendTapPayment(
        SendTapPaymentParams: SendTapPaymentParams,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.sendTapPayment(SendTapPaymentParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  verify tap payment
    fun verifyTapPayment(
        VerifyTapPaymentParams: VerifyTapPaymentParams,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.verifyTapPayment(VerifyTapPaymentParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  get channel list
    fun getChannelList(
        token: String,
        language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getChannelList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  get channel messages
    fun getChannelMessages(
        token: String, channelId: Int, language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getChannelMessages(token, channelId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  create channel
    fun createChannel(
        token: String, rawString: JSONObject, language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.createChannel(token, rawString, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  send message
    fun sendMessage(
        token: String, channelId: Int, message: SendMessage, language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.sendChatMessage(
                        token,
                        channelId,
                        message,
                        language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    fun getPaymentList(
        token: String, page: Int, language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getPaymentsList(token, page, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // show serial number Page
    fun showSerialNumberPage(
        token: String,language: String,brandId: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.showSerialNumberPage(token, language,brandId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // track shipments list
    fun getShipmentTrackingList(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getShipmentTrackingList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // track shipment detail
    fun trackShipmentDetails(
        token: String,language: String,shipmentId: Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getShipmentTrackingDetail(token, language,shipmentId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get FAQ
    fun getFAQ(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getFAQ(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get Auction WishList
    fun getAuctionWishList(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAuctionWishList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // add auction to wishList
    fun toggleAuctionWishList(
        token: String,language: String,productId: JsonObject
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.toggleAuctionWishList(token, language,productId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get auction orders
    fun getAuctionOrders(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getAuctionOrders(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get evaluation orders
    fun getEvaluationOrders(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationOrders(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get evaluation reports
    fun getEvaluationReports(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationReports(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get evaluation wishList
    fun getEvaluationWishList(
        token: String,language: String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getEvaluationWishList(token, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // add evaluation to wishList
    fun toggleEvaluationToWishList(
        token: String,language: String, productId: JsonObject
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.toggleEvaluationToWishList(token, language,productId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  add address
    fun addAddress(
        AddAddressParams: AddAddressParams,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.addAddress(AddAddressParams)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  update address
    fun updateAddress(
        UpdateAddressParams: UpdateAddressParams,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.updateAddress(UpdateAddressParams, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  delete address
    fun deleteAddress(
        token: String,
        addressId: String,
        language: String,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.deleteAddress(token, addressId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // get wishlist items
    fun getWishlistItems(customerId: Int, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getWishlistItems(customerId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    // add to wishlist
    fun addToWishList(AddAndUntickWishListRequest: AddAndUntickWishListRequest) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(Resource.success(data = apiService.addToWishList(AddAndUntickWishListRequest)))
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // remove from wishlist
    fun removeFromWishlist(RemoveFromWishlistRequest: RemoveFromWishlistRequest) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.removeFromWishlist(
                            RemoveFromWishlistRequest
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // untick wishlist
    fun unTickWishlist(AddAndUntickWishListRequest: AddAndUntickWishListRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.unTickWishlist(
                            AddAndUntickWishListRequest, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get Currency
    fun getCurrencyCodes() =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getCurrencyCodes()
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    // search api
    fun search(SearchRequestParams: SearchRequestParams, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.search(
                            SearchRequestParams, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    get order list
    fun getOrderList(customerId: Int, language: String) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.getOrderList(customerId, language)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  cancel order
    fun cancelOrder(orderId: Int) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiService.cancelOrder(orderId)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

 //  reOrder
    fun reOrder(ReOrderRequest: ReOrderRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.reOrder(
                            ReOrderRequest, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

//  static page data
fun getStaticPageData(page: String, language: String) =
    liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(
                Resource.success(
                    data = apiService.getStaticPageData(
                        page, language
                    )
                )
            )
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
        }
    }

    //  get FAQ for ecommerce
    fun getFaqEcom(language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getFaqEcom(
                            language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  reOrder
    fun contactUs(ContactUsRequest: ContactUsRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.contactUs(
                            ContactUsRequest, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  reOrder
    fun viewAllProducts(jsonObject: JsonObject, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.viewAllProducts(
                            jsonObject, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  getProfile
    fun getProfile(getProfileRequest: GetProfileRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getProfile(
                            getProfileRequest, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  update profile
    fun updateProfile(updateProfileRequest: UpdateProfileRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.updateProfile(
                            updateProfileRequest, language
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  change password
    fun changePassword(changePasswordRequest: ChangePasswordRequest) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.changePassword(changePasswordRequest)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  get filter data
    fun getFilterData(getFilterDataRequest: GetFilterDataRequest, language: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getFilterData(getFilterDataRequest, language)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  get filter navigation
    fun getFilterNavigation(
        getFilterNavigationRequest: GetFilterNavigationRequest,
        language: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getFilterNavigation(getFilterNavigationRequest, language)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  get filter navigation list
    fun getFilterNavigationList(
        getFilterNavigationListRequest: GetFilterNavigationListRequest,
        language: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.getFilterNavigationList(getFilterNavigationListRequest,
                            language)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  is Number verified
    fun isNumberVerified(
        numberVerifiedRequest: NumberVerifiedRequest,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.isNumberVerified(numberVerifiedRequest)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    send OTP
    fun sendOtp(mobile: String, resend: Int, customerId: Int, isWeb: Int) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.sendOtp(
                            mobile, resend, customerId, isWeb
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //    verify OTP
    fun verifyOtp(mobile: String, otp: String) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.verifyOtp(
                            mobile, otp
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }

    //  set currency to quote
    fun setCurrencyToQuote(
        setCurrencyToQuoteRequest: SetCurrencyToQuoteRequest,
        language: String, cookies: String,
    ) =
        liveData(Dispatchers.IO) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = apiService.setCurrencyToQuote(setCurrencyToQuoteRequest,
                            language,
                            cookies)
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
        }
}
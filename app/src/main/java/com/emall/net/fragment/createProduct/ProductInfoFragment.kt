package com.emall.net.fragment.createProduct

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.emall.net.R
import com.emall.net.activity.dashboard.SellerActivity
import com.emall.net.network.api.*
import com.emall.net.network.model.*
import com.emall.net.network.model.filter.VariantsData
import com.emall.net.utils.*
import com.emall.net.utils.Constants.PRODUCT
import com.emall.net.utils.Constants.SELLER_EVALUATOR_TOKEN
import com.emall.net.utils.Utility.makeGone
import com.emall.net.utils.Utility.makeVisible
import com.emall.net.utils.Utility.replaceFragment
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.*
import kotlinx.android.synthetic.main.fragment_product_info.*


class ProductInfoFragment : Fragment(), View.OnClickListener {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""

    private var brandList = ArrayList<ProductInfo>()
    private var modelList = ArrayList<ProductInfo>()
    private var variantList = ArrayList<VariantsData>()
    private var variants = ArrayList<String>()
    private var models = ArrayList<String>()
    private var brands = ArrayList<String>()

    private var addProduct: AddProduct? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as SellerActivity).showHideToolbar("")
        (activity as SellerActivity).setToolbarTitle(getString(R.string.sell_your_mobile))
        token = PreferenceHelper.readString(SELLER_EVALUATOR_TOKEN)
        addProduct = arguments?.getParcelable(PRODUCT)
        next_btn.setOnClickListener(this::onClick)

        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(ApiClient().apiClient().create(ApiService::class.java))
        )
            .get(MainViewModel::class.java)
        viewModel.getBrandByCategory(addProduct?.categoryId!!, token!!,Utility.getLanguage()).observe(
            viewLifecycleOwner,
            Observer {
                it?.let { resource ->
                    addProduct?.brand= ""
                    addProduct?.model= ""
                    addProduct?.variant= ""
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA.let {
                                progress_bar.makeGone()
                                brands.clear()
                                models.clear()
                                variants.clear()

                                brands.add(getString(R.string.select_brand))
                                models.add(getString(R.string.select_model))
                                variants.add(getString(R.string.select_variant))

                               model_spinner.setItems(models)
                                variant_spinner.setItems(variants)

                                for (productInfo in resource.data?.DATA!!) {
                                    brands.add(productInfo.title)
                                }
                                brand_spinner.setItems(brands)
                                brandList = resource.data.DATA
                            }
                        }
                        Status.ERROR -> progress_bar.makeGone()
                        Status.LOADING -> progress_bar.makeVisible()
                    }
                }
            })

        brand_spinner.setOnItemSelectedListener { view, position, id, item ->
            if(position!=0) {
                addProduct?.brandId = brandList[position - 1].id
                addProduct?.brand = brandList[position - 1].title
                viewModel.getModelByBrand(
                    addProduct?.categoryId!!,
                    addProduct?.brandId!!,
                    token!!,
                    Utility.getLanguage()
                )
                    .observe(viewLifecycleOwner, Observer {
                        it?.let { resource ->
                            when (resource.status) {
                                Status.SUCCESS -> {
                                    resource.data?.DATA.let { it ->
                                        models.clear()
                                        variants.clear()
                                        variants.add(getString(R.string.select_variant))
                                        models.add(getString(R.string.select_model))
                                        for (modelInfo in it!!) {
                                            models.add(modelInfo.title)

                                        }
                                        variant_spinner.setItems(variants)
                                        model_spinner.setItems(models)
                                        modelList = it
                                    }
                                }
                                Status.ERROR -> {}
                                Status.LOADING -> {}
                            }
                        }
                    })
            }
            else{ addProduct?.brand =""}
        }

        model_spinner.setOnItemSelectedListener { view, position, id, item ->
            Log.e("position"," $position")
            if(position!=0) {
                addProduct?.modelId = modelList[position - 1].id
                addProduct?.model = modelList[position - 1].title
                viewModel.getVariantByModel(addProduct?.modelId!!, token!!, Utility.getLanguage())
                    .observe(
                        viewLifecycleOwner,
                        Observer {
                            it?.let { resource ->
                                when (resource.status) {
                                    Status.SUCCESS -> {
                                        resource.data?.DATA.let { it ->
                                            if (it!!.isNotEmpty()) {
                                                variants.clear()
                                                variants.add(getString(R.string.select_variant))
                                                for (variantInfo in it!!) {
                                                    variants.add(variantInfo.title)
                                                }
                                                variant_spinner.setItems(variants)
                                                variantList = it
                                            } else {
                                                product_info_constraint_layout.snack("Empty Variants")
                                            }
                                        }
                                    }
                                    Status.ERROR -> {}
                                    Status.LOADING -> {}
                                }
                            }
                        })
            }
            else{ addProduct?.model =""}
        }
        variant_spinner.setOnItemSelectedListener { _, position, id, item ->
            if(position!=0) {
                addProduct?.variant = variantList[position - 1].title
                addProduct?.variantId = variantList[position - 1].id
            }
            else{ addProduct?.variant =""}
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.next_btn) {
            Log.e("addProduct?.model",addProduct?.model.toString())

            if (!(addProduct?.model.isNullOrEmpty()) && !(addProduct?.brand.isNullOrEmpty()) && !(addProduct?.variant.isNullOrEmpty())) {
                val bundle = Bundle()
                bundle.putParcelable(PRODUCT, addProduct)
                val fragment = SerialNumberFragment()
                fragment.arguments = bundle
                (activity as SellerActivity).replaceFragment(fragment,R.id.container)
            }
            else{
                Log.e("You need to select all fields to proceed","You need to select all fields to proceed")
                Toast.makeText(requireContext(),"You need to select all fields to proceed",Toast.LENGTH_SHORT).show()
        }

        }
    }
}
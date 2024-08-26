package com.emall.net.fragment.product

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.adapter.FilterListDataAdapter
import com.emall.net.adapter.FilterListTitleAdapter
import com.emall.net.listeners.OnItemClick
import com.emall.net.network.api.ApiClient
import com.emall.net.network.api.ApiService
import com.emall.net.network.model.getFilterDataRequest.GetFilterDataRequest
import com.emall.net.network.model.getFilterDataRequest.GetFilterDataRequestParam
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponseList
import com.emall.net.network.model.getFilterDataResponse.GetFilterDataResponseListData
import com.emall.net.network.model.getFilterNavigationRequest.GetFilterNavigationRequest
import com.emall.net.network.model.getFilterNavigationRequest.GetFilterNavigationRequestParam
import com.emall.net.network.model.getFilterNavigationResponse.GetFilterNavigationResponseData
import com.emall.net.utils.Status
import com.emall.net.utils.Utility
import com.emall.net.utils.ViewUtils
import com.emall.net.viewmodel.MainViewModel
import com.emall.net.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.fragment_filter.*


class FilterFragment : DialogFragment(), OnItemClick {
    private lateinit var viewModel: MainViewModel
    private lateinit var filterListTitleAdapter: FilterListTitleAdapter
    private lateinit var filterListDataAdapter: FilterListDataAdapter
    private var filterData = ArrayList<GetFilterDataResponseList>()
    private var filterListData = ArrayList<GetFilterDataResponseListData>()
    private var storageList = ArrayList<String>()
    private var ramList = ArrayList<String>()
    private var colorList = ArrayList<String>()
    private var sizeList = ArrayList<String>()
    private var currentPositionTitle: Int = 0
    private var categoryId: Int = 0
    private var filterNavigationData = ArrayList<GetFilterNavigationResponseData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgumentData()
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiClient().storeUrlApiClient()
                    .create(ApiService::class.java)
            ))
            .get(MainViewModel::class.java)
        setUp()
    }

    private fun getArgumentData() {
        if (arguments != null) {
            categoryId = arguments?.getInt("categoryId")!!
            storageList = arguments?.getStringArrayList("storageList")!!
            ramList = arguments?.getStringArrayList("ramList")!!
            colorList = arguments?.getStringArrayList("colorList")!!
            sizeList = arguments?.getStringArrayList("sizeList")!!
            filterNavigationData =
                arguments?.getSerializable("filterNavigationData") as ArrayList<GetFilterNavigationResponseData>
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)

        }
    }

    private fun setUp() {
        filter_list_title_recyclerview.setHasFixedSize(true)
        filter_list_title_recyclerview.layoutManager = LinearLayoutManager(activity)

        filter_list_data_recyclerview.setHasFixedSize(true)
        filter_list_data_recyclerview.layoutManager = LinearLayoutManager(activity)

        apply_button.setOnClickListener {

            val bundle = Bundle()

            val intent = Intent()
            intent.putExtra("AllFilterData",filterData);

            bundle.putStringArrayList("Storage",storageList)

            bundle.putStringArrayList("size",sizeList)
            bundle.putStringArrayList("Colour",colorList)
            bundle.putStringArrayList("RamSize",ramList)


            intent.putExtra("BUNDLE", bundle)

            targetFragment!!.onActivityResult(100, 99, intent)
            dialog!!.cancel()
        }

        cancel_button.setOnClickListener {
            storageList.clear()
            ramList.clear()
            colorList.clear()
            sizeList.clear()
            filterNavigationData.clear()
            getFilterData()
        }
        getFilterData()
    }

    private fun getFilterData() {

        val getFilterDataRequest = GetFilterDataRequest(
            param = GetFilterDataRequestParam(
                categoryId.toString(), Utility.getDeviceType()
            )
        )
        viewModel.getFilterData(getFilterDataRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                filterData.clear()
                                filterData.addAll(it.data)
                                filterListTitleAdapter = FilterListTitleAdapter(filterData,
                                    storageList,
                                    ramList,
                                    colorList,
                                    sizeList,
                                    this)
                                filter_list_title_recyclerview.adapter = filterListTitleAdapter
                                filterListTitleAdapter.notifyDataSetChanged()
                                setFilterListData(0)
                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    override fun onItemClick(position: Int, type: String) {
        if (type.equals("filterListTitle", true)) {

            currentPositionTitle = position
            filterListTitleAdapter.notifyDataSetChanged()

            setFilterListData(position)
        } else {
            selectItems(position)
            //getFilterNavigation(position)
        }
    }

    private fun getFilterNavigation(position: Int) {
        /*if (filterData.isNotEmpty()) {
            when {
                filterData[currentPositionTitle].code.equals("storage", true) -> {
                    storageList.add(filterData[currentPositionTitle].data.get(position).value)
                }
                filterData[currentPositionTitle].code.equals("ram", true) -> {
                    ramList.add( filterData[currentPositionTitle].data.get(position).value)
                }
                filterData[currentPositionTitle].code.equals("color", true) -> {
                         colorList.add(filterData[currentPositionTitle].data.get(position).value)
                }
                else -> {
                    sizeList.add(filterData[currentPositionTitle].data.get(position).value)
                }
            }
        }*/

        val storageValues = storageList.joinToString { it -> it }
        val sizeValues = sizeList.joinToString { it -> it }
        val colorValues = colorList.joinToString { it -> it }
        val ramValues = ramList.joinToString { it -> it }


        Log.e("Storage Value-->>",storageValues)
        Log.e("sizeValues-->>",sizeValues)
        Log.e("colorValues-->>",colorValues)
        Log.e("ramValues-->>",ramValues)

        val getFilterNavigationRequest = GetFilterNavigationRequest(
            param = GetFilterNavigationRequestParam(
                categoryId.toString(),
                "",
                Utility.getDeviceType(),
                storageValues,
                sizeValues,
                colorValues,
                ramValues
            )
        )
        viewModel.getFilterNavigation(getFilterNavigationRequest, Utility.getLanguage())
            .observe(viewLifecycleOwner, Observer {
                it?.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            resource.data?.let {
                                //filterNavigationData.clear()
                                //filterNavigationData.addAll(it.data)

                                filterData.clear()
                                filterData.addAll(it.data)
                                setFilterListData(currentPositionTitle)
                               /* filterListDataAdapter.notifyDataSetChanged()
                                filterListTitleAdapter.notifyItemChanged(currentPositionTitle)
                                filterListData.clear()
                                filterListData.addAll(filterData[position].data)
                                filterListDataAdapter =
                                    FilterListDataAdapter(filterListData, storageList, ramList, colorList, sizeList, this)
                                filter_list_data_recyclerview.adapter = filterListDataAdapter*/

                            }
                        }
                        Status.ERROR -> {
                            activity?.let { ViewUtils.hideProgressBar() }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                        Status.LOADING -> {
                            activity?.let { ViewUtils.showProgressBar(it) }
                            Log.d("TAG", "onClick: ${it.message} , ${it.status}")
                        }
                    }
                }
            })
    }

    private fun selectItems(position: Int) {

        if (filterData.isNotEmpty()) {
            when {
                filterData[currentPositionTitle].code.equals("storage", true) -> {
                    addAndRemoveItem(storageList, position)
                }
                filterData[currentPositionTitle].code.equals("ram", true) -> {
                    addAndRemoveItem(ramList, position)
                }
                filterData[currentPositionTitle].code.equals("color", true) -> {
                    addAndRemoveItem(colorList, position)
                }
                else -> {
                    addAndRemoveItem(sizeList, position)
                }
            }
        }
        getFilterNavigation(position)

        filterListDataAdapter.notifyDataSetChanged()
        filterListTitleAdapter.notifyItemChanged(currentPositionTitle)

        val storageValues = storageList.joinToString { it -> it }
        val sizeValues = sizeList.joinToString { it -> it }
        val colorValues = colorList.joinToString { it -> it }
        val ramValues = ramList.joinToString { it -> it }


        Log.e("Storage Value2-->>",storageValues)
        Log.e("sizeValues2-->>",sizeValues)
        Log.e("colorValues2-->>",colorValues)
        Log.e("ramValues2-->>",ramValues)
    }

    private fun addAndRemoveItem(list: ArrayList<String>, position: Int) {
        if (list.contains(filterListData[position].value)) {
            list.remove(filterListData[position].value)
        } else {
            list.add(filterListData[position].value)
        }

    }

    private fun setFilterListData(position: Int) {
        filterListData.clear()
        filterListData.addAll(filterData[position].data)
        filterListDataAdapter =
            FilterListDataAdapter(filterListData, storageList, ramList, colorList, sizeList, this)
        filter_list_data_recyclerview.adapter = filterListDataAdapter
        filterListDataAdapter.notifyDataSetChanged()
    }
}

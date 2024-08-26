package com.emall.net.fragment.evaluationReports

import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.emall.net.R
import com.emall.net.utils.BaseFragment
import com.emall.net.activity.dashboard.BuyerActivity
import com.emall.net.adapter.EvaluationReportAdapter
import com.emall.net.model.ReportData
import com.emall.net.utils.*
import com.emall.net.utils.Utility.snack
import com.emall.net.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.fragment_evaluation_reports.*
import kotlinx.coroutines.launch

class EvaluationReportsFragment : BaseFragment() {

    private lateinit var viewModel: MainViewModel
    private var token: String? = ""
    private lateinit var evaluationReportAdapter: EvaluationReportAdapter
    private var reportList = ArrayList<ReportData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_evaluation_reports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as BuyerActivity).showHideToolbar("")
        (activity as BuyerActivity).setToolbarTitle(getString(R.string.my_payments))

        scopeMainThread.launch {
            viewModel = getReCommerceViewModel()
            token = getReCommerceToken()
            evaluation_report_recycler_view.setHasFixedSize(true)
            evaluation_report_recycler_view.layoutManager = LinearLayoutManager(activity)
            evaluationReportAdapter = EvaluationReportAdapter(reportList)
            evaluation_report_recycler_view.adapter = evaluationReportAdapter
            getEvaluationReports()
        }
    }


    private fun getEvaluationReports() {
        viewModel.getEvaluationReports("Bearer $token", Utility.getLanguage())
            .observe(viewLifecycleOwner, {
                it.let { resource ->
                    when (resource.status) {
                        Status.SUCCESS -> {
                            resource.data?.DATA?.data?.let { it ->
                                    reportList.clear()
                                    reportList.add(ReportData(0, "Device", "Date", "Status"))
                                    it.forEach { data ->
                                        reportList.add(
                                            ReportData(
                                                data.id,
                                                data.device_name,
                                                data.date,
                                                data.report_status
                                            )
                                        )
                                    }
                                    evaluationReportAdapter.notifyDataSetChanged()
                                }
                        }
                        Status.ERROR -> {
                                scrollView.snack(it.data?.MESSAGE!!)
                        }
                        Status.LOADING -> {

                        }
                    }
                }
            })

//        }
    }
}
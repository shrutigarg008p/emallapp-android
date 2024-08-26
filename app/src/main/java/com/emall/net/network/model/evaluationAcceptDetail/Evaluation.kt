package com.emall.net.network.model.evaluationAcceptDetail

data class Evaluation(
    val action: Action,
    val amount: String,
    val id: Int,
    val is_selected: Boolean,
    val re_evaluation_status: Any,
    val current_status: String,
    val selected_at: String,
    val selected_at_str: String,
    val selected_by: SelectedBy
)
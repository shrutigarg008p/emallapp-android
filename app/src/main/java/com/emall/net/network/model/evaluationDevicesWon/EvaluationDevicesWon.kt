package com.emall.net.network.model.evaluationDevicesWon

data class EvaluationDevicesWon(
    val action_button: ActionButton,
    val chat_with_seller: ChatWithSeller,
    val ends_at: String,
    val ends_at_str: String,
    val extra_notes: ExtraNotes,
    val id: Int,
    val product: Product,
    val raise_issue: RaiseIssue,
    val re_evaluate: ReEvaluate,
    val shipping_id: Int,
    val starts_at: String,
    val status: String,
    val steps: Int,
    val updated_at: String
)
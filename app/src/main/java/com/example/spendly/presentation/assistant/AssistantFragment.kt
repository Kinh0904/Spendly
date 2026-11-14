package com.example.spendly.presentation.assistant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.spendly.MainActivity
import com.example.spendly.databinding.FragmentAssistantBinding
import com.example.spendly.presentation.transaction.add.BudgetFragment
import com.example.spendly.utils.NavigationHelper

class AssistantFragment : Fragment() {
    private var _binding: FragmentAssistantBinding? = null
    private val binding get() = _binding!!
    private var draftVisible = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentAssistantBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.askMost.setOnClickListener { addMessage("Tháng này tôi chi nhiều nhất ở đâu?", "Ăn uống chiếm nhiều nhất: 38% (1.450.000 đ). Tiếp theo là Mua sắm 24% và Hóa đơn 18%.") }
        binding.askBudget.setOnClickListener { addMessage("Còn bao nhiêu ngân sách?", "Bạn còn 1.150.000 đ trong ngân sách tháng này.") }
        binding.askTips.setOnClickListener { addMessage("Gợi ý ý tiết kiệm", "Thử đặt giới hạn mua sắm 700.000 đ và xem lại các khoản nhỏ trong tuần nhé.") }
        binding.askExpense.setOnClickListener { draftVisible = true; addMessage("Trưa nay ăn bún bò 45k", "Mình ghi giúp bạn khoản này nhé."); showDraft() }
        binding.sendMessage.setOnClickListener { val msg = binding.messageInput.text.toString().trim(); if(msg.isNotEmpty()) { binding.messageInput.text?.clear(); if (msg.contains("45") || msg.contains("chi")) { addMessage(msg, "Mình đã tạo bản nháp chi tiêu để bạn kiểm tra."); draftVisible = true; showDraft() } else addMessage(msg, "Mình chưa kết nối dữ liệu trực tuyến. Bạn có thể thử câu hỏi mẫu về chi tiêu.") } }
        binding.saveDraft.setOnClickListener { binding.draftCard.visibility = View.GONE; addBubble("Đã lưu khoản chi 45.000 đ (bản demo)", false); draftVisible = false }
        binding.editDraft.setOnClickListener { androidx.appcompat.app.AlertDialog.Builder(requireContext()).setTitle("Sửa khoản chi").setMessage("Bản nháp: 45.000 đ · Ăn uống").setPositiveButton("Đã hiểu", null).show() }
        binding.retryOffline.setOnClickListener { addMessage("Thử lại", "Chưa có kết nối dịch vụ Trợ lý. Bạn vẫn có thể dùng các thao tác ngoại tuyến.") }
        NavigationHelper.setupBottomToolbar(binding.root, NavigationHelper.Destination.HOME)
    }
    private fun addMessage(user: String, answer: String) { addBubble(user, true); addBubble(answer, false) }
    private fun addBubble(text: String, mine: Boolean) { val tv = TextView(requireContext()).apply { this.text = text; textSize = 14f; setTextColor(if(mine) 0xFFFFFFFF.toInt() else 0xFF272536.toInt()); setPadding(dp(14), dp(11), dp(14), dp(11)); setBackgroundColor(if(mine) 0xFF8058F5.toInt() else 0xFFFFFFFF.toInt()) }; val p = android.widget.LinearLayout.LayoutParams(-2, -2).apply { topMargin = dp(7); if(mine) marginStart = dp(52) else marginEnd = dp(52); gravity = if(mine) android.view.Gravity.END else android.view.Gravity.START }; binding.chatMessages.addView(tv, p); binding.chatScroll.post { binding.chatScroll.fullScroll(View.FOCUS_DOWN) } }
    private fun showDraft() { binding.draftCard.visibility = View.VISIBLE }
    private fun dp(v:Int) = (v * resources.displayMetrics.density).toInt()
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

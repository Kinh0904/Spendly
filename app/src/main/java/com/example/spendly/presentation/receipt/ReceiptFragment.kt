package com.example.spendly.presentation.receipt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spendly.databinding.FragmentReceiptBinding

class ReceiptFragment : Fragment() {
    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private var flashOn = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentReceiptBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.closeScanner.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.flashButton.setOnClickListener { flashOn = !flashOn; binding.flashButton.text = if(flashOn) "⚡ Bật" else "⚡" }
        binding.galleryButton.setOnClickListener { Toast.makeText(requireContext(), "Chọn ảnh sẽ khả dụng khi tích hợp bộ chọn ảnh", Toast.LENGTH_LONG).show() }
        binding.captureReceipt.setOnClickListener { reviewReceipt() }
        binding.manualReceipt.setOnClickListener { reviewReceipt() }
        binding.aiReceipt.setOnClickListener { Toast.makeText(requireContext(), "Nhận dạng AI chưa được kết nối; bạn có thể nhập thủ công.", Toast.LENGTH_LONG).show(); reviewReceipt() }
    }
    private fun reviewReceipt() {
        val form = layoutInflater.inflate(com.example.spendly.R.layout.dialog_receipt_review, null)
        val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext()).setView(form).create()
        dialog.show()
        dialog.findViewById<View>(com.example.spendly.R.id.rescan_receipt)?.setOnClickListener { dialog.dismiss() }
        dialog.findViewById<View>(com.example.spendly.R.id.save_receipt)?.setOnClickListener { Toast.makeText(requireContext(), "Đã lưu giao dịch hóa đơn (bản demo)", Toast.LENGTH_SHORT).show(); dialog.dismiss(); parentFragmentManager.popBackStack() }
    }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

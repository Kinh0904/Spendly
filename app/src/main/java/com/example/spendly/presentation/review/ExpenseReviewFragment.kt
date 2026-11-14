package com.example.spendly.presentation.review

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.spendly.databinding.FragmentExpenseReviewBinding

class ExpenseReviewFragment : Fragment() {
    private var _binding: FragmentExpenseReviewBinding? = null
    private val binding get() = _binding!!
    private var rating = ""
    private val reasons = mutableSetOf<String>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentExpenseReviewBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        listOf(binding.ratingGood, binding.ratingOkay, binding.ratingRegret).forEachIndexed { i, b -> b.setOnClickListener { rating = listOf("Đáng", "Tạm được", "Hối tiếc")[i]; b.isChecked = true } }
        listOf(binding.reasonImpulse, binding.reasonUnneeded, binding.reasonExpensive, binding.reasonTired, binding.reasonFriends, binding.reasonOther).forEach { b -> b.setOnClickListener { if(!reasons.add(b.text.toString())) reasons.remove(b.text.toString()); b.isChecked = b.text.toString() in reasons } }
        binding.skipReview.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.saveReview.setOnClickListener { if(rating.isBlank()) android.widget.Toast.makeText(requireContext(), "Hãy chọn cảm nhận của bạn", android.widget.Toast.LENGTH_SHORT).show() else { binding.reviewForm.visibility = View.GONE; binding.reviewSuccess.visibility = View.VISIBLE } }
        binding.viewJournal.setOnClickListener { android.widget.Toast.makeText(requireContext(), "Nhật ký hối tiếc đã ghi nhận", android.widget.Toast.LENGTH_SHORT).show(); parentFragmentManager.popBackStack() }
        binding.closeReview.setOnClickListener { parentFragmentManager.popBackStack() }
    }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

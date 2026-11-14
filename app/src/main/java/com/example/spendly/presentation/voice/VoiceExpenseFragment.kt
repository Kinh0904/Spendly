package com.example.spendly.presentation.voice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.spendly.databinding.FragmentVoiceExpenseBinding
import com.example.spendly.presentation.transaction.add.ExpenseEntryFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class VoiceExpenseFragment : Fragment() {
    private var _binding: FragmentVoiceExpenseBinding? = null
    private val binding get() = _binding!!
    private val micPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if(granted) startRecognitionMock() else showPermissionDenied() }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View { _binding = FragmentVoiceExpenseBinding.inflate(inflater, container, false); return binding.root }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.navBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.startVoice.setOnClickListener { if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) startRecognitionMock() else MaterialAlertDialogBuilder(requireContext()).setTitle("Cho phép dùng micro").setMessage("Spendly dùng giọng nói để tách số tiền và danh mục; không lưu bản ghi âm.").setNegativeButton("Để sau", null).setPositiveButton("Cho phép") { _, _ -> micPermission.launch(Manifest.permission.RECORD_AUDIO) }.show() }
        binding.tryAgain.setOnClickListener { binding.voiceError.visibility = View.GONE; binding.voiceReady.visibility = View.VISIBLE }
        binding.manualEntry.setOnClickListener { (requireActivity() as com.example.spendly.MainActivity).navigateToFragment(ExpenseEntryFragment()) }
        binding.repeatVoice.setOnClickListener { binding.voiceReview.visibility = View.GONE; binding.voiceReady.visibility = View.VISIBLE }
        binding.saveVoice.setOnClickListener { android.widget.Toast.makeText(requireContext(), "Đã lưu khoản chi 45.000 đ (bản demo)", android.widget.Toast.LENGTH_SHORT).show(); parentFragmentManager.popBackStack() }
        binding.voiceExample.setOnClickListener { MaterialAlertDialogBuilder(requireContext()).setTitle("Bạn có thể nói…").setMessage("Ăn sáng bốn lăm nghìn → 45.000 đ\nCà phê 30k → 30.000 đ\nMột củ rưỡi tiền trọ → 1.500.000 đ").setPositiveButton("Đóng", null).show() }
    }
    private fun startRecognitionMock() { binding.voiceReady.visibility = View.GONE; binding.voiceError.visibility = View.GONE; binding.voiceReview.visibility = View.VISIBLE; binding.transcript.text = "ăn sáng bốn lăm nghìn" }
    private fun showPermissionDenied() { binding.voiceReady.visibility = View.GONE; binding.voiceError.visibility = View.VISIBLE }
    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

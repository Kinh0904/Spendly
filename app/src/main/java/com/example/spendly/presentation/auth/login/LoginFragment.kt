package com.example.spendly.presentation.auth.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spendly.R
import com.example.spendly.databinding.FragmentLoginBinding
import com.example.spendly.presentation.home.HomeFragment

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        // Main Login Action
        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                performLoginSuccess()
            }
        }

        // Social Login Placeholders
        binding.btnGoogleLogin.setOnClickListener {
            Toast.makeText(requireContext(), "Đăng nhập bằng Google", Toast.LENGTH_SHORT).show()
        }

        binding.btnFacebookLogin.setOnClickListener {
            Toast.makeText(requireContext(), "Đăng nhập bằng Facebook", Toast.LENGTH_SHORT).show()
        }

        // Forgot Password Action
        binding.tvForgotPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Tính năng Quên mật khẩu", Toast.LENGTH_SHORT).show()
        }

        // Register Action
        binding.tvRegisterNow.setOnClickListener {
            Toast.makeText(requireContext(), "Chuyển sang màn hình Đăng ký", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateInputs(): Boolean {
        binding.tilEmailOrPhone.error = null
        binding.tilPassword.error = null

        val input = binding.etEmailOrPhone.text?.toString()?.trim().orEmpty()
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (input.isEmpty()) {
            binding.tilEmailOrPhone.error = getString(R.string.err_empty_email)
            binding.etEmailOrPhone.requestFocus()
            return false
        }

        if (password.isEmpty()) {
            binding.tilPassword.error = getString(R.string.err_empty_password)
            binding.etPassword.requestFocus()
            return false
        }

        if (password.length < 6) {
            binding.tilPassword.error = getString(R.string.err_short_password)
            binding.etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLoginSuccess() {
        Toast.makeText(requireContext(), getString(R.string.msg_login_success), Toast.LENGTH_SHORT).show()

        // Navigate to HomeFragment and replace LoginFragment without backstack entry
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
            )
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.spendly.presentation.settings

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.spendly.R
import com.example.spendly.databinding.FragmentSettingsBinding
import com.example.spendly.utils.ThemeManager

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        com.example.spendly.utils.NavigationHelper.setupBottomToolbar(
            binding.root,
            com.example.spendly.utils.NavigationHelper.Destination.SETTINGS
        )
        setupThemeSelector()
        setupAccentSelector()
        setupShortcutListeners()
    }

    private fun setupThemeSelector() {
        val currentMode = ThemeManager.getThemeMode(requireContext())
        highlightModePill(currentMode)

        binding.btnModeLight.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.MODE_LIGHT)
            highlightModePill(ThemeManager.MODE_LIGHT)
        }

        binding.btnModeDark.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.MODE_DARK)
            highlightModePill(ThemeManager.MODE_DARK)
        }

        binding.btnModeSystem.setOnClickListener {
            ThemeManager.setThemeMode(requireContext(), ThemeManager.MODE_SYSTEM)
            highlightModePill(ThemeManager.MODE_SYSTEM)
        }
    }

    private fun highlightModePill(mode: Int) {
        resetPill(binding.btnModeLight)
        resetPill(binding.btnModeDark)
        resetPill(binding.btnModeSystem)

        val activeView = when (mode) {
            ThemeManager.MODE_LIGHT -> binding.btnModeLight
            ThemeManager.MODE_DARK -> binding.btnModeDark
            else -> binding.btnModeSystem
        }

        activeView.setBackgroundResource(R.drawable.bg_statistics_pill_active)
        activeView.setTextColor(Color.parseColor("#27243A"))
        activeView.setTypeface(null, android.graphics.Typeface.BOLD)
    }

    private fun resetPill(tv: TextView) {
        tv.background = null
        tv.setTextColor(Color.parseColor("#7A768F"))
        tv.setTypeface(null, android.graphics.Typeface.NORMAL)
    }

    private fun setupAccentSelector() {
        binding.btnAccentPurple.setOnClickListener {
            ThemeManager.setAccentColor(requireContext(), ThemeManager.ACCENT_PURPLE)
            Toast.makeText(requireContext(), "Đã chọn màu Tím", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccentBlue.setOnClickListener {
            ThemeManager.setAccentColor(requireContext(), ThemeManager.ACCENT_BLUE)
            Toast.makeText(requireContext(), "Đã chọn màu Xanh dương", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccentGreen.setOnClickListener {
            ThemeManager.setAccentColor(requireContext(), ThemeManager.ACCENT_GREEN)
            Toast.makeText(requireContext(), "Đã chọn màu Xanh lá", Toast.LENGTH_SHORT).show()
        }

        binding.btnAccentOrange.setOnClickListener {
            ThemeManager.setAccentColor(requireContext(), ThemeManager.ACCENT_ORANGE)
            Toast.makeText(requireContext(), "Đã chọn màu Cam", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupShortcutListeners() {
        binding.btnCategoryManagement.setOnClickListener {
            (activity as? com.example.spendly.MainActivity)?.navigateToFragment(com.example.spendly.presentation.category.CategoryFragment())
        }

        binding.btnBudgetSettings.setOnClickListener {
            (activity as? com.example.spendly.MainActivity)?.navigateToFragment(com.example.spendly.presentation.transaction.add.BudgetFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package com.example.spendly.utils

import android.view.View
import com.example.spendly.MainActivity
import com.example.spendly.R
import com.example.spendly.presentation.history.HistoryFragment
import com.example.spendly.presentation.home.HomeFragment
import com.example.spendly.presentation.settings.SettingsFragment
import com.example.spendly.presentation.statistics.StatisticsFragment
import com.example.spendly.presentation.category.CategoryFragment
import com.example.spendly.presentation.transaction.add.BudgetFragment
import com.example.spendly.presentation.assistant.AssistantFragment
import com.example.spendly.presentation.receipt.ReceiptFragment
import com.example.spendly.presentation.transaction.add.ExpenseEntryFragment
import com.example.spendly.presentation.voice.VoiceExpenseFragment

object NavigationHelper {

    fun setupBottomToolbar(rootView: View, currentDestination: Destination) {
        val navHome = rootView.findViewById<View>(R.id.nav_home)
        val navHistory = rootView.findViewById<View>(R.id.nav_history)
        val navAdd = rootView.findViewById<View>(R.id.nav_add)
        val navStatistics = rootView.findViewById<View>(R.id.nav_statistics)
        val navSettings = rootView.findViewById<View>(R.id.nav_settings)

        navHome?.setOnClickListener {
            if (currentDestination != Destination.HOME) {
                (rootView.context as? MainActivity)?.navigateToFragment(HomeFragment())
            }
        }

        navHistory?.setOnClickListener {
            if (currentDestination != Destination.HISTORY) {
                (rootView.context as? MainActivity)?.navigateToFragment(HistoryFragment())
            }
        }

        navAdd?.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(rootView.context)
                .setTitle("Thêm khoản chi")
                .setItems(arrayOf("Nhập thủ công", "Ghi bằng giọng nói", "Quét hóa đơn")) { _, which ->
                    val host = rootView.context as? MainActivity ?: return@setItems
                    when (which) {
                        0 -> host.navigateToFragment(ExpenseEntryFragment())
                        1 -> host.navigateToFragment(VoiceExpenseFragment())
                        else -> host.navigateToFragment(ReceiptFragment())
                    }
                }.show()
        }

        navStatistics?.setOnClickListener {
            if (currentDestination != Destination.STATISTICS) {
                (rootView.context as? MainActivity)?.navigateToFragment(StatisticsFragment())
            }
        }

        navSettings?.setOnClickListener {
            if (currentDestination != Destination.SETTINGS) {
                (rootView.context as? MainActivity)?.navigateToFragment(SettingsFragment())
            }
        }
    }

    enum class Destination {
        HOME, HISTORY, STATISTICS, SETTINGS
    }
}

package com.belajar.submissionawal.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.belajar.submissionawal.data.local.datastore.dataStore
import com.belajar.submissionawal.data.worker.DailyReminderWorker
import com.belajar.submissionawal.databinding.FragmentSettingBinding
import com.belajar.submissionawal.ui.ViewModelFactory
import java.util.concurrent.TimeUnit

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext(), com.belajar.submissionawal.data.local.datastore.SettingPreferences.getInstance(requireContext().dataStore))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                binding.switchTheme.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                binding.switchTheme.isChecked = false
            }
        }

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveThemeSetting(isChecked)
        }

        viewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive ->
            binding.switchReminder.isChecked = isReminderActive
        }

        binding.switchReminder.setOnCheckedChangeListener { _, isChecked ->
            viewModel.saveReminderSetting(isChecked)
            if (isChecked) {
                startDailyReminder()
            } else {
                cancelDailyReminder()
            }
        }
    }

    private fun startDailyReminder() {
        val workManager = WorkManager.getInstance(requireContext())
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, TimeUnit.DAYS)
            .setConstraints(constraints)
            .addTag("daily_reminder")
            .build()
        workManager.enqueueUniquePeriodicWork(
            "daily_reminder_work",
            androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
            dailyReminderRequest
        )
    }

    private fun cancelDailyReminder() {
        val workManager = WorkManager.getInstance(requireContext())
        workManager.cancelAllWorkByTag("daily_reminder")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

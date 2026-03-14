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

        binding.apply {
            viewModel.getThemeSettings().observe(viewLifecycleOwner) { isDarkModeActive ->
                if (isDarkModeActive) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    switchTheme.isChecked = true
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    switchTheme.isChecked = false
                }
            }

            switchTheme.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveThemeSetting(isChecked)
            }

            viewModel.getReminderSetting().observe(viewLifecycleOwner) { isReminderActive ->
                switchReminder.isChecked = isReminderActive
                layoutTimePicker.visibility = if (isReminderActive) View.VISIBLE else View.GONE
            }

            switchReminder.setOnCheckedChangeListener { _, isChecked ->
                viewModel.saveReminderSetting(isChecked)
                if (isChecked) {
                    viewModel.getReminderTime().value?.let { startDailyReminder(it) }
                } else {
                    cancelDailyReminder()
                }
            }

            viewModel.getReminderTime().observe(viewLifecycleOwner) { time ->
                tvReminderTime.text = time
                if (switchReminder.isChecked) {
                    startDailyReminder(time)
                }
            }

            tvReminderTime.setOnClickListener {
                showTimePicker()
            }
        }
    }

    private fun showTimePicker() {
        val currentTime = viewModel.getReminderTime().value ?: "09:00"
        val parts = currentTime.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val picker = com.google.android.material.timepicker.MaterialTimePicker.Builder()
            .setTimeFormat(com.google.android.material.timepicker.TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Pilih Waktu Pengingat")
            .build()

        picker.addOnPositiveButtonClickListener {
            val newTime = String.format("%02d:%02d", picker.hour, picker.minute)
            viewModel.saveReminderTime(newTime)
        }

        picker.show(childFragmentManager, "TimePicker")
    }

    private fun startDailyReminder(time: String) {
        val parts = time.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()

        val calendar = java.util.Calendar.getInstance()
        val now = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour)
        calendar.set(java.util.Calendar.MINUTE, minute)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        if (calendar.before(now)) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = calendar.timeInMillis - now.timeInMillis

        val workManager = WorkManager.getInstance(requireContext())
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val dailyReminderRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(1, java.util.concurrent.TimeUnit.DAYS)
            .setInitialDelay(initialDelay, java.util.concurrent.TimeUnit.MILLISECONDS)
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
